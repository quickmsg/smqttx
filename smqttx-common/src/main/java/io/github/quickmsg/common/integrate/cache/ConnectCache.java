package io.github.quickmsg.common.integrate.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.Data;

/**
 * @author luxurong
 */

@Data
public class ConnectCache {

    private String clientId;

    private String clientAddress;

    private String nodeIp;

    private MqttVersion version;

    private int keepalive;

    private boolean cleanSession;

    private String connectTime;

    private MqttChannel.Auth auth;

    private MqttChannel.Will will;
}
