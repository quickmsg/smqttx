package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.acl.AclAction;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.enums.ChannelEvent;
import io.github.quickmsg.common.enums.ChannelStatus;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.NoneEvent;
import io.github.quickmsg.common.event.acceptor.ConnectEvent;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.message.mqtt.ConnectMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.spi.registry.EventRegistry;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.github.quickmsg.core.mqtt.MqttReceiveContext;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.Optional;

/**
 * @author luxurong
 */
@Slf4j
public class ConnectProtocol implements Protocol<ConnectMessage> {

    private static final int MILLI_SECOND_PERIOD = 1_000;


    @Override
    public Mono<Event> parseProtocol(ConnectMessage connectMessage, MqttChannel mqttChannel, ContextView contextView) {
        Event event = NoneEvent.INSTANCE;
        try {
            synchronized (connectMessage.getClientId()) {
                MqttReceiveContext mqttReceiveContext = (MqttReceiveContext) contextView.get(ReceiveContext.class);
                EventRegistry eventRegistry = mqttReceiveContext.getEventRegistry();
                String clientIdentifier = connectMessage.getClientId();
                Integrate integrate = mqttReceiveContext.getIntegrate();
                IntegrateChannels channels = integrate.getChannels();
                IntegrateTopics<SubscribeTopic> topics = integrate.getTopics();
                AclManager aclManager = mqttReceiveContext.getAclManager();
                /*check clientIdentifier exist*/
                if (channels.exists(clientIdentifier)) {
                    return mqttChannel.write(
                            MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED)).then(mqttChannel.close()).thenReturn(event);
                }
                /*protocol version support*/
                if (MqttVersion.MQTT_3_1_1 != connectMessage.getVersion()
                        && MqttVersion.MQTT_3_1 != connectMessage.getVersion() && MqttVersion.MQTT_5 != connectMessage.getVersion()) {
                    return mqttChannel.write(
                            MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION)).then(mqttChannel.close()).thenReturn(event);
                }
                /*password check*/
                if (aclManager.auth(clientIdentifier, clientIdentifier, AclAction.CONNECT)) {
                    /*cancel  defer close not authenticate channel */
                    mqttChannel.setConnectMessage(connectMessage);
                    mqttChannel.setStatus(ChannelStatus.ONLINE);
                    mqttChannel.setAuthTime(System.currentTimeMillis());
                    /*registry unread event close channel */
                    mqttChannel.getConnection()
                            .onReadIdle((long) connectMessage.getKeepalive() * MILLI_SECOND_PERIOD << 1,
                                    () -> close(mqttChannel, mqttReceiveContext, eventRegistry));

                    /*registry will message send */
                    mqttChannel.registryClose(channel -> Optional.ofNullable(mqttChannel.getConnectMessage().getWill())
                            .ifPresent(will ->
                                    topics.getObjectsByTopic(will.getWillTopic())
                                            .forEach(subscribeTopic -> {
                                                MqttChannel subscribeChannel = subscribeTopic.getMqttChannel();
                                                MqttQoS mqttQoS = subscribeTopic.minQos(will.getMqttQoS());
                                                subscribeChannel.sendPublish(mqttQoS, will.toPublishMessage());
                                            })));
                    /* do session message*/
                    doSession(mqttChannel, channels, topics);


                    /* registry new channel*/
                    channels.registry(mqttChannel.getConnectMessage().getClientId(), mqttChannel);

                    /* registry close mqtt channel event*/
                    mqttChannel.registryClose(channel -> this.close(mqttChannel, mqttReceiveContext, eventRegistry));

//                mqttChannel.registryClose(ConnectProtocol::accept);

                    eventRegistry.registry(ChannelEvent.CONNECT, mqttChannel, connectMessage, mqttReceiveContext);

                    event = buildConnectEvent(mqttChannel);

                    return mqttChannel.write(MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_ACCEPTED))
                            .then(Mono.fromRunnable(() -> sendOfflineMessage(mqttReceiveContext.getIntegrate().getMessages(), mqttChannel)))
                            .thenReturn(event);
                } else {
                    return mqttChannel.write(
                            MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD)).then(mqttChannel.close()).thenReturn(event);
                }
            }
        } catch (Exception e) {
            log.error("connect error ", e);
        }
        return Mono.just(event);
    }


    @Override
    public Class<ConnectMessage> getClassType() {
        return ConnectMessage.class;
    }

    private Event buildConnectEvent(MqttChannel mqttChannel) {
        ConnectEvent connectEvent = new ConnectEvent();
        ConnectMessage connectMessage = mqttChannel.getConnectMessage();
        connectEvent.setAddress(mqttChannel.getAddress());
        connectEvent.setKeepalive(connectMessage.getKeepalive());
        connectEvent.setClientId(connectMessage.getClientId());
        connectEvent.setUsername(Optional.ofNullable(mqttChannel.getConnectMessage().getAuth())
                .map(MqttChannel.Auth::getUsername)
                .orElse(null));
        connectEvent.setSessionPersistent(!connectMessage.isCleanSession());
        connectEvent.setWill(JacksonUtil.bean2Json(connectMessage.getWill()));
        connectEvent.setTimestamp(mqttChannel.getAuthTime());
        return connectEvent;
    }

    private void sendOfflineMessage(IntegrateMessages messages, MqttChannel mqttChannel) {
        Optional.ofNullable(messages.getSessionMessage(mqttChannel.getConnectMessage().getClientId()))
                .ifPresent(sessionMessages -> {
                    sessionMessages.forEach(sessionMessage -> {
                        mqttChannel.sendPublish(MqttQoS.valueOf(sessionMessage.getQos()),
                                sessionMessage.toPublishMessage());
                    });
                    messages.deleteSessionMessage(mqttChannel.getConnectMessage().getClientId());
                });
    }


    private void close(MqttChannel mqttChannel, MqttReceiveContext mqttReceiveContext, EventRegistry eventRegistry) {
        synchronized (mqttChannel.getConnectMessage().getClientId()) {
            mqttChannel.setStatus(ChannelStatus.OFFLINE);
            if (mqttChannel.getConnectMessage().isCleanSession()) {
                mqttChannel.getTopics()
                        .forEach(subscribeTopic ->
                                mqttReceiveContext
                                        .getIntegrate()
                                        .getTopics()
                                        .removeTopic(subscribeTopic.getTopicFilter(), subscribeTopic));
                mqttReceiveContext.getIntegrate().getChannels().close(mqttChannel);
            }
            mqttReceiveContext.getRetryManager().clearRetry(mqttChannel);
            eventRegistry.registry(ChannelEvent.CLOSE, mqttChannel, null, mqttReceiveContext);
            mqttChannel.close().subscribe();
        }
    }

    /**
     * session
     *
     * @param mqttChannel new channel      {@link MqttChannel}
     * @param channels    {@link IntegrateChannels}
     * @param topics      {@link IntegrateTopics}
     */
    private void doSession(MqttChannel mqttChannel,
                           IntegrateChannels channels,
                           IntegrateTopics<SubscribeTopic> topics) {
        MqttChannel sessionChannel = channels.get(mqttChannel.getConnectMessage().getClientId());
        if (sessionChannel != null) {
            sessionChannel
                    .getTopics()
                    .forEach(subscribeTopic -> {
                        topics.registryTopic(subscribeTopic.getTopicFilter(),
                                new SubscribeTopic(subscribeTopic.getTopicFilter(),
                                        subscribeTopic.getQoS(), mqttChannel));
                        topics.removeTopic(subscribeTopic.getTopicFilter(), subscribeTopic);
                    });
            channels.close(sessionChannel);
        }
    }

}
