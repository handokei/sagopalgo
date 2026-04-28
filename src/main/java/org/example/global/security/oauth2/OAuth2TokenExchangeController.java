package org.example.global.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.example.domain.user.controller.dto.UserLoginResponseDto;
import org.example.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuth2TokenExchangeController {

    private final OAuth2TokenExchangeService tokenExchangeService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> exchangeToken(@RequestParam String code) {
        UserLoginResponseDto responseDto = tokenExchangeService.exchangeCodeForToken(code);
        return ResponseEntity.ok(ApiResponse.success("OAuth2 로그인 성공", responseDto));
    }
}
