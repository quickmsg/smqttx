package io.github.quickmsg.common.pipeline;

import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * @author luxurong
 */

@Data
public class PipelinePublish {

    @QuerySqlField(index = true)
    private long  id;

    @QuerySqlField
    private long timestamp;

    @QuerySqlField(index = true)
    private String clientIdentifier;

    @QuerySqlField
    private String topic;

    @QuerySqlField
    private int qos;

    @QuerySqlField
    private boolean retain;

    @QuerySqlField
    private byte[] message;

}
