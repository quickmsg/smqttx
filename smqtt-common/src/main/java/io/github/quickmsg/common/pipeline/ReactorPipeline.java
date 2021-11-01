package io.github.quickmsg.common.pipeline;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * @author luxurong
 */
@Slf4j
public class ReactorPipeline implements Pipeline {


    private final Sinks.Many<Object> onBackpressureBuffer = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public void accept(Object pipelineContext) {
        onBackpressureBuffer.tryEmitNext(pipelineContext);
    }

    @Override
    public Flux<Object> handle() {
        return onBackpressureBuffer.asFlux();
    }

    @Override
    public <T> Flux<T> handle(Class<T> tClass) {
        return onBackpressureBuffer
                .asFlux()
                .doOnNext(obj -> {
                    if (log.isDebugEnabled()) {
                        log.debug(obj.toString());
                    }
                })
                .ofType(tClass);
    }

}
