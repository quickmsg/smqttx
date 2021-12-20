package io.github.quickmsg.dsl;

import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.EventSubscriber;
import io.github.quickmsg.common.event.Pipeline;

/**
 * @author luxurong
 */
public class RuleDslAcceptor extends EventSubscriber<Event> {



    public RuleDslAcceptor(Pipeline pipeline) {
        super(pipeline, Event.class);
    }

    @Override
    public void apply(Event event) {

    }


}
