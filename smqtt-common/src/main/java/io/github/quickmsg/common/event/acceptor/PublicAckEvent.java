package io.github.quickmsg.common.event.acceptor;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * @author luxurong
 */
@Data
@AllArgsConstructor
public class PublicAckEvent extends MessageEvent {


    @QuerySqlField(index = true)
    private String type;

    @QuerySqlField(index = true, descending = true)
    private long timestamp;

    @QuerySqlField(index = true)
    private String clientId;

    @QuerySqlField(index = true)
    private int messageId;
}
