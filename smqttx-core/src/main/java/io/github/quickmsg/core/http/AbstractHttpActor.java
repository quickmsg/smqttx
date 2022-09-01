package io.github.quickmsg.core.http;

import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.http.HttpActor;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * @author luxurong
 */
public abstract class AbstractHttpActor implements HttpActor {

    /**
     * 发送mqtt消息
     *
     * @param mqttPublishMessage publish消息
     */
    public void sendMqttMessage(MqttPublishMessage mqttPublishMessage) {
        ContextHolder
                .getReceiveContext()
                .getProtocolAdaptor()
                .chooseProtocol(new PublishMessage(mqttPublishMessage, null));
    }
}
