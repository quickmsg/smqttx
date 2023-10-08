package io.github.quickmsg.core.auth;

import io.github.quickmsg.common.auth.AuthBean;
import io.github.quickmsg.common.auth.AuthManager;
import reactor.core.publisher.Mono;

/**
 * @author luxurong
 */
public class NoneAuthManager implements AuthManager {
    @Override
    public Mono<Boolean> auth(String userName, byte[] passwordInBytes, String clientIdentifier) {
        return  Mono.just(true);
    }
}
