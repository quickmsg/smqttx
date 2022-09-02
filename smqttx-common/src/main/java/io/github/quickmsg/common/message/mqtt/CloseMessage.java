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
public class CloseMessage implements Message {

    private int messageId;
    private String connectTime;

    private String reason;

    @JsonIgnore
    private MqttChannel mqttChannel;


    public CloseMessage(){
        this.connectTime = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
    }

}
