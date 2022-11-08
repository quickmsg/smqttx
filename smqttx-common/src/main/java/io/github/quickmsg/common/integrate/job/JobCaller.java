package io.github.quickmsg.common.integrate.job;

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

}
