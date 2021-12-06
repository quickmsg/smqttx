package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.message.Message;

/**
 * @author luxurong
 */
public class CLoseMessage implements Message {
    @Override
    public int getMessageId() {
        return 0;
    }
}
