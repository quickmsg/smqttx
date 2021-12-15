package io.github.quickmsg.core;

import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.spi.loader.DynamicLoader;
import io.github.quickmsg.common.utils.RetryFailureHandler;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author luxurong
 */
@Slf4j
public class DefaultProtocolAdaptor implements ProtocolAdaptor {


    private final Scheduler scheduler;


    @SuppressWarnings("unchecked")
    public DefaultProtocolAdaptor(Scheduler scheduler) {
        this.scheduler = Optional.ofNullable(scheduler).orElse(Schedulers.boundedElastic());
        DynamicLoader
                .findAll(Protocol.class)
                .forEach(protocol ->
                        acceptor.asFlux().ofType(protocol.getClassType()).subscribeOn(this.scheduler).subscribe(msg -> {
                            Message message = (Message) msg;
                            Protocol<Message> messageProtocol = (Protocol<Message>) protocol;
                            ReceiveContext<?> receiveContext = message.getContext();
                            messageProtocol
                                    .doParseProtocol(message, message.getMqttChannel())
                                    .contextWrite(context -> context.putNonNull(ReceiveContext.class, message.getContext()))
                                    .subscribe(receiveContext::submitEvent);
                        }));
    }

    @Override
    public void chooseProtocol(Message message) {
        acceptor.emitNext(message, RetryFailureHandler.RETRY_NON_SERIALIZED);
    }
}
