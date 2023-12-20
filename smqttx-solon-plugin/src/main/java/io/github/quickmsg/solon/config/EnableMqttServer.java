package io.github.quickmsg.solon.config;


import io.github.quickmsg.solon.SolonBootstrapConfig;
import org.noear.solon.annotation.Import;

import java.lang.annotation.*;

/**
 * @author luxurong
 * @author noear
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(classes = {SolonBootstrapConfig.class, AutoMqttConfiguration.class},
        profilesIfAbsent = {"classpath:META-INF/solon_def/smqttx-def.yml"})
@Documented
public @interface EnableMqttServer {
}
