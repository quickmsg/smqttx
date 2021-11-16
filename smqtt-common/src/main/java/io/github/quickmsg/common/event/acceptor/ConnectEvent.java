package io.github.quickmsg.common.event.acceptor;

import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * @author luxurong
 */

@Data
public class ConnectEvent extends MessageEvent {

    @QuerySqlField(index = true)
    private String type;

    @QuerySqlField(index = true, descending = true)
    private long timestamp;

    @QuerySqlField(index = true)
    private String clientId;

    @QuerySqlField
    private boolean sessionPersistent;

    @QuerySqlField
    private String will;

    @QuerySqlField
    private long keepalive;

    @QuerySqlField
    private String username;

    @QuerySqlField
    private String address;


}
