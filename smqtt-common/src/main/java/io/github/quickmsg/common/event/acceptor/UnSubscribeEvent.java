package io.github.quickmsg.common.event.acceptor;

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
public class UnSubscribeEvent extends MessageEvent {


    @QuerySqlField(index = true)
    private String clientIdentifier;


    @QuerySqlField(index = true)
    private int messageId;

    @QuerySqlField
    private List<String> topics;


    @QuerySqlField(index = true, descending = true)
    private long timestamp;


}
