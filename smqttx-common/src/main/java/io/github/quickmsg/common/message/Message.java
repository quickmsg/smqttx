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
        public String getConnectTime() {
            return null;
        }

        @Override
        public MqttChannel getMqttChannel() {
            return null;
        }

    };

    int getMessageId();

    String getConnectTime();

    MqttChannel getMqttChannel();

}
