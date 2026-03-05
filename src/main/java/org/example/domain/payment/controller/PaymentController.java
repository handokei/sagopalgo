package org.example.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.payment.controller.dto.PaymentConfirmRequestDto;
import org.example.domain.payment.controller.dto.PaymentPrepareRequestDto;
import org.example.domain.payment.controller.dto.PaymentResponseDto;
import org.example.domain.payment.service.PaymentService;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 준비 (Payment 레코드 생성)
    @PostMapping("/prepare")
    public ResponseEntity<PaymentResponseDto> prepare(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PaymentPrepareRequestDto requestDto
    ) {
        return ResponseEntity.status(201)
                .body(paymentService.prepare(userDetails.getId(), requestDto));
    }

    // 결제 확인 (PortOne 검증 후 완료)
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponseDto> confirm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PaymentConfirmRequestDto requestDto
    ) {
        return ResponseEntity.ok(paymentService.confirm(userDetails.getId(), requestDto));
    }
}
