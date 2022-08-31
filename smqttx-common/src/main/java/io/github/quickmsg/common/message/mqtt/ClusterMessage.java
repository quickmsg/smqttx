package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class ClusterMessage implements Message {

    private int messageId;

    private String topic;

    private int qos;

    private boolean retain;

    private byte[] body;

    private long timestamp;


    @JsonIgnore
    private MqttChannel mqttChannel;

    @Override
    public int getMessageId() {
        return 0;
    }



    public ClusterMessage(PublishMessage message) {
        this.messageId = message.getMessageId();
        this.topic = message.getTopic();
        this.qos = message.getQos();
        this.retain = message.isRetain();
        this.body = message.getBody();
        this.timestamp = System.currentTimeMillis();
        this.mqttChannel = message.getMqttChannel();
    }

    public PublishMessage toPublishMessage() {
        PublishMessage publishMessage = new PublishMessage();
        publishMessage.setMessageId(this.messageId );
        publishMessage.setTopic(this.topic);
        publishMessage.setQos(this.qos);
        publishMessage.setRetain(this.retain);
        publishMessage.setBody(this.body);
        publishMessage.setMqttChannel(this.mqttChannel);
        return  publishMessage;
    }
}
