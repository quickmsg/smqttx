package io.github.quickmsg.common.event.acceptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * @author luxurong
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CommonEvent extends MessageEvent {

    @QuerySqlField(index = true)
    private String type;

    @QuerySqlField(index = true)
    private String clientId;

    @QuerySqlField(index = true)
    private int messageId;

    @QuerySqlField(index = true, descending = true)
    private long timestamp;




}
