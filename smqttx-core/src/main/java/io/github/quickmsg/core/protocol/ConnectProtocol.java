package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.auth.AuthManager;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.enums.ChannelEvent;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.message.mqtt.ConnectMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.spi.registry.EventRegistry;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.github.quickmsg.core.mqtt.MqttReceiveContext;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

/**
 * @author luxurong
 */
@Slf4j
public class ConnectProtocol implements Protocol<ConnectMessage> {

    private static final int MILLI_SECOND_PERIOD = 1_000;


    @Override
    public void parseProtocol(ConnectMessage connectMessage, MqttChannel mqttChannel, ContextView contextView) {
        MqttReceiveContext mqttReceiveContext = (MqttReceiveContext) contextView.get(ReceiveContext.class);
        EventRegistry eventRegistry = mqttReceiveContext.getEventRegistry();
        String clientIdentifier = connectMessage.getClientId();
        Integrate integrate = mqttReceiveContext.getIntegrate();
        IntegrateChannels channels = integrate.getChannels();
        IntegrateTopics<SubscribeTopic> topics = integrate.getTopics();
        AuthManager aclManager = mqttReceiveContext.getAuthManager();
        /*protocol version support*/
        if (MqttVersion.MQTT_3_1_1 != connectMessage.getVersion()
                && MqttVersion.MQTT_3_1 != connectMessage.getVersion() && MqttVersion.MQTT_5 != connectMessage.getVersion()) {
            mqttChannel.write(MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION));
            return;
        }
        /*password check*/
        if (aclManager.auth(connectMessage.getAuth().getUsername(), connectMessage.getAuth().getPassword(), clientIdentifier)) {
            /*check clientIdentifier exist*/

            mqttChannel.setConnectMessage(connectMessage);
            mqttChannel.setAuthTime(DateFormatUtils.format(new Date(),"yyyy-mm-dd hh:mm:ss"));

            /*registry unread event close channel */
            mqttChannel.getConnection()
                    .onReadIdle((long) connectMessage.getKeepalive() * MILLI_SECOND_PERIOD << 1,
                            mqttChannel::close);

            /* registry new channel*/
            channels.add(mqttChannel.getConnectMessage().getClientId(), mqttChannel);

            /* registry close mqtt channel event*/
            mqttChannel.registryClose(channel -> this.close(mqttChannel, mqttReceiveContext));

            eventRegistry.registry(ChannelEvent.CONNECT, mqttChannel, connectMessage, mqttReceiveContext);

            mqttChannel.write(MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_ACCEPTED));
        } else {
            mqttChannel.write(
                    MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
        }

    }


    @Override
    public Class<ConnectMessage> getClassType() {
        return ConnectMessage.class;
    }



    private void close(MqttChannel mqttChannel, MqttReceiveContext mqttReceiveContext) {
        mqttReceiveContext.getIntegrate().getChannels().remove(mqttChannel);
        IntegrateTopics<SubscribeTopic>  topics=mqttReceiveContext.getIntegrate().getTopics();
        topics.removeTopic(new ArrayList<>(mqttChannel.getTopics()));
        mqttReceiveContext.getRetryManager().clearRetry(mqttChannel);
        Optional.ofNullable(mqttChannel.getConnectMessage().getWill())
                .ifPresent(will ->
                        topics.getObjectsByTopic(will.getWillTopic())
                                .forEach(subscribeTopic -> {
                                    String clientId = subscribeTopic.getClientId();
                                    MqttChannel channel =mqttReceiveContext.getIntegrate().getChannels().get(clientId);
                                    MqttQoS mqttQoS = subscribeTopic.minQos(will.getMqttQoS());
                                    channel.sendPublish(mqttQoS, will.toPublishMessage());
                                }));

    }


}