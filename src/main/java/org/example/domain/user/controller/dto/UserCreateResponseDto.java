package org.example.domain.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserCreateResponseDto {

    private String email;


    public static UserCreateResponseDto from(String email) {
    return UserCreateResponseDto.from(email);
    }
}
