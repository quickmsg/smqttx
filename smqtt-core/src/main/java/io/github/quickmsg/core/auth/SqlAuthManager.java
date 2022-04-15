package io.github.quickmsg.core.auth;

import io.github.quickmsg.common.auth.AuthBean;
import io.github.quickmsg.common.auth.AuthManager;
import io.github.quickmsg.common.config.AuthConfig;
import reactor.core.publisher.Mono;

/**
 * @author luxurong
 */
public class SqlAuthManager implements AuthManager {

    public SqlAuthManager(AuthConfig authConfig) {
    }

    //todo
    @Override
    // AuthBean

    public Mono<Boolean> auth(String userName, byte[] passwordInBytes, String clientIdentifier) {

        return Mono.just(true);
    }
}
