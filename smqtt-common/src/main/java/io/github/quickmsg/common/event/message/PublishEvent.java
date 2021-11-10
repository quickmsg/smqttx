package io.github.quickmsg.common.event.message;

import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;


/**
 * @author luxurong
 */

@Data
public class PublishEvent extends MessageEvent {

    @QuerySqlField(index = true)
    private String type;

    @QuerySqlField(index = true,descending = true)
    private long timestamp;

    @QuerySqlField(index = true)
    private String clientId;

    @QuerySqlField
    private String topic;

    @QuerySqlField(index = true)
    private int qos;

    @QuerySqlField(index = true)
    private boolean retain;

    @QuerySqlField
    private String message;

}
