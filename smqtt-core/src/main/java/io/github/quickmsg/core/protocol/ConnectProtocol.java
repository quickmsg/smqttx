package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.auth.PasswordAuthentication;
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
import io.github.quickmsg.common.utils.EventMsg;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.github.quickmsg.core.mqtt.MqttReceiveContext;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
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
            MqttReceiveContext mqttReceiveContext = (MqttReceiveContext) contextView.get(ReceiveContext.class);
            EventRegistry eventRegistry = mqttReceiveContext.getEventRegistry();
            String clientIdentifier = connectMessage.getClientId();
            Integrate integrate = mqttReceiveContext.getIntegrate();
            IntegrateChannels channels = integrate.getChannels();
            IntegrateTopics<SubscribeTopic> topics = integrate.getTopics();
            PasswordAuthentication passwordAuthentication = mqttReceiveContext.getPasswordAuthentication();
            /*check clientIdentifier exist*/
            if (channels.exists(clientIdentifier)) {
                return mqttChannel.write(
                        MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED),
                        false).then(mqttChannel.close()).thenReturn(event);
            }
            /*protocol version support*/
            if (MqttVersion.MQTT_3_1_1 != connectMessage.getVersion()
                    && MqttVersion.MQTT_3_1 != connectMessage.getVersion()) {
                return mqttChannel.write(
                        MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION),
                        false).then(mqttChannel.close()).thenReturn(event);
            }
            /*password check*/
            if (passwordAuthentication.auth(connectMessage.getUsername(), connectMessage.getPassword(), clientIdentifier)) {
                /*cancel  defer close not authenticate channel */
                mqttChannel.disposableClose();
                mqttChannel.setClientIdentifier(clientIdentifier);
                Optional.ofNullable(connectMessage.getWill()).ifPresent(mqttChannel::setWill);
                mqttChannel.setAuthTime(System.currentTimeMillis());
                mqttChannel.setKeepalive(connectMessage.getKeepalive());
                mqttChannel.setVersion(connectMessage.getVersion().protocolName());
                mqttChannel.setSessionPersistent(!connectMessage.isCleanSession());
                mqttChannel.setStatus(ChannelStatus.ONLINE);
                mqttChannel.setUsername(connectMessage.getUsername());
                /*registry unread event close channel */

                mqttChannel.getConnection()
                        .onReadIdle((long) connectMessage.getKeepalive() * MILLI_SECOND_PERIOD << 1,
                                () -> close(mqttChannel, mqttReceiveContext, eventRegistry));

                /*registry will message send */
                mqttChannel.registryClose(channel -> Optional.ofNullable(mqttChannel.getWill())
                        .ifPresent(will ->
                                topics.getObjectsByTopic(will.getWillTopic())
                                        .forEach(subscribeTopic -> {
                                            MqttChannel subscribeChannel = subscribeTopic.getMqttChannel();
                                            subscribeChannel.write(
                                                    MqttMessageUtils
                                                            .buildPub(false,
                                                                    subscribeTopic.minQos(will.getMqttQoS()),
                                                                    subscribeTopic.getQoS() == MqttQoS.AT_MOST_ONCE
                                                                            ? 0 : subscribeChannel.generateMessageId(),
                                                                    will.getWillTopic(),
                                                                    Unpooled.wrappedBuffer(will.getWillMessage())
                                                            ), subscribeTopic.getQoS().value() > 0
                                            ).subscribe();
                                        })));
                /* do session message*/
                doSession(mqttChannel, channels, topics);


                /* registry new channel*/
                channels.registry(mqttChannel.getClientIdentifier(), mqttChannel);

                /* registry close mqtt channel event*/
                mqttChannel.registryClose(channel -> this.close(mqttChannel, mqttReceiveContext, eventRegistry));

//                mqttChannel.registryClose(ConnectProtocol::accept);

                eventRegistry.registry(ChannelEvent.CONNECT, mqttChannel, connectMessage, mqttReceiveContext);

                event = buildConnectEvent(mqttChannel);

                return mqttChannel.write(MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_ACCEPTED), false)
                        .then(Mono.fromRunnable(() -> sendOfflineMessage(mqttReceiveContext.getIntegrate().getMessages(), mqttChannel)))
                        .thenReturn(event)
                        ;
            } else {
                return mqttChannel.write(
                        MqttMessageUtils.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD),
                        false).then(mqttChannel.close()).thenReturn(event);
            }
        } catch (Exception e) {
            log.error("connect error ", e);
        }
        return Mono.just(event);
    }

    private Event buildConnectEvent(MqttChannel mqttChannel) {
        ConnectEvent connectEvent = new ConnectEvent();
        connectEvent.setAddress(mqttChannel.getAddress());
        connectEvent.setKeepalive(mqttChannel.getKeepalive());
        connectEvent.setClientId(mqttChannel.getClientIdentifier());
        connectEvent.setUsername(mqttChannel.getUsername());
        connectEvent.setSessionPersistent(mqttChannel.isSessionPersistent());
        connectEvent.setType(EventMsg.CONNECT_MESSAGE);
        connectEvent.setWill(JacksonUtil.bean2Json(mqttChannel.getWill()));
        connectEvent.setTimestamp(mqttChannel.getAuthTime());
        return connectEvent;
    }

    private void sendOfflineMessage(IntegrateMessages messages, MqttChannel mqttChannel) {
        Optional.ofNullable(messages.getSessionMessage(mqttChannel.getClientIdentifier()))
                .ifPresent(sessionMessages -> {
                    sessionMessages.forEach(sessionMessage -> mqttChannel
                            .write(sessionMessage.toPublishMessage(mqttChannel),
                                    sessionMessage.getQos() > 0)
                            .subscribeOn(Schedulers.single())
                            .subscribe());
                });
    }

    private void close(MqttChannel mqttChannel, MqttReceiveContext mqttReceiveContext, EventRegistry eventRegistry) {
        log.info(" 【{}】【{}】 【{}】",
                Thread.currentThread().getName(),
                "CLOSE",
                mqttChannel);
        mqttChannel.setStatus(ChannelStatus.OFFLINE);
        if (!mqttChannel.isSessionPersistent()) {
            mqttChannel.getTopics()
                    .forEach(subscribeTopic ->
                            mqttReceiveContext
                                    .getIntegrate()
                                    .getTopics()
                                    .removeTopic(subscribeTopic.getTopicFilter(), subscribeTopic));
            mqttReceiveContext.getIntegrate().getChannels().close(mqttChannel);
        }
        eventRegistry.registry(ChannelEvent.CLOSE, mqttChannel, null, mqttReceiveContext);
        mqttChannel.close().subscribe();
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
        MqttChannel sessionChannel = channels.get(mqttChannel.getClientIdentifier());
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
