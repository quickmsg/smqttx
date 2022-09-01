package io.github.quickmsg.common.event;

import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.utils.RetryFailureHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

/**
 * @author luxurong
 */
@Slf4j
public class ReactorPipeline implements Pipeline {


    private final Sinks.Many<Message> onBackpressureBuffer = Sinks.many().multicast().directAllOrNothing();

    @Override
    public void accept(Message message) {
        try {
            onBackpressureBuffer.emitNext(message, new RetryFailureHandler());
        }catch (Exception e){
           log.error("event emitNext error",e);
        }
    }

    @Override
    public <T extends Message> Flux<T> handle(Class<T> tClass) {
        return onBackpressureBuffer
                .asFlux()
                .ofType(tClass)
                .publishOn(Schedulers.parallel());
    }

}
