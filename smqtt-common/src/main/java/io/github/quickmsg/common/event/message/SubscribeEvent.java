package io.github.quickmsg.common.event.message;

import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * @author luxurong
 */


@Data
public class SubscribeEvent extends MessageEvent {


    @QuerySqlField(index = true)
    private String type;

    @QuerySqlField(index = true)
    private String clientIdentifier;

    @QuerySqlField(index = true)
    private String topic;

    @QuerySqlField(index = true)
    private String qos;

    @QuerySqlField(index = true, descending = true)
    private long timestamp;

}
