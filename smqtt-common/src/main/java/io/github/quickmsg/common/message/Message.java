package io.github.quickmsg.common.message;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;

/**
 * @author luxurong
 */

public interface Message {

    Message EMPTY_MESSAGE = new Message() {
        @Override
        public int getMessageId() {
            return 0;
        }

        @Override
        public Message fromMqttMessage(Object message) {
            return null;
        }

        @Override
        public MqttChannel getMqttChannel() {
            return null;
        }

        @Override
        public ReceiveContext<?> getContext() {
            return null;
        }

    };

    int getMessageId();

    Message fromMqttMessage(Object message);

    MqttChannel getMqttChannel();

    ReceiveContext<?> getContext();


}
