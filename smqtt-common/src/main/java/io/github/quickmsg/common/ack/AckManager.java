package io.github.quickmsg.common.ack;

import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.message.mqtt.RetryMessage;

/**
 * @author luxurong
 */
public interface AckManager {

    void addAck(Ack ack);

    Ack getAck(Long id);

    void deleteAck(Long id);

   void doRetry(long id,  RetryMessage retrymessage);


}
