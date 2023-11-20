package io.github.quickmsg.dsl;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.message.mqtt.CloseMessage;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.rule.DslExecutor;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.rule.RuleChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

/**
 * @author luxurong
 */

public class RuleDslExecutor implements DslExecutor {

    private final RuleChain ruleChain;

    public RuleDslExecutor(RuleChain ruleChain) {
        this.ruleChain = ruleChain;
    }

    @Override
    public void executeRule(Message message) {
        final Map<String, Object> map = JacksonUtil.bean2Map(message);
        if(message instanceof  PublishMessage){
            map.put("body",JacksonUtil.dynamic(new String(((PublishMessage)message).getBody())));
        }
        Mono.deferContextual(ruleChain::executeRule)
                .contextWrite(context -> context
                        .put(Map.class,  map)
                        .put(ReceiveContext.class, ContextHolder.getReceiveContext()))
                .subscribeOn(Schedulers.parallel())
                .subscribe();
    }




    @Override
    public Boolean isExecute() {
        return !ruleChain.getRuleNodeList().isEmpty();
    }
}
