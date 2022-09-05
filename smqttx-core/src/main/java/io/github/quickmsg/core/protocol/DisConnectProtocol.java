package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.mqtt.DisConnectMessage;
import io.github.quickmsg.common.protocol.Protocol;
import reactor.netty.Connection;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class DisConnectProtocol implements Protocol<DisConnectMessage> {


    @Override
    public void parseProtocol(DisConnectMessage message, MqttChannel mqttChannel, ContextView contextView) {
        mqttChannel.getConnectCache().setWill(null);
        Connection connection;
        if (!(connection = mqttChannel.getConnection()).isDisposed()) {
            connection.dispose();
        }
    }

    @Override
    public Class<DisConnectMessage> getClassType() {
        return DisConnectMessage.class;
    }


}
