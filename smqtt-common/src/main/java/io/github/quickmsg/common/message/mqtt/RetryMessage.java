package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luxurong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetryMessage implements Message {

    private int messageId;

    private long timestamp;


    private boolean isRetain;

    private String topic;

    private MqttQoS mqttQoS;

    private byte[] body;


    private MqttChannel mqttChannel;

    @JsonIgnore
    private ReceiveContext<?>  context;

}
