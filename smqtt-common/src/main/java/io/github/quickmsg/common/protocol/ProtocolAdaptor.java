package io.github.quickmsg.common.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.interceptor.Intercept;
import io.github.quickmsg.common.interceptor.MessageProxy;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.spi.loader.DynamicLoader;

/**
 * @author luxurong
 */
public interface ProtocolAdaptor {

    ProtocolAdaptor INSTANCE = DynamicLoader.findFirst(ProtocolAdaptor.class).orElse(null);


    MessageProxy MESSAGE_PROXY = new MessageProxy();


    /**
     * 分发某种协议下  消息类型
     *
     * @param mqttChannel    {@link MqttChannel}
     * @param message        {@link mqttChannel}
     * @param receiveContext {@link ReceiveContext}
     * @param <C>            {@link Configuration}
     */
    @Intercept
    <C extends Configuration> void chooseProtocol(MqttChannel mqttChannel, Message message, ReceiveContext<C> receiveContext);


    /**
     * 代理类  用来注入 filter monitor
     *
     * @return {@link ProtocolAdaptor}
     */
    default ProtocolAdaptor proxy() {
        return MESSAGE_PROXY.proxy(this);
    }


}
