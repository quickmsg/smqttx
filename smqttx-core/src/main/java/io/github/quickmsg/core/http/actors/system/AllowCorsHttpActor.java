package io.github.quickmsg.core.http.actors.system;

import io.github.quickmsg.common.http.annotation.AllowCors;
import io.github.quickmsg.common.http.annotation.Router;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.http.enums.HttpType;
import io.github.quickmsg.common.http.HttpActor;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

/**
 * @author luxurong
 */

@Router(value = "/**", type = HttpType.OPTIONS)
@AllowCors
public class AllowCorsHttpActor implements HttpActor {
    @Override
    public Publisher<Void> doRequest(HttpServerRequest request, HttpServerResponse response, Configuration configuration) {
        response.addHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Accept, Origin,Authorization,Sec-Ch-Ua,Sec-Ch-Ua-Mobile,Sec-Ch-Ua-Platform,Content-Type,Referer,User-Agent");
        response.addHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "*");
        response.addHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.addHeader(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");

        return response.send();
    }
}
