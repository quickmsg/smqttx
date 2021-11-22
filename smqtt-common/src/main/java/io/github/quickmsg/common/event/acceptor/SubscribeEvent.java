package io.github.quickmsg.common.event.acceptor;

import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.List;

/**
 * @author luxurong
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeEvent extends MessageEvent {


    @QuerySqlField(index = true)
    private String type;

    @QuerySqlField(index = true)
    private String clientIdentifier;

    @QuerySqlField(index = true)
    private List<MqttTopicSubscription> topicSubscriptions;

    @QuerySqlField(index = true, descending = true)
    private long timestamp;

}
