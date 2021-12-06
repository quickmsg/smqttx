package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PublishRelMessage implements Message {

    private int messageId;

}
