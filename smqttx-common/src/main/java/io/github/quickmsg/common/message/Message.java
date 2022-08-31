package io.github.quickmsg.common.message;

import io.github.quickmsg.common.channel.MqttChannel;

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
        public long getTimestamp() {
            return System.currentTimeMillis();
        }

        @Override
        public MqttChannel getMqttChannel() {
            return null;
        }

    };

    int getMessageId();

    long getTimestamp();

    MqttChannel getMqttChannel();

}
