package io.github.quickmsg.common.integrate.job;

import java.util.Collection;

/**
 * @author luxurong
 */
public interface JobExecutor {

    void execute(Job job);

    <R> Collection<R> callBroadcast(JobCaller<R> callable);

    <R> R call(JobCaller<R> callable);

}
