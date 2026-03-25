package com.health.module.user.controller;

import com.health.common.Result;
import com.health.module.user.dto.UserVO;
import com.health.module.user.entity.SysUser;
import com.health.module.user.repository.UserRepository;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 当前用户与管理员用户查询
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public Result<UserVO> me() {
        LoginUser lu = SecurityUtils.requireLogin();
        SysUser u = userRepository.findById(lu.getUserId()).orElseThrow();
        return Result.ok(toVo(u));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<UserVO>> listAll() {
        List<UserVO> list = userRepository.findAll().stream().map(this::toVo).collect(Collectors.toList());
        return Result.ok(list);
    }

    private UserVO toVo(SysUser u) {
        return UserVO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .realName(u.getRealName())
                .phone(u.getPhone())
                .email(u.getEmail())
                .role(u.getRole())
                .avatar(u.getAvatar())
                .build();
    }
}
