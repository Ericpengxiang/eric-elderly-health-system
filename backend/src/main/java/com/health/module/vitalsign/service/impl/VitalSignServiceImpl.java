package com.health.module.vitalsign.service.impl;

import com.health.common.BusinessException;
import com.health.common.constant.ThresholdConstants;
import com.health.common.enums.AlertLevelEnum;
import com.health.common.enums.RoleEnum;
import com.health.common.enums.SignTypeEnum;
import com.health.module.alert.service.AlertService;
import com.health.module.vitalsign.dto.VitalSignAddDTO;
import com.health.module.vitalsign.entity.VitalSign;
import com.health.module.vitalsign.repository.VitalSignRepository;
import com.health.module.vitalsign.service.VitalSignService;
import com.health.security.ElderAccessService;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 体征录入与阈值判定，异常时生成预警
 */
@Service
@RequiredArgsConstructor
public class VitalSignServiceImpl implements VitalSignService {

    private final VitalSignRepository vitalSignRepository;
    private final AlertService alertService;
    private final ElderAccessService elderAccessService;

    @Override
    @Transactional
    public VitalSign add(VitalSignAddDTO dto) {
        LoginUser login = SecurityUtils.requireLogin();
        Long targetUserId = dto.getUserId() != null ? dto.getUserId() : login.getUserId();
        if (login.getRole() == RoleEnum.ELDER) {
            targetUserId = login.getUserId();
        }
        elderAccessService.assertCanAccessElder(login, targetUserId);

        VitalSign v = new VitalSign();
        v.setUserId(targetUserId);
        v.setSignType(dto.getSignType());
        v.setValueSystolic(dto.getValueSystolic());
        v.setValueDiastolic(dto.getValueDiastolic());
        v.setValueMain(dto.getValueMain());
        v.setUnit(dto.getUnit());
        v.setRecordTime(dto.getRecordTime() != null ? dto.getRecordTime() : LocalDateTime.now());
        v.setDeviceSource(dto.getDeviceSource());
        v.setRemark(dto.getRemark());
        v.setCreateTime(LocalDateTime.now());

        int abnormal = 0;
        SignTypeEnum st = dto.getSignType();
        if (st == SignTypeEnum.BLOOD_PRESSURE) {
            if (dto.getValueSystolic() == null || dto.getValueDiastolic() == null) {
                throw new BusinessException("血压需填写收缩压与舒张压");
            }
            int sys = dto.getValueSystolic().intValue();
            int dia = dto.getValueDiastolic().intValue();
            if (sys > ThresholdConstants.SYSTOLIC_MAX || sys < ThresholdConstants.SYSTOLIC_MIN
                    || dia > ThresholdConstants.DIASTOLIC_MAX) {
                abnormal = 1;
            }
        } else if (st == SignTypeEnum.BLOOD_SUGAR) {
            if (dto.getValueMain() == null) {
                throw new BusinessException("血糖需填写主值");
            }
            double val = dto.getValueMain().doubleValue();
            if (val > ThresholdConstants.BLOOD_SUGAR_MAX || val < ThresholdConstants.BLOOD_SUGAR_MIN) {
                abnormal = 1;
            }
        } else if (st == SignTypeEnum.HEART_RATE) {
            if (dto.getValueMain() == null) {
                throw new BusinessException("心率需填写主值");
            }
            int hr = dto.getValueMain().intValue();
            if (hr > ThresholdConstants.HEART_RATE_MAX || hr < ThresholdConstants.HEART_RATE_MIN) {
                abnormal = 1;
            }
        } else if (st == SignTypeEnum.BLOOD_OXYGEN) {
            if (dto.getValueMain() == null) {
                throw new BusinessException("血氧需填写主值");
            }
            double ox = dto.getValueMain().doubleValue();
            if (ox < ThresholdConstants.BLOOD_OXYGEN_MIN) {
                abnormal = 1;
            }
        } else if (st == SignTypeEnum.TEMPERATURE) {
            if (dto.getValueMain() == null) {
                throw new BusinessException("体温需填写主值");
            }
            double t = dto.getValueMain().doubleValue();
            if (t > ThresholdConstants.TEMPERATURE_MAX || t < ThresholdConstants.TEMPERATURE_MIN) {
                abnormal = 1;
            }
        }
        v.setIsAbnormal(abnormal);
        vitalSignRepository.save(v);

        if (abnormal == 1) {
            String type = st.name();
            AlertLevelEnum level = AlertLevelEnum.MEDIUM;
            String content = buildAlertContent(st, v);
            if (st == SignTypeEnum.BLOOD_PRESSURE) {
                int sys = v.getValueSystolic().intValue();
                if (sys >= 150 || v.getValueDiastolic().intValue() >= 95) {
                    level = AlertLevelEnum.HIGH;
                }
            }
            alertService.createFromVitalSign(v, type, content, level);
        }
        return v;
    }

    private String buildAlertContent(SignTypeEnum st, VitalSign v) {
        return switch (st) {
            case BLOOD_PRESSURE -> String.format("血压异常：收缩压%smmHg，舒张压%smmHg，请关注。",
                    v.getValueSystolic().setScale(0, RoundingMode.HALF_UP),
                    v.getValueDiastolic().setScale(0, RoundingMode.HALF_UP));
            case BLOOD_SUGAR -> String.format("血糖异常：%s mmol/L，请关注。",
                    v.getValueMain().setScale(1, RoundingMode.HALF_UP));
            case HEART_RATE -> String.format("心率异常：%s bpm，请关注。", v.getValueMain().intValue());
            case BLOOD_OXYGEN -> String.format("血氧偏低：%s%%，请关注。", v.getValueMain().setScale(1, RoundingMode.HALF_UP));
            case TEMPERATURE -> String.format("体温异常：%s℃，请关注。", v.getValueMain().setScale(1, RoundingMode.HALF_UP));
        };
    }

    @Override
    public List<VitalSign> list(Long elderUserId, String signType) {
        LoginUser login = SecurityUtils.requireLogin();
        Long uid = elderUserId != null ? elderUserId : login.getUserId();
        elderAccessService.assertCanAccessElder(login, uid);
        if (signType != null && !signType.isBlank()) {
            SignTypeEnum st = SignTypeEnum.valueOf(signType);
            return vitalSignRepository.findByUserIdAndSignTypeOrderByRecordTimeDesc(uid, st);
        }
        return vitalSignRepository.findByUserIdOrderByRecordTimeDesc(uid);
    }
}
