package io.github.quickmsg.core.http.actors.resource;

import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.http.HttpActor;
import io.github.quickmsg.common.http.annotation.AllowCors;
import io.github.quickmsg.common.http.annotation.Header;
import io.github.quickmsg.common.http.annotation.Router;
import io.github.quickmsg.common.http.enums.HttpType;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.core.http.HttpConfiguration;
import io.github.quickmsg.core.http.model.LoginDo;
import io.github.quickmsg.core.http.model.LoginVm;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author luxurong
 */
@Router(value = "/", type = HttpType.GET,resource = true)
@Slf4j
@AllowCors
@Header(key = "Location",value = "/index.html")
public class RootResourceActor implements HttpActor {

    @Override
    public Publisher<Void> doRequest(HttpServerRequest request, HttpServerResponse response, Configuration httpConfiguration) {
        return response.status(HttpResponseStatus.SEE_OTHER).send().then();

    }

}
