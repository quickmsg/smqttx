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
import reactor.core.publisher.Flux;
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
        
        // 使用共享的Flux处理所有消息，避免重复订阅
        Flux<Message> sharedFlux = acceptor.asFlux()
            .doOnError(throwable -> log.error("DefaultProtocolAdaptor consumer", throwable))
            .onErrorResume(throwable -> Mono.empty())
            .publishOn(Schedulers.newParallel("message-acceptor", threadSize))
            .share(); // 共享Flux避免重复订阅
        
        DynamicLoader.findAll(Protocol.class).forEach(protocol ->
            sharedFlux.ofType(protocol.getClassType())
                .subscribe(msg -> processMessage((Message) msg, protocol)));
    }
    
    private void processMessage(Message message, Protocol<?> protocol) {
        try {
            Protocol<Message> messageProtocol = (Protocol<Message>) protocol;
            ReceiveContext<?> receiveContext = ContextHolder.getReceiveContext();
            
            messageProtocol.doParseProtocol(message, message.getMqttChannel())
                .contextWrite(context -> context.putNonNull(ReceiveContext.class, ContextHolder.getReceiveContext()))
                .onErrorContinue((throwable, obj) -> {
                    log.error("DefaultProtocolAdaptor processMessage error", throwable);
                })
                .subscribe();
                
            RuleDslExecutor executor = ((AbstractReceiveContext<?>) receiveContext).getRuleDslExecutor();
            executor.executeRule(message);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
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
