package com.health.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    private String accessToken;
    private String refreshToken;
    /** access 过期时间（秒） */
    private long expiresIn;
    private UserVO user;
}
