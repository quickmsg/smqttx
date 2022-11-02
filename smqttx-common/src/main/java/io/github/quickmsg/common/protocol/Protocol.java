package io.github.quickmsg.common.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.message.SmqttMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public interface Protocol<T extends Message> {


    /**
     * 解析协议添加上下文
     *
     * @param message     {@link SmqttMessage}
     * @param mqttChannel {@link MqttChannel}
     * @return Mono
     * @see MqttMessage
     */
    default Mono<Void> doParseProtocol(T message, MqttChannel mqttChannel) {
        return Mono.deferContextual(contextView -> {
            this.parseProtocol(message, mqttChannel, contextView);
            return Mono.empty();
        });
    }


    /**
     * 处理协议
     *
     * @param message     {@link T extends Message}
     * @param mqttChannel {@link MqttChannel}
     * @param contextView {@link ContextView}
     * @see MqttMessage
     */
    void parseProtocol(T message, MqttChannel mqttChannel, ContextView contextView);


    /**
     * @return Class
     */
    Class<T> getClassType();


}
