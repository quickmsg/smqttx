package io.github.quickmsg.common.event;

import io.github.quickmsg.common.message.Message;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * @author luxurong
 */
public interface Pipeline  extends Consumer<Message> {

    <M extends Message> Flux<M> handle(Class<M > tClass);

}
