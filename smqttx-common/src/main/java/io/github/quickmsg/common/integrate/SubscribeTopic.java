package io.github.quickmsg.common.integrate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
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


    @JsonIgnore
    private MqttChannel mqttChannel;

    public SubscribeTopic(String topicFilter, MqttQoS qoS) {
        this.topicFilter = topicFilter;
        this.qoS = qoS;
    }

    public SubscribeTopic(String topicFilter, MqttQoS qoS, MqttChannel mqttChannel) {
        this.topicFilter = topicFilter;
        this.qoS = qoS;
        this.mqttChannel = mqttChannel;
    }


    public SubscribeTopic setMqttChannel(MqttChannel mqttChannel) {
        this.mqttChannel = mqttChannel;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscribeTopic that = (SubscribeTopic) o;
        return Objects.equals(topicFilter, that.topicFilter) && Objects.equals( this.mqttChannel.getClientId(), that.getMqttChannel().getClientId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicFilter, this.mqttChannel.getClientId());
    }

    public MqttQoS minQos(MqttQoS mqttQoS) {
        return MqttQoS.valueOf(Math.min(mqttQoS.value(), qoS.value()));
    }

    @Override
    public String toString() {
        return "SubscribeTopic{" +
                "topicFilter='" + topicFilter + '\'' +
                ", qoS=" + qoS +
                ", clientId=" + this.mqttChannel.getClientId() +
                '}';
    }
}
