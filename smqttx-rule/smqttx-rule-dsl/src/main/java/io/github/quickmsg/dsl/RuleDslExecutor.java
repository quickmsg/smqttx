package io.github.quickmsg.dsl;

import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.rule.DslExecutor;
import io.github.quickmsg.rule.RuleChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author luxurong
 */

public class RuleDslExecutor implements DslExecutor {

    private final RuleChain ruleChain;

    public RuleDslExecutor(RuleChain ruleChain) {
        this.ruleChain = ruleChain;
    }

    @Override
    public void executeRule(Event event) {
        Mono.deferContextual(ruleChain::executeRule)
                .contextWrite(context -> context
                        .put(Event.class, event)
                        .put(ReceiveContext.class, ContextHolder.getReceiveContext()))
                .subscribeOn(Schedulers.parallel())
                .subscribe();
    }


    @Override
    public Boolean isExecute() {
        return ruleChain.getRuleNodeList().size() > 0;
    }
}
