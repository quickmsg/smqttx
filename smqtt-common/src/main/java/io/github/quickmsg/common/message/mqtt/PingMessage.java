package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.message.Message;
import lombok.Data;

/**
 * @author luxurong
 */

@Data
public class PingMessage implements Message {



    private PingMessage() {
    }

    public static PingMessage INSTANCE = new PingMessage();

    @Override
    public int getMessageId() {
        return 0;
    }

    @Override
    public boolean isCluster() {
        return false;
    }
}
