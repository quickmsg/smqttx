package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.message.Message;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class DisConnectMessage implements Message {



    private DisConnectMessage() {
    }

    public static DisConnectMessage INSTANCE = new DisConnectMessage();

    @Override
    public int getMessageId() {
        return 0;
    }

}
