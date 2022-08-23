package io.github.quickmsg.core;

import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.spi.loader.DynamicLoader;
import io.github.quickmsg.common.utils.RetryFailureHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

/**
 * @author luxurong
 */
@Slf4j
public class DefaultProtocolAdaptor implements ProtocolAdaptor {

    private final Sinks.Many<Message> acceptor;

    @SuppressWarnings("unchecked")
    public DefaultProtocolAdaptor(Integer businessQueueSize, Integer threadSize) {
        this.acceptor = Sinks.many().multicast().onBackpressureBuffer(businessQueueSize);
        DynamicLoader.findAll(Protocol.class).forEach(protocol ->
                acceptor.asFlux().publishOn(Schedulers.newParallel("message-acceptor", threadSize))
                        .doOnError(throwable -> log.error("DefaultProtocolAdaptor consumer",throwable))
                        .onErrorResume(throwable -> Mono.empty())
                        .ofType(protocol.getClassType()).subscribe(msg -> {
                            Message message = (Message) msg;
                            Protocol<Message> messageProtocol = (Protocol<Message>) protocol;
                            ReceiveContext<?> receiveContext = message.getContext();
                            messageProtocol.doParseProtocol(message, message.getMqttChannel())
                                    .contextWrite(context -> context.putNonNull(ReceiveContext.class, message.getContext()))
                                    .onErrorContinue((throwable,obj)-> {
                                        log.error("DefaultProtocolAdaptor",throwable);
                                    })
                                    .subscribe();
                        }));
    }

    @Override
    public void chooseProtocol(Message message) {
        try {
            acceptor.emitNext(message, RetryFailureHandler.RETRY_NON_SERIALIZED);
        } catch (Exception e) {
            log.error("protocol emitNext error", e);
        }
    }
}
