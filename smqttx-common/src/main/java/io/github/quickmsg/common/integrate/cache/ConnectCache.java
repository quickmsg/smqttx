package io.github.quickmsg.common.integrate.cache;

import io.github.quickmsg.common.channel.MqttChannel;
import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

/**
 * @author luxurong
 */

@Data
public class ConnectCache {

    @QuerySqlField(index = true)
    private String clientId;

    @QuerySqlField
    private String clientAddress;

    @QuerySqlField
    private String nodeIp;

    @QuerySqlField
    private MqttVersion version;

    @QuerySqlField
    private int keepalive;

    @QuerySqlField
    private boolean cleanSession;

    @QuerySqlField( descending = true)
    private String connectTime;

    @QuerySqlField
    private MqttChannel.Auth auth;

    @QuerySqlField
    private MqttChannel.Will will;
}
