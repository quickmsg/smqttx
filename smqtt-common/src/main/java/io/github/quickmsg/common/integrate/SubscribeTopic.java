package io.github.quickmsg.common.integrate;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ContextHolder;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author luxurong
 */

@Getter
@Setter
public class SubscribeTopic {

    private final String topicFilter;

    private final MqttQoS qoS;

    private String clientId;

    public SubscribeTopic(String topicFilter, MqttQoS qoS) {
        this.topicFilter = topicFilter;
        this.qoS = qoS;
    }

    public SubscribeTopic(String topicFilter, MqttQoS qoS, String clientId) {
        this.topicFilter = topicFilter;
        this.qoS = qoS;
        this.clientId = clientId;
    }


    public SubscribeTopic setMqttChannel(String clientId) {
        this.clientId = clientId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscribeTopic that = (SubscribeTopic) o;
        return Objects.equals(topicFilter, that.topicFilter) && Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicFilter, clientId);
    }

    public MqttQoS minQos(MqttQoS mqttQoS) {
        return MqttQoS.valueOf(Math.min(mqttQoS.value(), qoS.value()));
    }

    public void linkSubscribe() {
        MqttChannel mqttChannel = ContextHolder.getReceiveContext().getIntegrate().getChannels().get(this.clientId);
        if(mqttChannel!=null){
            mqttChannel.getTopics().add(this);
        }
    }

    public void unLinkSubscribe() {
        MqttChannel mqttChannel = ContextHolder.getReceiveContext().getIntegrate().getChannels().get(this.clientId);
        if(mqttChannel!=null){
            mqttChannel.getTopics().remove(this);
        }
    }

    @Override
    public String toString() {
        return "SubscribeTopic{" +
                "topicFilter='" + topicFilter + '\'' +
                ", qoS=" + qoS +
                ", clientId=" + clientId +
                '}';
    }
}
