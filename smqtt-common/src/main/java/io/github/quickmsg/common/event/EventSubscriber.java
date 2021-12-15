package io.github.quickmsg.common.event;

import reactor.core.scheduler.Schedulers;

/**
 * @author luxurong
 * @date 2021/11/10 23:12
 */
public abstract class EventSubscriber<T extends Event> {

    public EventSubscriber(Pipeline pipeline, Class<T> tClass) {
        pipeline.handle(tClass).subscribeOn(Schedulers.parallel()).subscribe(this::apply);
    }

    public abstract void apply(T t);

}
