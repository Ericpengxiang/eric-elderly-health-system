package com.health.module.assessment.engine;

import cn.hutool.json.JSONUtil;
import com.health.common.enums.RiskLevelEnum;
import com.health.module.assessment.entity.RiskAssessment;
import com.health.module.assessment.repository.RiskAssessmentRepository;
import com.health.module.profile.entity.HealthProfile;
import com.health.module.profile.repository.HealthProfileRepository;
import com.health.module.vitalsign.repository.VitalSignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * 本地规则引擎：年龄、近30天异常体征次数、病史关键词加权，生成风险等级与建议文本
 */
@Component
@RequiredArgsConstructor
public class LocalRiskEngine {

    private final HealthProfileRepository healthProfileRepository;
    private final VitalSignRepository vitalSignRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;

    public RiskAssessment evaluateAndPersist(Long userId) {
        HealthProfile profile = healthProfileRepository.findByUserId(userId).orElse(null);
        int age = 70;
        if (profile != null && profile.getBirthDate() != null) {
            age = Period.between(profile.getBirthDate(), java.time.LocalDate.now()).getYears();
        }
        LocalDateTime from = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        long abnormalCnt = vitalSignRepository.countByUserIdAndIsAbnormalAndRecordTimeAfter(userId, 1, from);

        double score = 0.3;
        if (age >= 75) {
            score += 0.15;
        }
        score += Math.min(0.4, abnormalCnt * 0.08);
        if (profile != null && profile.getMedicalHistory() != null) {
            String mh = profile.getMedicalHistory();
            if (mh.contains("糖尿病") || mh.contains("高血压") || mh.contains("冠心病")) {
                score += 0.15;
            }
        }
        score = Math.min(1.0, score);

        RiskLevelEnum level;
        if (score >= 0.65) {
            level = RiskLevelEnum.HIGH;
        } else if (score >= 0.4) {
            level = RiskLevelEnum.MEDIUM;
        } else {
            level = RiskLevelEnum.LOW;
        }

        java.util.Map<String, Object> diseaseMap = new java.util.HashMap<>();
        diseaseMap.put("综合风险指数", round2(score));
        diseaseMap.put("近30天异常体征次数", abnormalCnt);
        String diseaseJson = JSONUtil.toJsonStr(diseaseMap);

        String healthAdvice = String.format(
                "根据本地规则引擎：您今年约 %d 岁，近30天异常体征记录 %d 条。综合评分 %.2f，当前风险等级为 %s。请遵医嘱用药并定期复查。",
                age, abnormalCnt, score, level.name());

        String diet = "低盐低脂、控制精制糖摄入，多蔬菜与优质蛋白。";
        String exercise = "建议每日步行30分钟左右，避免剧烈运动。";

        RiskAssessment ra = new RiskAssessment();
        ra.setUserId(userId);
        ra.setAssessmentTime(LocalDateTime.now());
        ra.setRiskLevel(level);
        ra.setDiseaseRisks(diseaseJson);
        ra.setHealthAdvice(healthAdvice);
        ra.setDietAdvice(diet);
        ra.setExerciseAdvice(exercise);
        ra.setRawResponse("LOCAL_RULE_ENGINE_V1");
        ra.setAccuracyScore(BigDecimal.valueOf(0.75));
        ra.setCreateTime(LocalDateTime.now());
        return riskAssessmentRepository.save(ra);
    }

    private static double round2(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
