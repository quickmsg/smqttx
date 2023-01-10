package io.github.quickmsg.core.auth;

import io.github.quickmsg.common.auth.AuthManager;
import io.github.quickmsg.common.config.AuthConfig;
import reactor.core.publisher.Mono;

/**
 * @author luxurong
 */
public class FixedAuthManager implements AuthManager {

    private final AuthConfig authConfig;

    public FixedAuthManager(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @Override
    public Mono<Boolean> auth(String userName, byte[] passwordInBytes, String clientIdentifier) {
        return Mono.just(authConfig.getFixed().getUsername().equals(userName)
                && authConfig.getFixed().getPassword().equals(new String(passwordInBytes)));
    }
}
