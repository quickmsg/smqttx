package io.github.quickmsg.common.event;

/**
 * @author luxurong
 * @date 2021/11/10 23:12
 */
public abstract class EventSubscriber<T extends Event> {

    public EventSubscriber(Pipeline pipeline, Class<T> tClass) {
        pipeline.handle(tClass).subscribe(this::apply);
    }

    public abstract void apply(T t);

}
