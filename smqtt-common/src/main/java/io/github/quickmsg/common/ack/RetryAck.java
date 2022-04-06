package io.github.quickmsg.common.ack;

/**
 * @author luxurong
 */

public class RetryAck extends AbsAck {

    private final long id;

    private final int channelId;

    public RetryAck(int channelId, long id, int maxRetrySize, int period, Runnable runnable, AckManager ackManager) {
        super(maxRetrySize, period, runnable, ackManager);
        this.id = id;
        this.channelId = channelId;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public int getChannelId() {
        return this.channelId;
    }
}
