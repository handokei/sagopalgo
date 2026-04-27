package org.example.global.security.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final Long userId;
    private final String email;
    private final String role;
    private final OAuthAttributes oAuthAttributes;

    public CustomOAuth2User(Long userId, String email, String role, OAuthAttributes oAuthAttributes) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.oAuthAttributes = oAuthAttributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuthAttributes.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
