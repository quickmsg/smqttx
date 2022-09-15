package io.github.quickmsg.core.http.actors.mqtt;

import io.github.quickmsg.common.http.annotation.AllowCors;
import io.github.quickmsg.common.http.annotation.Router;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.http.enums.HttpType;
import io.github.quickmsg.common.message.HttpPublishMessage;
import io.github.quickmsg.core.http.AbstractHttpActor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author luxurong
 */
@Router(value = "/smqtt/publish", type = HttpType.POST)
@Slf4j
@AllowCors
public class PublishActor extends AbstractHttpActor {


    @Override
    public Publisher<Void> doRequest(HttpServerRequest request, HttpServerResponse response, Configuration httpConfiguration) {
        return request
                .receive()
                .asString(StandardCharsets.UTF_8)
                .map(this.toJson(HttpPublishMessage.class))
                .doOnNext(message -> {
                    this.sendMqttMessage(message.getPublishMessage());
                    log.info("http request url {} body {}", request.path(), message);
                }).then(response.sendString(Mono.just("success")).then());
    }

}
