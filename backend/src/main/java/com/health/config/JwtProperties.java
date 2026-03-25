package com.health.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置项，对应 application.yml 中 jwt 节点
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * HS256 密钥（建议生产环境通过环境变量注入至少 32 字节随机串）
     */
    private String secret;

    private long accessTokenExpireMinutes = 120;

    private long refreshTokenExpireDays = 7;

    private String issuer = "elderly-health-system";
}
