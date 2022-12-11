package io.github.quickmsg.common.message.mqtt;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.common.utils.MessageUtils;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

/**
 * @author luxurong
 */
@Data
public class PublishMessage implements Message {

    private int messageId;

    private String event = "publish";

    private String topic;

    private int qos;

    private boolean retain;

    private Object body;

    private String time;

    private String clientId;

    @JsonIgnore
    private MqttChannel mqttChannel;


    public MqttPublishMessage buildMqttMessage(MqttQoS qoS, int messageId) {
        return MqttMessageUtils.buildPub(false, qoS, this.retain, messageId,
                this.getTopic(), PooledByteBufAllocator.DEFAULT.buffer().writeBytes(JacksonUtil.dynamicJson(this.body).getBytes(StandardCharsets.UTF_8)));
    }

    public MqttPublishMessage buildMqttMessage(MqttQoS qoS, int messageId,boolean isDup) {
        return MqttMessageUtils.buildPub(isDup, qoS, this.retain, messageId, this.getTopic(),
                PooledByteBufAllocator.DEFAULT.buffer().writeBytes(JacksonUtil.dynamicJson(this.body).getBytes(StandardCharsets.UTF_8)));
    }

    public PublishMessage() {
    }

    public PublishMessage(Object message, MqttChannel mqttChannel) {
        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) message;
        this.mqttChannel = mqttChannel;
        this.messageId = mqttPublishMessage.variableHeader().packetId();
        this.topic = mqttPublishMessage.variableHeader().topicName();
        this.qos = mqttPublishMessage.fixedHeader().qosLevel().value();
        this.retain = mqttPublishMessage.fixedHeader().isRetain();
        this.body = JacksonUtil.dynamic(new String(MessageUtils.readByteBuf(mqttPublishMessage.payload()),StandardCharsets.UTF_8));
        this.time = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
        this.clientId = Optional.ofNullable(mqttChannel)
                    .map(MqttChannel::getClientId).orElse(null);
    }

}
