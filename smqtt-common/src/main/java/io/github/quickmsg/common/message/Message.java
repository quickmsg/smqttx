package io.github.quickmsg.common.message;

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

    };

    int getMessageId();

    Message fromMqttMessage(Object message);


}
