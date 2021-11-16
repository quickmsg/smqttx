package io.github.quickmsg.common.event.acceptor;

import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * @author luxurong
 */

@Data
public class UnSubscribeEvent extends MessageEvent {

    @QuerySqlField(index = true)
    private String type;

    @QuerySqlField(index = true)
    private String clientIdentifier;

    @QuerySqlField(index = true)
    private String topic;

    @QuerySqlField(index = true, descending = true)
    private long timestamp;


}
