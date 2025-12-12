package org.example.domain.user.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserModifiedRequestDto {


    @NotBlank
    @Size(min = 2 ,max = 10, message = "2 ~ 10글자 이하로 입력하세요")
    private String nickname;

    @NotBlank
    @Size(min = 8, message = "8글자 이상 입력하세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
            message = "비밀번호는 대소문자 영문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인란을 입력해주세요.")
    private String confirmPassword;

}
