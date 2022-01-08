package io.github.quickmsg.rule.node;

import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.rule.RuleNode;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class SqlRuleNode implements RuleNode {

    private final String script;

    private RuleNode ruleNode;

    public SqlRuleNode(String script) {
        this.script = script;
    }


    @Override
    public void execute(ContextView context) {
        ReceiveContext<?> receiveContext = context.get(ReceiveContext.class);
        Event event = context.get(Event.class);
//        IntegrateCache<String, Object> cache = receiveContext.getIntegrate().getLocalCache();
        try {
//            cache.
        }finally {

        }
        executeNext(context);
    }

    @Override
    public RuleNode getNextRuleNode() {
        return this.ruleNode;
    }

    @Override
    public void setNextRuleNode(RuleNode ruleNode) {
        this.ruleNode = ruleNode;
    }
}
