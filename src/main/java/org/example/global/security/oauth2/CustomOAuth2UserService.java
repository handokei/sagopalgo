package org.example.global.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.user.domain.model.User;
import org.example.domain.user.domain.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 로그인 시도 - provider: {}, attributes: {}", registrationId, attributes);

        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, attributes);

        User user = userRepository
                .findByProviderAndProviderIdAndIsDeletedFalse(
                        oAuthAttributes.getProvider(),
                        oAuthAttributes.getProviderId())
                .map(existing -> {
                    existing.updateOAuth2Info(oAuthAttributes.getName(), oAuthAttributes.getNickname());
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    User created = oAuthAttributes.toUser();
                    log.info("신규 OAuth2 유저 가입 - provider: {}, email: {}", registrationId, created.getEmail());
                    return userRepository.save(created);
                });

        return new CustomOAuth2User(user.getId(), user.getEmail(), user.getUserRole().name(), oAuthAttributes);
    }
}
