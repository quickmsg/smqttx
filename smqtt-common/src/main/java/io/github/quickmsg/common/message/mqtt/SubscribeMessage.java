package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.message.Message;
import lombok.Data;

import java.util.List;

/**
 * @author luxurong
 */

@Data
public class SubscribeMessage implements Message {

    private int messageId;

    private List<SubscribeTopic> subscribeTopics;

}
