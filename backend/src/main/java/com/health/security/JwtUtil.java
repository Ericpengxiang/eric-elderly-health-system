package com.health.security;

import com.health.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 生成与解析（access + refresh 双令牌）
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    public static final String CLAIM_UID = "uid";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_TYPE = "typ";
    public static final String CLAIM_JTI = "jti";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;

    private SecretKey signingKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                keyBytes = md.digest(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId, String username, String roleName) {
        long expMs = jwtProperties.getAccessTokenExpireMinutes() * 60 * 1000;
        Date now = new Date();
        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setSubject(username)
                .claim(CLAIM_UID, userId)
                .claim(CLAIM_ROLE, roleName)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expMs))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * refresh 令牌携带随机 jti，用于在 Redis 中校验与吊销
     */
    public String createRefreshToken(Long userId, String username) {
        long expMs = jwtProperties.getRefreshTokenExpireDays() * 24 * 60 * 60 * 1000;
        Date now = new Date();
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setSubject(username)
                .claim(CLAIM_UID, userId)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .claim(CLAIM_JTI, jti)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expMs))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractJti(String refreshToken) {
        Claims c = parseToken(refreshToken);
        return c.get(CLAIM_JTI, String.class);
    }
}
