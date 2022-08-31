package io.github.quickmsg.common.enums;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.message.system.ChannelStatusMessage;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.Optional;

/**
 * @author luxurong
 */
public enum ChannelEvent {


    /**
     * 连接事件
     */
    CONNECT {
        private static final String CONNECT_TOPIC = "$event/connect";

        @Override
        public void sender(MqttChannel mqttChannel, Message message, ReceiveContext<?> receiveContext) {
            write(receiveContext, mqttChannel, message);
        }

        @Override
        public ByteBuf writeBody(MqttChannel mqttChannel, Message message) {
            return PooledByteBufAllocator.DEFAULT
                    .directBuffer().writeBytes(JacksonUtil.bean2Json(new ChannelStatusMessage(
                            mqttChannel.getConnectMessage().getClientId(),
                            System.currentTimeMillis(),
                            Optional.ofNullable(mqttChannel.getConnectMessage().getAuth())
                                    .map(MqttChannel.Auth::getUsername)
                                    .orElse(null),
                            ChannelStatus.ONLINE)).getBytes());
        }

    },
    /**
     * 关闭事件
     */
    CLOSE {
        private static final String CLOSE_TOPIC = "$event/close";

        @Override
        public void sender(MqttChannel mqttChannel, Message message, ReceiveContext<?> receiveContext) {
            write(receiveContext, mqttChannel, message);
        }

        @Override
        public ByteBuf writeBody(MqttChannel mqttChannel, Message message) {
            return PooledByteBufAllocator.DEFAULT
                    .directBuffer().writeBytes(JacksonUtil.bean2Json(new ChannelStatusMessage(
                            mqttChannel.getConnectMessage().getClientId(),
                            System.currentTimeMillis(),
                            Optional.ofNullable(mqttChannel.getConnectMessage().getAuth())
                                    .map(MqttChannel.Auth::getUsername)
                                    .orElse(null),
                            ChannelStatus.OFFLINE)).getBytes());
        }
    };

    /**
     * write event
     *
     * @param mqttChannel    {@link MqttChannel }
     * @param message        {@link Message }
     * @param receiveContext {@link ReceiveContext }
     */
    public abstract void sender(MqttChannel mqttChannel, Message message, ReceiveContext<?> receiveContext);


    /**
     * body
     *
     * @param mqttChannel {@link MqttChannel }
     * @param message     {@link Message }
     * @return ByteBuf
     */
    public abstract ByteBuf writeBody(MqttChannel mqttChannel, Message message);


    public void write(ReceiveContext<?> receiveContext, MqttChannel mqttChannel, Message message) {
//        receiveContext.getProtocolAdaptor()
//                .chooseProtocol(mqttChannel, message, receiveContext);
    }

}
