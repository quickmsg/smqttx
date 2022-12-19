package io.github.quickmsg.common.message.mqtt;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */

@Data
public class SubscribeMessage implements Message {

    private int messageId;

    private String event = "subscribe";

    private String time;

    private String clientId;

    @JsonIgnore
    private MqttChannel mqttChannel;


    private List<SubscribeTopic> subscribeTopics;


    public SubscribeMessage(MqttMessage mqttMessage, MqttChannel mqttChannel) {
        MqttSubscribeMessage subscribeMessage = (MqttSubscribeMessage) mqttMessage;
        this.mqttChannel=mqttChannel;
        this.clientId= mqttChannel.getClientId();
        this.messageId = subscribeMessage.variableHeader().messageId();
        this.time = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
        this.subscribeTopics =
                subscribeMessage
                        .payload()
                        .topicSubscriptions()
                        .stream()
                        .map(mqttTopicSubscription -> new SubscribeTopic(mqttTopicSubscription.topicName(), mqttTopicSubscription.qualityOfService(), mqttChannel))
                        .collect(Collectors.toList());
    }
}
