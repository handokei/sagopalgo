package org.example.global.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.example.domain.user.controller.dto.UserLoginResponseDto;
import org.example.domain.user.domain.model.User;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.example.global.security.jwt.JwtProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2TokenExchangeService {

    private final StringRedisTemplate redisTemplate;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public UserLoginResponseDto exchangeCodeForToken(String code) {
        String payload = redisTemplate.opsForValue().getAndDelete(OAuth2Constants.AUTH_CODE_PREFIX + code);

        if (payload == null) {
            throw new UserException(UserErrorCode.INVALID_AUTH_CODE);
        }

        String[] parts = payload.split(":", 3);
        Long userId = Long.parseLong(parts[0]);
        String email = parts[1];
        String role = parts[2];

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        String accessToken = jwtProvider.createAccessToken(userId, email, role);
        String refreshToken = jwtProvider.createRefreshToken(userId, email, role);

        return UserLoginResponseDto.from(user.getId(), user.getName(), accessToken, refreshToken);
    }
}
