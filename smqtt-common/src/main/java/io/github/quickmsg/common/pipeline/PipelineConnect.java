package io.github.quickmsg.common.pipeline;

import io.github.quickmsg.common.channel.MqttChannel;
import lombok.Data;

/**
 * @author luxurong
 */

@Data
public class PipelineConnect {

    private MqttChannel mqttChannel;

}
