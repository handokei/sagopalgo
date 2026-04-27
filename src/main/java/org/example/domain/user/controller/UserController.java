package org.example.domain.user.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.domain.user.controller.dto.*;

import org.example.domain.user.service.UserService;
import org.example.global.response.ApiResponse;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
           @RequestBody @Valid UserCreateRequestDto requestDto
    ) {
        userService.create(requestDto);
        return ResponseEntity.status(201).body(ApiResponse.success("회원가입 성공", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(
           @RequestBody @Valid UserLoginRequestDto requestDto
    ) {
        UserLoginResponseDto user = userService.login(requestDto);
        return ResponseEntity.ok().body(ApiResponse.success("로그인 성공", user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserReadResponseDto>> readAllUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long id = userDetails.getId();
        UserReadResponseDto userReadResponseDto = userService.read(id);
        return ResponseEntity.ok().body(ApiResponse.success("회원 조회 성공", userReadResponseDto));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserReadResponseDto>> getMe(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long id = userDetails.getId();
        UserReadResponseDto userReadResponseDto = userService.read(id);
        return ResponseEntity.ok().body(ApiResponse.success("내 정보 조회 성공", userReadResponseDto));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserReadResponseDto>> updateMe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserModifiedRequestDto requestDto
    ) {
        Long userId = userDetails.getId();
        UserReadResponseDto userReadResponseDto = userService.updateUser(userId, requestDto);
        return ResponseEntity.ok().body(ApiResponse.success("내 정보 수정 성공", userReadResponseDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserReadResponseDto>> modifiedUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserModifiedRequestDto requestDto
    ) {
        Long userId = userDetails.getId();
        UserReadResponseDto userReadResponseDto = userService.updateUser(userId, requestDto);
        return ResponseEntity.ok().body(ApiResponse.success("회원 수정 성공", userReadResponseDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace("Bearer ", "");
        userService.logout(accessToken);
        return ResponseEntity.ok().body(ApiResponse.success("로그아웃 성공", null));
    }
}
