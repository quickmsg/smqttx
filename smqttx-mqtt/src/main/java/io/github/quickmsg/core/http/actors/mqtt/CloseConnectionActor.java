package io.github.quickmsg.core.http.actors.mqtt;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.http.annotation.AllowCors;
import io.github.quickmsg.common.http.annotation.Header;
import io.github.quickmsg.common.http.annotation.Router;
import io.github.quickmsg.common.http.enums.HttpType;
import io.github.quickmsg.common.integrate.job.JobCaller;
import io.github.quickmsg.common.integrate.job.JobClosure;
import io.github.quickmsg.core.http.AbstractHttpActor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @author luxurong
 */
@Router(value = "/smqtt/close/connection", type = HttpType.POST)
@Slf4j
@Header(key = "Content-Type", value = "application/json")
@AllowCors
public class CloseConnectionActor extends AbstractHttpActor {

    @Override
    public Publisher<Void> doRequest(HttpServerRequest request, HttpServerResponse response, Configuration httpConfiguration) {
        return request
                .receive()
                .asString(StandardCharsets.UTF_8)
                .map(this.toJson(Close.class))
                .doOnNext(close -> {
                    if(CollectionUtils.isNotEmpty(close.getIds())){
                        close.getIds().forEach(id->{
                            ContextHolder.getReceiveContext()
                                    .getIntegrate().getJobExecutor()
                                    .callBroadcast(new JobClosure<String,Void>() {

                                        @Override
                                        public Void apply(String clientId) {
                                           Optional.ofNullable(ContextHolder.getReceiveContext()
                                                   .getIntegrate()
                                                   .getChannels()
                                                   .get(id))
                                                   .ifPresent(MqttChannel::close);
                                            return null;
                                        }

                                        @Override
                                        public String getJobName() {
                                            return "close-connection";
                                        }
                                    },id);
                        });
                    }
                }).then();
    }


    @Data
    public static class Close{
        private List<String> ids;
    }



}
