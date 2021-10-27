package io.github.quickmsg.common.interate1.job;

/**
 * @author luxurong
 */
public interface Job extends Runnable {

    /**
     * job名称
     *
     * @return String
     */
    String getJobName();

    /**
     * 是否广播
     *
     * @return Boolean
     */
    Boolean isBroadcast();





}
