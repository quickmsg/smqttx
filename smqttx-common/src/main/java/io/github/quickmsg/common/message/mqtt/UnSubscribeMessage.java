package io.github.quickmsg.common.message.mqtt;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author luxurong
 */

@Data
public class UnSubscribeMessage implements Message {

    private int messageId;
    private String time;

    private String event = "unsubscribe";

    private String clientId;

    private List<String> topics;

    @JsonIgnore
    private MqttChannel mqttChannel;

    public UnSubscribeMessage(Object message,MqttChannel mqttChannel) {
        this.clientId =mqttChannel.getClientId();
        this.mqttChannel=mqttChannel;
        MqttUnsubscribeMessage unsubscribeMessage = (MqttUnsubscribeMessage) message;
        this.messageId = unsubscribeMessage.variableHeader().messageId();
        this.topics = unsubscribeMessage.payload().topics();
        this.time = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
    }
}
