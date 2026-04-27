package org.example.domain.sms.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SmsSendRequestDto {

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식을 입력하세요. (예: 01012345678)")
    private String phoneNumber;
}
