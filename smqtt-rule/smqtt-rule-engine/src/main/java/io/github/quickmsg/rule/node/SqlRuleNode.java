package io.github.quickmsg.rule.node;

import io.github.quickmsg.rule.RuleNode;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 * @date 2021/11/8 21:47
 */
public class SqlRuleNode  implements RuleNode{


    @Override
    public void execute(ContextView context) {

    }

    @Override
    public RuleNode getNextRuleNode() {
        return null;
    }

    @Override
    public void setNextRuleNode(RuleNode ruleNode) {

    }
}
