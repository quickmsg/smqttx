package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */

@Data
public class SubscribeMessage implements Message {

    private int messageId;

    private long timestamp;

    private String clientId;


    private List<SubscribeTopic> subscribeTopics;


    public SubscribeMessage(MqttMessage mqttMessage, String clientId) {
        MqttSubscribeMessage subscribeMessage = (MqttSubscribeMessage) mqttMessage;
        this.clientId=clientId;
        this.messageId = subscribeMessage.variableHeader().messageId();
        this.timestamp = System.currentTimeMillis();
        this.subscribeTopics =
                subscribeMessage
                        .payload()
                        .topicSubscriptions()
                        .stream()
                        .map(mqttTopicSubscription -> new SubscribeTopic(mqttTopicSubscription.topicName(), mqttTopicSubscription.qualityOfService(), clientId))
                        .collect(Collectors.toList());
    }
}
