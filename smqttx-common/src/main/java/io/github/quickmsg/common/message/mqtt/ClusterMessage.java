package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.channel.MqttChannel;
import lombok.Data;

import java.util.Optional;

/**
 * @author luxurong
 */
@Data
public class ClusterMessage{

    private int qos;

    private boolean retain;

    private Object body;

    private String connectTime;

    private String clientId;

    private String topic;


    private String originTopic;

    private int channelId;



    public ClusterMessage(PublishMessage message) {
        this.topic = message.getTopic();
        this.originTopic = message.getTopic();
        this.qos = message.getQos();
        this.retain = message.isRetain();
        this.body = message.getBody();
        this.connectTime = message.getTime();
        this.channelId= Optional.ofNullable(message.getMqttChannel()).map(MqttChannel::getId).orElse(0);
        this.clientId= message.getClientId();
    }

    public PublishMessage toPublishMessage() {
        PublishMessage publishMessage = new PublishMessage();
        publishMessage.setTopic(this.originTopic);
        publishMessage.setQos(this.qos);
        publishMessage.setRetain(this.retain);
        publishMessage.setBody(this.body);
        return  publishMessage;
    }
}
