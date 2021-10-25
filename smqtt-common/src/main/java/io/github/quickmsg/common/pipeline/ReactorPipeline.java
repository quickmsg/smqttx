package io.github.quickmsg.common.pipeline;

import io.github.quickmsg.common.message.HeapMqttMessage;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luxurong
 * @date 2021/10/25 13:41
 * @description
 */
public class ReactorPipeline implements  Pipeline<PipelineContext>{

    private final  List<Subscriber<String>> subscribers = new ArrayList<>();


    private final Sinks.Many<PipelineContext> conexts = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public void accept(PipelineContext pipelineContext) {
         conexts.tryEmitNext(pipelineContext);
    }




}
