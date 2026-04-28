package org.example.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorResponseDto;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";
    private static final int DEFAULT_LOGIN_LIMIT = 10;
    private static final int DEFAULT_SMS_SEND_LIMIT = 3;
    private static final int DEFAULT_SMS_VERIFY_LIMIT = 5;

    private static final Map<String, Integer> PATH_LIMITS = Map.of(
            "/api/users/login", DEFAULT_LOGIN_LIMIT,
            "/api/sms/send", DEFAULT_SMS_SEND_LIMIT,
            "/api/sms/verify", DEFAULT_SMS_VERIFY_LIMIT
    );

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        Integer maxRequests = PATH_LIMITS.get(path);
        if (maxRequests == null) {
            return true;
        }

        String clientIp = getClientIp(request);
        String key = RATE_LIMIT_PREFIX + path + ":" + clientIp;

        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }

        if (count != null && count > maxRequests) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.TOO_MANY_REQUESTS, "요청 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return false;
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
