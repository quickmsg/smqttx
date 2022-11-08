package io.github.quickmsg.common.message.mqtt;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import lombok.Data;

import java.util.Date;

/**
 * @author luxurong
 */
@Data
public class DisConnectMessage implements Message {


    private String clientId;


    private String time;

    private String event = "disconnect";

    @JsonIgnore
    private MqttChannel mqttChannel;


    private DisConnectMessage() {
    }

    public static DisConnectMessage INSTANCE = new DisConnectMessage();

    @Override
    public int getMessageId() {
        return 0;
    }


    public DisConnectMessage(  MqttChannel mqttChannel){
        this.clientId = mqttChannel.getClientId();
        this.mqttChannel=mqttChannel;
        this.time = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);

    }


}
