package io.github.quickmsg.core.http.actors.mqtt;

import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.http.annotation.AllowCors;
import io.github.quickmsg.common.http.annotation.Header;
import io.github.quickmsg.common.http.annotation.Router;
import io.github.quickmsg.common.http.enums.HttpType;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.core.http.AbstractHttpActor;
import io.github.quickmsg.common.sql.ConnectionQueryModel;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.nio.charset.StandardCharsets;

/**
 * @author luxurong
 */
@Router(value = "/smqtt/connection", type = HttpType.POST)
@Slf4j
@Header(key = "Content-Type", value = "application/json")
@AllowCors
public class ConnectionActor extends AbstractHttpActor {

    @Override
    public Publisher<Void> doRequest(HttpServerRequest request, HttpServerResponse response, Configuration httpConfiguration) {
        return request
                    .receive()
                    .asString(StandardCharsets.UTF_8)
                    .map(this.toJson(ConnectionQueryModel.class))
                    .doOnNext(connectionQueryModel -> {
                        response.sendString(Mono.just(JacksonUtil.bean2Json(ContextHolder.getReceiveContext().getIntegrate().getChannels()
                                    .queryConnectionSql(connectionQueryModel)))).then().subscribe();
                    }).then();

    }


}
