package org.example.domain.user.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.domain.user.controller.dto.*;


import org.example.domain.user.service.UserService;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    //회원가입
    @PostMapping("/register")
    public ResponseEntity<Void> register(
           @RequestBody @Valid UserCreateRequestDto requestDto
    ) {
        userService.create(requestDto);
     return ResponseEntity.ok(null);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(
           @RequestBody @Valid UserLoginRequestDto requestDto
    ) {

        UserLoginResponseDto user =  userService.login(requestDto);
        return ResponseEntity.ok().body(user);
    }

    //회원조회
    @GetMapping
    public ResponseEntity<UserReadResponseDto> readAllUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long id = userDetails.getId();
        UserReadResponseDto userReadResponseDto = userService.read(id);
        return ResponseEntity.ok().body(userReadResponseDto);
    }

    //회원수정
    @PatchMapping("/{id}")
    public ResponseEntity<UserReadResponseDto> modifiedUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserModifiedRequestDto requestDto
    ) {
        Long userId = userDetails.getId();
        UserReadResponseDto userReadResponseDto = userService.updateUser(userId, requestDto);
        return ResponseEntity.ok().body(userReadResponseDto);
    }


//    //로그아웃
//    @PostMapping("/logout")
//    public ResponseEntity<Void> deleted(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @RequestBody @Valid UserLogoutRequestDto requestDto) {
//        Long userId = userDetails.getId();
//
//        userService.deleted(userId, requestDto);
//        return ResponseEntity.ok().body();
//    }
//    )






}
