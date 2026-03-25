package com.health.module.user.controller;

import com.health.common.BusinessException;
import com.health.common.Result;
import com.health.common.enums.RoleEnum;
import com.health.module.user.dto.UserVO;
import com.health.module.user.entity.SysUser;
import com.health.module.user.repository.UserRepository;
import com.health.security.LoginUser;
import com.health.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 当前用户与管理员用户查询、管理
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

    /** 管理员：获取所有用户列表 */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<UserVO>> listAll() {
        List<UserVO> list = userRepository.findAll().stream().map(this::toVo).collect(Collectors.toList());
        return Result.ok(list);
    }

    /** 按角色获取用户列表（如获取所有医生供预约选择） */
    @GetMapping("/by-role")
    public Result<List<UserVO>> listByRole(@RequestParam RoleEnum role) {
        List<UserVO> list = userRepository.findByRole(role).stream().map(this::toVo).collect(Collectors.toList());
        return Result.ok(list);
    }

    /** 管理员：启用/禁用用户 */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser u = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        u.setStatus(status);
        u.setUpdateTime(LocalDateTime.now());
        userRepository.save(u);
        return Result.ok(null);
    }

    /** 管理员：修改用户角色 */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateRole(@PathVariable Long id, @RequestParam RoleEnum role) {
        SysUser u = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        u.setRole(role);
        u.setUpdateTime(LocalDateTime.now());
        userRepository.save(u);
        return Result.ok(null);
    }

    private UserVO toVo(SysUser u) {
        return UserVO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .realName(u.getRealName())
                .phone(u.getPhone())
                .email(u.getEmail())
                .role(u.getRole())
                .status(u.getStatus())
                .avatar(u.getAvatar())
                .build();
    }
}
