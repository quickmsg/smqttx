package io.github.quickmsg.common.retry;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.mqtt.RetryMessage;

/**
 * @author luxurong
 */
public interface RetryManager {


   void doRetry(MqttChannel mqttChannel, RetryMessage retrymessage);

   void cancelRetry(MqttChannel mqttChannel, int messageId);

   void clearRetry(MqttChannel mqttChannel);




}
