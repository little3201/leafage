package io.leafage.auth.service;

import io.leafage.auth.repository.UserRepository;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

@Service
public class OidcUserService {

    private final UserRepository userRepository;

    public OidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public OidcUserInfo loadUser(String username) {
        return userRepository.findByUsername(username)
                .map(user -> OidcUserInfo.builder()
                        .subject(username)
                        .name(user.getName())
                        .build())
                .orElse(null);
    }
}
