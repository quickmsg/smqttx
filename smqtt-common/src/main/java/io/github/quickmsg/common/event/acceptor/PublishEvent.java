package io.github.quickmsg.common.event.acceptor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ignite.cache.query.annotations.QuerySqlField;


/**
 * @author luxurong
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishEvent extends MessageEvent {

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
    private String body;

}
