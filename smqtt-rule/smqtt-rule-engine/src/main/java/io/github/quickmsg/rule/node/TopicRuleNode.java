package io.github.quickmsg.rule.node;

import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.PublishEvent;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.github.quickmsg.rule.RuleNode;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import reactor.util.context.ContextView;

import java.util.Objects;

/**
 * @author luxurong
 */
@Slf4j
public class TopicRuleNode implements RuleNode {

    private final String topic;

    private RuleNode ruleNode;

    public TopicRuleNode(String topic) {
        Objects.requireNonNull(topic);
        this.topic = topic;
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
        ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);
        Event event = contextView.get(Event.class);
        if(event instanceof PublishEvent){
            PublishEvent publishEvent = (PublishEvent) event;
            log.info("rule engine TopicRuleNode  request {}", publishEvent);
            ProtocolAdaptor protocolAdaptor = receiveContext.getProtocolAdaptor();
//            protocolAdaptor.chooseProtocol();
            executeNext(contextView);
        }
    }


}
