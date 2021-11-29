package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.message.Message;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PublishMessage  implements Message {

    private String topic;

    private int qos;

    private boolean retain;

    private String message;


}
