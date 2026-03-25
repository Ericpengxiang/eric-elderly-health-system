package com.health.module.family.controller;

import com.health.common.BusinessException;
import com.health.common.Result;
import com.health.common.enums.RoleEnum;
import com.health.module.family.entity.FamilyBind;
import com.health.module.family.repository.FamilyBindRepository;
import com.health.module.user.dto.UserVO;
import com.health.module.user.entity.SysUser;
import com.health.module.user.repository.UserRepository;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 家属端：查看已绑定老人列表
 */
@RestController
@RequestMapping("/family")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyBindRepository familyBindRepository;
    private final UserRepository userRepository;

    @GetMapping("/elders")
    public Result<List<UserVO>> boundElders() {
        LoginUser login = SecurityUtils.requireLogin();
        if (login.getRole() != RoleEnum.FAMILY && login.getRole() != RoleEnum.ADMIN) {
            throw new BusinessException(403, "仅家属或管理员可访问此接口");
        }
        List<FamilyBind> binds = familyBindRepository.findByFamilyIdAndBindStatus(login.getUserId(), 1);
        List<UserVO> vos = binds.stream()
                .map(b -> userRepository.findById(b.getElderId()).orElse(null))
                .filter(u -> u != null)
                .map(u -> UserVO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .realName(u.getRealName())
                        .phone(u.getPhone())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .avatar(u.getAvatar())
                        .build())
                .collect(Collectors.toList());
        return Result.ok(vos);
    }
}
