package io.github.quickmsg.common.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author luxurong
 */
@Getter
@Setter
@ToString
public class RuleChainDefinition {

    private String ruleName;

    private List<RuleDefinition> chain;

}
