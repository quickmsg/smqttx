package io.github.quickmsg.common.pipeline;

import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * @author luxurong
 */
public interface Pipeline extends Consumer<Object> {

    Flux<Object> handle();

    <T> Flux<T> handle(Class<T> tClass);

}
