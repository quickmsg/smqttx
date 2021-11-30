package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luxurong
 */

@Data
@Slf4j
public class ConnectMessage implements Message {

    private boolean logger;

    private String clientId;

    private MqttVersion version;

    private String username;

    private byte[] password;

    private int keepalive;

    private boolean cleanSession;

    private MqttChannel.Will will;

    @Override
    public int getMessageId() {
        return 0;
    }
}
