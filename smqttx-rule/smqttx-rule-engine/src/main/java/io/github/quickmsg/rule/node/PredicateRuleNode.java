package io.github.quickmsg.rule.node;

import io.github.quickmsg.rule.RuleNode;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class PredicateRuleNode implements RuleNode {

    private final String script;

    private RuleNode ruleNode;


    public PredicateRuleNode(String script) {
        this.script = script;
    }

    @Override
    public RuleNode getNextRuleNode() {
        return this.ruleNode;
    }


    @Override
    public void setNextRuleNode(RuleNode ruleNode) {
        this.ruleNode = ruleNode;
    }

    @Override

    public void execute(ContextView contextView) {
//        if ((Boolean) triggerScript(script, context -> {
//            Event event = contextView.get(Event.class);
//            context.set("$",event);
//        })) {
//            executeNext(contextView);
//        }
    }
}
