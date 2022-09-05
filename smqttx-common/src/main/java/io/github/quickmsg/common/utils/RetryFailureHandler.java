package io.github.quickmsg.common.utils;

import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;

import java.util.concurrent.locks.LockSupport;

/**
 * @author luxurong
 */
public class RetryFailureHandler implements Sinks.EmitFailureHandler {

    public static final RetryFailureHandler RETRY_NON_SERIALIZED = new RetryFailureHandler();

    public RetryFailureHandler() {
    }

    @Override
    public boolean onEmitFailure(SignalType signalType, Sinks.EmitResult emitResult) {
        LockSupport.parkNanos(10);
        return emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED;
    }
}
