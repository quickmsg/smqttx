package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.message.Message;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class CLoseMessage implements Message {

    private int messageId;

    private long timestamp;

    private String clientId;

    private String reason;

    @Override
    public Message fromMqttMessage(Object message) {
        throw new UnsupportedOperationException("not support fromMqttMessage CLoseMessage");
    }
}
