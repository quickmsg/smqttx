package io.github.quickmsg.common.event.acceptor;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.List;

/**
 * @author luxurong
 */


@Data
@AllArgsConstructor
public class SubscribeAckEvent extends MessageEvent {


    @QuerySqlField(index = true)
    private String clientIdentifier;

    @QuerySqlField(index = true)
    private Integer messageId;

    @QuerySqlField(index = true)
    private List<Integer> grantQosLevels;

    @QuerySqlField(index = true, descending = true)
    private long timestamp;

}
