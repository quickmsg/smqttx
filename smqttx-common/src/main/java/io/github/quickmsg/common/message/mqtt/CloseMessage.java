package io.github.quickmsg.common.message.mqtt;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luxurong
 */
@Data
public class CloseMessage implements Message {

    private int messageId;
    private String time;

    private String reason;

    @JsonIgnore
    private MqttChannel mqttChannel;

    private String event = "close";


    public CloseMessage(){
        this.time = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
    }

}
