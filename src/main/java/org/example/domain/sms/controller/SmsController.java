package org.example.domain.sms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.sms.controller.dto.SmsSendRequestDto;
import org.example.domain.sms.controller.dto.SmsVerifyRequestDto;
import org.example.domain.sms.service.SmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@Valid @RequestBody SmsSendRequestDto requestDto) {
        smsService.sendCode(requestDto.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody SmsVerifyRequestDto requestDto) {
        smsService.verifyCode(requestDto.getPhoneNumber(), requestDto.getCode());
        return ResponseEntity.ok().build();
    }
}
