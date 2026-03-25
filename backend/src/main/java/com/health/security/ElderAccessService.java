package com.health.security;

import com.health.common.BusinessException;
import com.health.common.enums.RoleEnum;
import com.health.module.family.repository.FamilyBindRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 判断当前用户是否可访问某位老年人相关数据（档案、体征等）
 */
@Service
@RequiredArgsConstructor
public class ElderAccessService {

    private final FamilyBindRepository familyBindRepository;

    public void assertCanAccessElder(LoginUser login, Long elderUserId) {
        if (!canAccessElder(login, elderUserId)) {
            throw new BusinessException(403, "无权查看该老人数据");
        }
    }

    public boolean canAccessElder(LoginUser login, Long elderUserId) {
        if (elderUserId == null) {
            return false;
        }
        RoleEnum role = login.getRole();
        if (role == RoleEnum.ADMIN) {
            return true;
        }
        if (role == RoleEnum.DOCTOR) {
            return true;
        }
        if (role == RoleEnum.ELDER && Objects.equals(login.getUserId(), elderUserId)) {
            return true;
        }
        if (role == RoleEnum.FAMILY) {
            return familyBindRepository.existsByElderIdAndFamilyIdAndBindStatus(
                    elderUserId, login.getUserId(), 1);
        }
        return false;
    }
}
