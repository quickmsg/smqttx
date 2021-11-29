package io.github.quickmsg.common.message.mqtt;

import lombok.Data;

import java.util.List;

/**
 * @author luxurong
 */

@Data
public class UnSubscribeMessage {

    private List<String> topics;


}
