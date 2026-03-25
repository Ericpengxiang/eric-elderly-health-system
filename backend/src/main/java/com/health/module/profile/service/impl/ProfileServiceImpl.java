package com.health.module.profile.service.impl;

import com.health.common.BusinessException;
import com.health.common.enums.RoleEnum;
import com.health.module.profile.dto.ProfileVO;
import com.health.module.profile.entity.HealthProfile;
import com.health.module.profile.repository.HealthProfileRepository;
import com.health.module.profile.service.ProfileService;
import com.health.security.ElderAccessService;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final HealthProfileRepository healthProfileRepository;
    private final ElderAccessService elderAccessService;

    @Override
    public ProfileVO getByElderUserId(Long elderUserId) {
        LoginUser login = SecurityUtils.requireLogin();
        Long uid = elderUserId != null ? elderUserId : login.getUserId();
        elderAccessService.assertCanAccessElder(login, uid);
        HealthProfile p = healthProfileRepository.findByUserId(uid)
                .orElseThrow(() -> new BusinessException("健康档案不存在"));
        return toVo(p);
    }

    @Override
    @Transactional
    public HealthProfile saveOrUpdate(Long elderUserId, ProfileVO vo) {
        LoginUser login = SecurityUtils.requireLogin();
        Long uid = elderUserId != null ? elderUserId : login.getUserId();
        if (login.getRole() != RoleEnum.ADMIN && login.getRole() != RoleEnum.DOCTOR) {
            if (!uid.equals(login.getUserId())) {
                throw new BusinessException(403, "仅能修改本人档案");
            }
        }
        elderAccessService.assertCanAccessElder(login, uid);
        HealthProfile p = healthProfileRepository.findByUserId(uid).orElse(new HealthProfile());
        p.setUserId(uid);
        p.setIdCard(vo.getIdCard());
        p.setBirthDate(vo.getBirthDate());
        p.setGender(vo.getGender());
        p.setBloodType(vo.getBloodType());
        p.setAddress(vo.getAddress());
        p.setEmergencyContact(vo.getEmergencyContact());
        p.setEmergencyPhone(vo.getEmergencyPhone());
        p.setMedicalHistory(vo.getMedicalHistory());
        p.setAllergyHistory(vo.getAllergyHistory());
        if (p.getCreateTime() == null) {
            p.setCreateTime(LocalDateTime.now());
        }
        return healthProfileRepository.save(p);
    }

    private ProfileVO toVo(HealthProfile p) {
        return ProfileVO.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .idCard(p.getIdCard())
                .birthDate(p.getBirthDate())
                .gender(p.getGender())
                .bloodType(p.getBloodType())
                .address(p.getAddress())
                .emergencyContact(p.getEmergencyContact())
                .emergencyPhone(p.getEmergencyPhone())
                .medicalHistory(p.getMedicalHistory())
                .allergyHistory(p.getAllergyHistory())
                .build();
    }
}
