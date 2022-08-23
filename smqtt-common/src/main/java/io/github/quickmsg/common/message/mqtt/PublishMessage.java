package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.utils.MessageUtils;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PublishMessage implements Message {

    private String clientId;

    private int messageId;

    private String topic;

    private int qos;

    private boolean retain;

    private byte[] body;

    private long timestamp;


    public MqttPublishMessage buildMqttMessage(MqttQoS qoS, int messageId) {
        return MqttMessageUtils.buildPub(false, qoS, this.retain, messageId, this.getTopic(), PooledByteBufAllocator.DEFAULT.buffer().writeBytes(body));
    }

    public PublishMessage(){}

    public PublishMessage(Object message, String clientId){
        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage)message;
        this.messageId=mqttPublishMessage.variableHeader().packetId();
        this.clientId = clientId;
        this.topic = mqttPublishMessage.variableHeader().topicName();
        this.qos = mqttPublishMessage.fixedHeader().qosLevel().value();
        this.retain = mqttPublishMessage.fixedHeader().isRetain();
        this.body = MessageUtils.readByteBuf(mqttPublishMessage.payload());
        this.timestamp = System.currentTimeMillis();
    }


}
