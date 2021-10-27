package io.github.quickmsg.common.interate1.job;

import java.util.concurrent.Callable;

/**
 * @author luxurong
 */
public interface JobCaller<R>  extends Callable<R> {

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
