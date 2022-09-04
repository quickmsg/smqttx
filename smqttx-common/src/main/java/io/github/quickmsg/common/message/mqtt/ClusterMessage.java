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
public class ClusterMessage{

    private int qos;

    private boolean retain;

    private byte[] body;

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
        this.connectTime = message.getConnectTime();
        this.channelId= message.getMqttChannel().getId();
        this.clientId= message.getMqttChannel().getClientId();
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
