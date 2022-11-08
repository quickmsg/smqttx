package io.github.quickmsg.core.http.actors.system;

import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.http.HttpActor;
import io.github.quickmsg.common.http.annotation.AllowCors;
import io.github.quickmsg.common.http.annotation.Router;
import io.github.quickmsg.common.http.enums.HttpType;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

/**
 * @author luxurong
 */
@Router(value = "/smqtt/is/cluster", type = HttpType.GET)
@Slf4j
@AllowCors
public class IsClusterActor implements HttpActor {


    @Override
    public Publisher<Void> doRequest(HttpServerRequest request, HttpServerResponse response, Configuration httpConfiguration) {
        return request.receive().then(response.sendString(Mono.just(String.valueOf(true))).then());
    }
}
