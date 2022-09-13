package io.github.quickmsg.core.http.actors.system;

import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.http.HttpActor;
import io.github.quickmsg.common.http.annotation.AllowCors;
import io.github.quickmsg.common.http.annotation.Header;
import io.github.quickmsg.common.http.annotation.Router;
import io.github.quickmsg.common.http.enums.HttpType;
import io.github.quickmsg.common.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cluster.ClusterNode;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Router(value = "/smqtt/cluster", type = HttpType.POST)
@Slf4j
@Header(key = "Content-Type", value = "application/json")
@AllowCors
public class ClusterActor implements HttpActor {


    @Override
    public Publisher<Void> doRequest(HttpServerRequest request, HttpServerResponse response, Configuration httpConfiguration) {
        // ContextHolder.getReceiveContext()
        //                        .getIntegrate().getIgnite().cluster().nodes().stream().flatMap(clusterNode -> clusterNode.addresses().stream()).collect(Collectors.toList()
        return request
                .receive()
                .then(response.sendString(Mono.just(JacksonUtil.bean2Json(Collections.emptyList()))).then());
    }
}
