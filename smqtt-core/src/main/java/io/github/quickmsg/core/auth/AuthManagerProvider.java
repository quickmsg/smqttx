package io.github.quickmsg.core.auth;

import io.github.quickmsg.common.config.AuthConfig;

/**
 * @author luxurong
 */
public interface AuthManagerProvider {

    AuthManagerFactory provider(AuthConfig authConfig);

}
