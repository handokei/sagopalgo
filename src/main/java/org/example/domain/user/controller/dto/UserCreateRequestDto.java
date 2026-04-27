package org.example.domain.user.controller.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.user.domain.model.UserRole;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {

    @NotBlank
    @Email(message = "올바른 이메일 형식을 입력하세요.")
    private String email;

    @NotBlank
    @Size(min = 8, message = "8글자 이상 입력하세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
            message = "비밀번호는 대소문자 영문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인란을 입력해주세요.")
    private String confirmPassword;

    @NotBlank
    @Size(min = 2 ,max = 10, message = "2 ~ 10글자 이하로 입력하세요.")
    private String name;

    @NotBlank
    @Size(min = 2 ,max = 10, message = "2 ~ 10글자 이하로 입력하세요")
    private String nickname;

    @NotNull
    private UserRole userRole;

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "올바른 전화번호 형식을 입력하세요. (예: 01012345678)")
    private String phoneNumber;

}
