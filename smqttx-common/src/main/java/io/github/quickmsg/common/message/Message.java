package io.github.quickmsg.common.message;

import io.github.quickmsg.common.channel.MqttChannel;

import java.util.Map;

/**
 * @author luxurong
 */

public interface Message {

    Message EMPTY_MESSAGE = new Message() {
        @Override
        public String getEvent() {
            return null;
        }

        @Override
        public int getMessageId() {
            return 0;
        }

        @Override
        public String getTime() {
            return null;
        }

        @Override
        public MqttChannel getMqttChannel() {
            return null;
        }
    };

    String getEvent();

    int getMessageId();

    String getTime();

    MqttChannel getMqttChannel();

}
