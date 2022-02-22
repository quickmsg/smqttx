package io.github.quickmsg.common.rule;

import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.EventSubscriber;
import io.github.quickmsg.common.event.Pipeline;

/**
 * @author luxurong
 */
public class RuleDslAcceptor extends EventSubscriber<Event> {


    private final DslExecutor dslExecutor;

    public RuleDslAcceptor(Pipeline pipeline, DslExecutor dslExecutor) {
        super(pipeline, Event.class);
        this.dslExecutor = dslExecutor;
    }

    @Override
    public void apply(Event event) {
        dslExecutor.executeRule(event);
    }


}
