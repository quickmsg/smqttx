package io.github.quickmsg.common.ack;

import io.netty.util.TimerTask;
import reactor.core.Disposable;

import java.util.concurrent.TimeUnit;

/**
 * @author luxurong
 */
public interface Ack extends TimerTask {

    int getTimed();

    TimeUnit getTimeUnit();

    long getId();

    void start();

    void stop();


}
