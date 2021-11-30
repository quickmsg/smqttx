package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.message.Message;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PublishRecMessage implements Message {

    private int messageId;

}
