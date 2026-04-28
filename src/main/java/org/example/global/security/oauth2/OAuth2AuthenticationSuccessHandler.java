package org.example.global.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final StringRedisTemplate redisTemplate;

    @Value("${oauth2.redirect-uri:http://localhost:3000/oauth2/callback}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String code = UUID.randomUUID().toString();
        String payload = oAuth2User.getUserId() + ":" + oAuth2User.getEmail() + ":" + oAuth2User.getRole();

        redisTemplate.opsForValue().set(
                OAuth2Constants.AUTH_CODE_PREFIX + code,
                payload,
                OAuth2Constants.AUTH_CODE_TTL_SECONDS,
                TimeUnit.SECONDS
        );

        String targetUrl = redirectUri + "?code=" + code;

        log.info("OAuth2 로그인 성공 - userId: {}", oAuth2User.getUserId());
        response.sendRedirect(targetUrl);
    }
}
