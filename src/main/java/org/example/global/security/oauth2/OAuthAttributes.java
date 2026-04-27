package org.example.global.security.oauth2;

import lombok.Getter;
import org.example.domain.user.domain.model.User;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String provider;
    private final String providerId;
    private final String email;
    private final String name;
    private final String nickname;

    private OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey,
                             String provider, String providerId,
                             String email, String name, String nickname) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
    }

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "kakao"  -> ofKakao(attributes);
            case "naver"  -> ofNaver(attributes);
            default -> throw new IllegalArgumentException("Unsupported provider: " + registrationId);
        };
    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
        return new OAuthAttributes(
                attributes,
                "sub",
                "google",
                (String) attributes.get("sub"),
                (String) attributes.get("email"),
                (String) attributes.get("name"),
                (String) attributes.get("name")
        );
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null
                ? (Map<String, Object>) kakaoAccount.get("profile")
                : null;

        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String nickname = profile != null ? (String) profile.get("nickname") : null;

        return new OAuthAttributes(
                attributes,
                "id",
                "kakao",
                String.valueOf(attributes.get("id")),
                email,
                nickname,
                nickname
        );
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        String nickname = response != null ? (String) response.get("nickname") : null;
        String displayName = (nickname != null) ? nickname : (response != null ? (String) response.get("name") : null);

        return new OAuthAttributes(
                attributes,
                "response",
                "naver",
                response != null ? (String) response.get("id") : null,
                response != null ? (String) response.get("email") : null,
                response != null ? (String) response.get("name") : null,
                displayName
        );
    }

    public User toUser() {
        String safeNickname = buildNickname(nickname, providerId);
        String safeName = (name != null && !name.isBlank()) ? name : "User";
        return User.ofOAuth2(email, safeName, safeNickname, provider, providerId);
    }

    private String buildNickname(String nickname, String providerId) {
        String base = (nickname != null && !nickname.isBlank())
                ? nickname.replaceAll("\\s+", "")
                : "user";
        String suffix = (providerId != null && providerId.length() >= 6)
                ? providerId.substring(0, 6)
                : "000000";
        return (base + "_" + suffix).toLowerCase();
    }
}
