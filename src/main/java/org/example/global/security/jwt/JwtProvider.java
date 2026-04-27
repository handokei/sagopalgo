package org.example.global.security.jwt;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.global.security.auth.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessTokenExpire = 1000L * 60 * 30;       // 30분
    private final long refreshTokenExpire = 1000L * 60 * 60 * 24 * 14; // 14일

    public JwtProvider(@Value("${jwt.secret.key}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 생성
    public String createAccessToken(Long userId, String email, String role) {
        return createToken(userId, email, role, accessTokenExpire);
    }

    // RefreshToken 생성
    public String createRefreshToken(Long userId, String email, String role) {
        return createToken(userId, email, role, refreshTokenExpire);
    }

    // 공통 Token 생성 로직
    private String createToken(Long userId, String email, String role, long expire) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expire);

        return Jwts.builder()
                .subject(String.valueOf(userId)) // subject → userId
                .claim("email", email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    // 토큰에서 role 꺼내기
    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // 토큰에서 userId 꺼내기
    public Long getUserId(String token) {
        return Long.parseLong(
                Jwts.parser()
                        .verifyWith(key)     // 키 검증
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }


    }

    public Authentication getAuthentication(
            String token,
            CustomUserDetailsService userDetailsService
    ) {
        Long userId = getUserId(token);

        UserDetails userDetails = userDetailsService.loadUserById(userId);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
