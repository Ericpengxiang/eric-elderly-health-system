package com.health.module.alert.service.impl;

import cn.hutool.json.JSONUtil;
import com.health.common.BusinessException;
import com.health.common.enums.AlertLevelEnum;
import com.health.common.enums.RoleEnum;
import com.health.module.alert.entity.AlertRecord;
import com.health.module.alert.repository.AlertRecordRepository;
import com.health.module.alert.service.AlertService;
import com.health.module.family.entity.FamilyBind;
import com.health.module.family.repository.FamilyBindRepository;
import com.health.module.notify.AsyncMailService;
import com.health.module.user.entity.SysUser;
import com.health.module.user.repository.UserRepository;
import com.health.module.vitalsign.entity.VitalSign;
import com.health.security.ElderAccessService;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import com.health.websocket.AlertMessage;
import com.health.websocket.AlertPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 预警创建、查询、处理；联动通知家属邮件与 WebSocket 广播
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRecordRepository alertRecordRepository;
    private final FamilyBindRepository familyBindRepository;
    private final UserRepository userRepository;
    private final AlertPushService alertPushService;
    private final AsyncMailService asyncMailService;
    private final ElderAccessService elderAccessService;

    @Override
    @Transactional
    public AlertRecord createFromVitalSign(VitalSign vital, String alertType, String content, AlertLevelEnum level) {
        AlertRecord a = new AlertRecord();
        a.setUserId(vital.getUserId());
        a.setVitalSignId(vital.getId());
        a.setAlertType(alertType);
        a.setAlertLevel(level);
        a.setAlertContent(content);
        a.setIsRead(0);
        a.setIsHandled(0);
        a.setCreateTime(LocalDateTime.now());
        List<Long> notifyIds = new ArrayList<>();
        List<FamilyBind> binds = familyBindRepository.findByElderIdAndBindStatus(vital.getUserId(), 1);
        for (FamilyBind b : binds) {
            notifyIds.add(b.getFamilyId());
        }
        List<SysUser> doctors = userRepository.findByRole(RoleEnum.DOCTOR);
        for (SysUser d : doctors) {
            notifyIds.add(d.getId());
        }
        a.setNotifyUserIds(JSONUtil.toJsonStr(notifyIds));
        alertRecordRepository.save(a);

        AlertMessage msg = AlertMessage.builder()
                .alertId(a.getId())
                .elderUserId(vital.getUserId())
                .alertType(alertType)
                .alertLevel(level.name())
                .content(content)
                .timestamp(System.currentTimeMillis())
                .build();
        alertPushService.broadcast(msg);

        SysUser elder = userRepository.findById(vital.getUserId()).orElse(null);
        String elderName = elder != null && elder.getRealName() != null ? elder.getRealName() : "老人";
        for (FamilyBind b : binds) {
            userRepository.findById(b.getFamilyId()).ifPresent(fu -> {
                if (fu.getEmail() != null && !fu.getEmail().isBlank()) {
                    asyncMailService.sendAlertEmail(fu.getEmail(),
                            "【健康预警】" + elderName,
                            content);
                }
            });
        }
        return a;
    }

    @Override
    public List<AlertRecord> listForCurrentUser(Long elderUserId) {
        LoginUser login = SecurityUtils.requireLogin();
        RoleEnum role = login.getRole();
        if (role == RoleEnum.FAMILY && elderUserId == null) {
            List<AlertRecord> merged = new ArrayList<>();
            for (FamilyBind b : familyBindRepository.findByFamilyIdAndBindStatus(login.getUserId(), 1)) {
                merged.addAll(alertRecordRepository.findByUserIdOrderByCreateTimeDesc(b.getElderId()));
            }
            merged.sort(Comparator.comparing(AlertRecord::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            return merged;
        }
        if (elderUserId == null) {
            elderUserId = login.getUserId();
        }
        elderAccessService.assertCanAccessElder(login, elderUserId);
        return alertRecordRepository.findByUserIdOrderByCreateTimeDesc(elderUserId);
    }

    @Override
    @Transactional
    public void markHandled(Long alertId, String remark) {
        LoginUser login = SecurityUtils.requireLogin();
        if (login.getRole() != RoleEnum.DOCTOR && login.getRole() != RoleEnum.ADMIN) {
            throw new BusinessException(403, "仅医生或管理员可处理预警");
        }
        AlertRecord a = alertRecordRepository.findById(alertId)
                .orElseThrow(() -> new BusinessException("预警不存在"));
        a.setIsHandled(1);
        a.setHandlerId(login.getUserId());
        a.setHandleRemark(remark);
    }

    @Override
    @Transactional
    public void markRead(Long alertId) {
        LoginUser login = SecurityUtils.requireLogin();
        AlertRecord a = alertRecordRepository.findById(alertId)
                .orElseThrow(() -> new BusinessException("预警不存在"));
        elderAccessService.assertCanAccessElder(login, a.getUserId());
        a.setIsRead(1);
    }
}
