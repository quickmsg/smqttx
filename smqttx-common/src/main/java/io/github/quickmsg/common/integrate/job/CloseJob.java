package io.github.quickmsg.common.integrate.job;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;

/**
 * @author luxurong
 */
public class CloseJob implements Job {


    private final String clientId;

    public CloseJob(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getJobName() {
        return clientId;
    }

    @Override
    public Boolean isBroadcast() {
        return true;
    }

    @Override
    public void run() {
        IntegrateChannels channels = ContextHolder.getReceiveContext()
                .getIntegrate().getChannels();
        MqttChannel mqttChannel = channels.get(clientId);
        if (mqttChannel != null) {
            channels.remove(mqttChannel);
            mqttChannel.close();
        }
    }
}
