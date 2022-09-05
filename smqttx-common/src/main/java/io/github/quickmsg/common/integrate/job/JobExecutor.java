package io.github.quickmsg.common.integrate.job;

import java.util.Collection;

/**
 * @author luxurong
 */
public interface JobExecutor {

    void execute(Job job);

    <R> Collection<R> callBroadcast(JobCaller<R> callable);

    <INPUT,OUT> Collection<OUT> callBroadcast(JobClosure<INPUT,OUT> callable,INPUT input);


    <R> R call(JobCaller<R> callable);

}
