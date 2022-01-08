package io.github.quickmsg.common.event.acceptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * @author luxurong
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PingEvent extends MessageEvent {

    @QuerySqlField(index = true)
    private String clientId;

    @QuerySqlField(index = true, descending = true)
    private long timestamp;


}
