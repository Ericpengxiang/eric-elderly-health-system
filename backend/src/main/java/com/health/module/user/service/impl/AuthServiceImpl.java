package com.health.module.user.service.impl;

import com.health.common.BusinessException;
import com.health.common.enums.RoleEnum;
import com.health.config.JwtProperties;
import com.health.module.user.dto.LoginDTO;
import com.health.module.user.dto.LoginVO;
import com.health.module.user.dto.RefreshTokenDTO;
import com.health.module.user.dto.RegisterDTO;
import com.health.module.user.dto.UserVO;
import com.health.module.user.entity.SysUser;
import com.health.module.user.repository.UserRepository;
import com.health.module.user.service.AuthService;
import com.health.security.JwtUtil;
import com.health.security.LoginUser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 登录、注册、刷新令牌
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String REDIS_REFRESH_PREFIX = "refresh:";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public LoginVO login(LoginDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        LoginUser lu = (LoginUser) auth.getPrincipal();
        return issueTokens(lu);
    }

    @Override
    public LoginVO refresh(RefreshTokenDTO dto) {
        Claims claims;
        try {
            claims = jwtUtil.parseToken(dto.getRefreshToken());
        } catch (Exception e) {
            throw new BusinessException(401, "刷新令牌无效或已过期");
        }
        if (!JwtUtil.TYPE_REFRESH.equals(claims.get(JwtUtil.CLAIM_TYPE))) {
            throw new BusinessException(401, "非刷新令牌");
        }
        Long userId = ((Number) claims.get(JwtUtil.CLAIM_UID)).longValue();
        String jti = claims.get(JwtUtil.CLAIM_JTI, String.class);
        String key = REDIS_REFRESH_PREFIX + jti;
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached == null || !cached.equals(String.valueOf(userId))) {
            throw new BusinessException(401, "刷新令牌已失效，请重新登录");
        }
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(401, "用户不存在"));
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "账号已禁用");
        }
        LoginUser lu = new LoginUser(user);
        stringRedisTemplate.delete(key);
        return issueTokens(lu);
    }

    private LoginVO issueTokens(LoginUser lu) {
        String access = jwtUtil.createAccessToken(lu.getUserId(), lu.getUsername(), lu.getRole().name());
        String refresh = jwtUtil.createRefreshToken(lu.getUserId(), lu.getUsername());
        String jti = jwtUtil.extractJti(refresh);
        long days = jwtProperties.getRefreshTokenExpireDays();
        stringRedisTemplate.opsForValue().set(
                REDIS_REFRESH_PREFIX + jti,
                String.valueOf(lu.getUserId()),
                Duration.ofDays(days));
        long expSec = jwtProperties.getAccessTokenExpireMinutes() * 60;
        UserVO vo = toUserVO(lu);
        return LoginVO.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .expiresIn(expSec)
                .user(vo)
                .build();
    }

    private UserVO toUserVO(LoginUser lu) {
        SysUser full = userRepository.findById(lu.getUserId()).orElseThrow();
        return UserVO.builder()
                .id(full.getId())
                .username(full.getUsername())
                .realName(full.getRealName())
                .phone(full.getPhone())
                .email(full.getEmail())
                .role(full.getRole())
                .avatar(full.getAvatar())
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        RoleEnum role = dto.getRole() == null ? RoleEnum.ELDER : dto.getRole();
        if (role != RoleEnum.ELDER && role != RoleEnum.FAMILY) {
            throw new BusinessException("自助注册仅支持老年人或家属角色");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        SysUser u = new SysUser();
        u.setUsername(dto.getUsername());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setRealName(dto.getRealName());
        u.setPhone(dto.getPhone());
        u.setEmail(dto.getEmail());
        u.setRole(role);
        u.setStatus(1);
        LocalDateTime now = LocalDateTime.now();
        u.setCreateTime(now);
        u.setUpdateTime(now);
        userRepository.save(u);
    }
}
