package io.github.quickmsg.common.event;

import io.github.quickmsg.common.message.Message;

/**
 * @author luxurong
 * @date 2021/11/10 23:12
 */
public abstract class EventSubscriber<T extends Message> {

    public EventSubscriber(Pipeline pipeline, Class<T> tClass) {
        pipeline.handle(tClass).subscribe(this::apply);
    }

    public abstract void apply(T t);

}
