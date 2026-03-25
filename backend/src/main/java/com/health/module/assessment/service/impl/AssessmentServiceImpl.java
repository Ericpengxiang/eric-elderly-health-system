package com.health.module.assessment.service.impl;

import com.health.module.assessment.engine.LocalRiskEngine;
import com.health.module.assessment.entity.RiskAssessment;
import com.health.module.assessment.feign.AssessmentFeignClient;
import com.health.module.assessment.repository.RiskAssessmentRepository;
import com.health.module.assessment.service.AssessmentService;
import com.health.module.profile.repository.HealthProfileRepository;
import com.health.module.vitalsign.repository.VitalSignRepository;
import com.health.security.ElderAccessService;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 先尝试 Feign 远程评估，失败或空结果则使用本地规则引擎；结果写入 Redis 缓存 24 小时
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private static final String CACHE_PREFIX = "risk:assess:";

    private final AssessmentFeignClient assessmentFeignClient;
    private final LocalRiskEngine localRiskEngine;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final ElderAccessService elderAccessService;
    private final HealthProfileRepository healthProfileRepository;
    private final VitalSignRepository vitalSignRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public RiskAssessment runAssessment(Long elderUserId) {
        LoginUser login = SecurityUtils.requireLogin();
        Long uid = elderUserId != null ? elderUserId : login.getUserId();
        elderAccessService.assertCanAccessElder(login, uid);

        String cacheKey = CACHE_PREFIX + uid;
        try {
            String cachedId = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cachedId != null) {
                return riskAssessmentRepository.findById(Long.parseLong(cachedId)).orElseGet(() -> runFresh(uid));
            }
        } catch (Exception e) {
            log.warn("读取风险评估缓存失败，将重新计算: {}", e.getMessage());
        }
        return runFresh(uid);
    }

    private RiskAssessment runFresh(Long uid) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", uid);
        healthProfileRepository.findByUserId(uid).ifPresent(p -> payload.put("medicalHistory", p.getMedicalHistory()));
        payload.put("recentVitals", vitalSignRepository.findByUserIdAndRecordTimeAfter(uid,
                java.time.LocalDateTime.now().minusDays(30)));

        try {
            Map<String, Object> remote = assessmentFeignClient.evaluate(payload);
            if (remote != null && !remote.isEmpty()) {
                log.debug("第三方接口返回键: {}", remote.keySet());
            }
        } catch (Exception e) {
            log.warn("第三方风险评估调用失败，将使用本地规则引擎: {}", e.getMessage());
        }
        RiskAssessment saved = localRiskEngine.evaluateAndPersist(uid);
        try {
            stringRedisTemplate.opsForValue().set(CACHE_PREFIX + uid, String.valueOf(saved.getId()),
                    Duration.ofHours(24));
        } catch (Exception e) {
            log.warn("写入风险评估缓存失败: {}", e.getMessage());
        }
        return saved;
    }

    @Override
    public List<RiskAssessment> list(Long elderUserId) {
        LoginUser login = SecurityUtils.requireLogin();
        Long uid = elderUserId != null ? elderUserId : login.getUserId();
        elderAccessService.assertCanAccessElder(login, uid);
        return riskAssessmentRepository.findByUserIdOrderByAssessmentTimeDesc(uid);
    }
}
