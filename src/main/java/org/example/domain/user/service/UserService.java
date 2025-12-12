package org.example.domain.user.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.user.controller.dto.*;
import org.example.domain.user.domain.model.User;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.example.global.security.jwt.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    @Transactional
    public void create(UserCreateRequestDto requestDto) {
        if (userRepository.existsByEmailAndIsDeletedFalse(requestDto.getEmail())) {
            throw new UserException(UserErrorCode.DUPLICATION_EMAIL_EXCEPTION);
        }

        String encodePassword = passwordEncoder.encode(requestDto.getPassword());
        User user = User.of(requestDto.getEmail(),
                encodePassword,
                requestDto.getName(),
                requestDto.getNickname());
        userRepository.save(user);
    }

    public UserReadResponseDto read(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        return UserReadResponseDto.from(user);
    }

    public UserLoginResponseDto login(@Valid UserLoginRequestDto requestDto) {

    User user = userRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

    if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
        throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail());

        return UserLoginResponseDto.from(user.getId(), user.getName(), accessToken, refreshToken);
    }

    public UserReadResponseDto updateUser(Long userId, @Valid UserModifiedRequestDto requestDto) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        user.update(requestDto.getPassword(),
                requestDto.getNickname());

        return UserReadResponseDto.from(user);

    }

//    public void deleted(Long userId, @Valid UserDeletedRequestDto requestDto) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));
//
//    }
}
