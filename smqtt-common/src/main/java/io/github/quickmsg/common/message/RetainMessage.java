package io.github.quickmsg.common.message;

import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
@Builder
public class RetainMessage {

    private String clientId;

    private String topic;

    private int qos;

    private boolean retain;

    private byte[] body;

    private long timestamp;

    public static RetainMessage of(PublishMessage message) {
        return RetainMessage.builder()
                .topic(message.getTopic())
                .qos(message.getQos())
                .body(message.getBody())
                .clientId(message.getClientId())
                .retain(message.isRetain())
                .timestamp(message.getTimestamp())
                .build();
    }

    public  PublishMessage toPublishMessage() {
        PublishMessage publishMessage =new PublishMessage();
        publishMessage.setBody(this.body);
        publishMessage.setTopic(this.topic);
        publishMessage.setRetain(this.retain);
        publishMessage.setClientId(this.clientId);
        publishMessage.setQos(this.qos);
        return publishMessage;
    }


}
