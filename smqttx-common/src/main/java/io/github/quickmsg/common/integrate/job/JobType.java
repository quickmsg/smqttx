package io.github.quickmsg.common.integrate.job;

/**
 * @author luxurong
 */
public enum JobType {
    /**
     * runnable  broadcast all node execute
     */
    RUNNABLE_BROADCAST,

    /**
     * runnable  signal node execute
     */
    RUNNABLE_SINGLE,

    /**
     * callable  signal node execute
     */
    CALLABLE_SINGLE,

    /**
     * callable  broadcast all node execute
     */
    CALLABLE_BROADCAST
}
