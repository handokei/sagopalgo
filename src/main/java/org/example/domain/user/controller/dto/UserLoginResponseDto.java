package org.example.domain.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserLoginResponseDto {

    private Long id;

    private String name;

    private String accessToken;

    private String refreshToken;

    public static UserLoginResponseDto from(Long id, String name, String accessToken, String refreshToken) {
        return new UserLoginResponseDto(id, name, accessToken, refreshToken);
    }
}
