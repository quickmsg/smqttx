package io.github.quickmsg.common.message;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.netty.handler.codec.mqtt.MqttMessage;

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
        public MqttChannel getMqttChannel() {
            return null;
        }

        @Override
        public ReceiveContext<?> getContext() {
            return null;
        }

        @Override
        public long getTimestamp() {
            return System.currentTimeMillis();
        }

    };

    int getMessageId();

    MqttChannel getMqttChannel();

    ReceiveContext<?> getContext();

    long getTimestamp();

}
