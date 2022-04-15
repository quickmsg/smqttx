package io.github.quickmsg.common.retry;

import io.github.quickmsg.common.message.mqtt.RetryMessage;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.Setter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author luxurong
 */
public class RetryTask implements TimerTask {

    private final RetryMessage retryMessage;

    private final int retrySize;

    private final int retryPeriod;

    @Setter
    private Timeout timeout;

    public RetryTask(RetryMessage retryMessage, int retrySize, int retryPeriod) {
        this.retryMessage = retryMessage;
        this.retrySize = retrySize;
        this.retryPeriod = retryPeriod;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (!timeout.isCancelled() && retryMessage.getCount() < retrySize) {
            retryMessage.retry();
            this.timeout = timeout.timer().newTimeout(this, retryPeriod, TimeUnit.SECONDS);
        }
        else{
            retryMessage.clear();
        }
    }


    public void cancel(){
        Optional.ofNullable(timeout).ifPresent(Timeout::cancel);
    }
}
