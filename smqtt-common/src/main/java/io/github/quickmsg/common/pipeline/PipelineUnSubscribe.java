package io.github.quickmsg.common.pipeline;

import io.github.quickmsg.common.channel.MqttChannel;
import lombok.Data;

import java.util.List;

/**
 * @author luxurong
 */

@Data
public class PipelineUnSubscribe {

    private MqttChannel channel;

    private List<String> topics;

    private long timestamp;


}
