package org.example.global.security.auth;

import lombok.RequiredArgsConstructor;
import org.example.domain.user.domain.model.User;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        String roleName = "ROLE_" + user.getUserRole().name();

        return new CustomUserDetails(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getPassword()
                , List.of(new SimpleGrantedAuthority(roleName))
        );
    }
}

