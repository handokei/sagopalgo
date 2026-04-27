package org.example.domain.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.user.domain.model.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserReadResponseDto {

  private String email;

    private String name;

    private String nickname;

    private String userRole;

    public static UserReadResponseDto from(User user) {
        return new UserReadResponseDto(
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getUserRole() != null ? user.getUserRole().name() : null
        );
    }


}
