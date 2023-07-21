package io.github.quickmsg.core;

import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.spi.loader.DynamicLoader;
import io.github.quickmsg.common.utils.RetryFailureHandler;
import io.github.quickmsg.core.mqtt.AbstractReceiveContext;
import io.github.quickmsg.dsl.RuleDslExecutor;
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
                    acceptor.asFlux()
                                .doOnError(throwable -> log.error("DefaultProtocolAdaptor consumer", throwable))
                                .onErrorResume(throwable -> Mono.empty())
                                .ofType(protocol.getClassType())
                                .publishOn(Schedulers.newParallel("message-acceptor", threadSize))
                                .subscribe(msg -> {
                                    Message message = (Message) msg;
                                    Protocol<Message> messageProtocol = (Protocol<Message>) protocol;
                                    ReceiveContext<?> receiveContext = ContextHolder.getReceiveContext();
                                    messageProtocol.doParseProtocol(message, message.getMqttChannel())
                                                .contextWrite(context -> context.putNonNull(ReceiveContext.class, ContextHolder.getReceiveContext()))
                                                .onErrorContinue((throwable, obj) -> {
                                                    log.error("DefaultProtocolAdaptor", throwable);
                                                })
                                                .subscribe();
                                    RuleDslExecutor executor = ((AbstractReceiveContext<?>) receiveContext).getRuleDslExecutor();
                                    executor.executeRule( message);
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
