package io.github.quickmsg.common.event;

import io.github.quickmsg.common.utils.RetryFailureHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * @author luxurong
 */
@Slf4j
public class ReactorPipeline implements Pipeline {


    private final Sinks.Many<Event> onBackpressureBuffer = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public void accept(Event event) {
        onBackpressureBuffer.emitNext(event, new RetryFailureHandler());
    }

    @Override
    public Flux<Event> handle() {
        return onBackpressureBuffer.asFlux();
    }

    @Override
    public <T extends Event> Flux<T> handle(Class<T> tClass) {
        return onBackpressureBuffer
                .asFlux()
                .ofType(tClass);
    }

}
