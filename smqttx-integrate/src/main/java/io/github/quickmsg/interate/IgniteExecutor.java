package io.github.quickmsg.interate;

import io.github.quickmsg.common.integrate.job.Job;
import io.github.quickmsg.common.integrate.job.JobCaller;
import io.github.quickmsg.common.integrate.job.JobClosure;
import io.github.quickmsg.common.integrate.job.JobExecutor;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteRunnable;

import java.util.Collection;

/**
 * @author luxurong
 */
public class IgniteExecutor implements JobExecutor {

    private final IgniteCompute igniteCompute;

    public IgniteExecutor(IgniteCompute igniteCompute) {
        this.igniteCompute = igniteCompute;
    }


    @Override
    public void execute(Job job) {
        IgniteRunnable runnable = job::run;
        if (job.isBroadcast()) {
            igniteCompute.broadcast(runnable);
        } else {
            igniteCompute.run(runnable);
        }
    }

    @Override
    public <R> Collection<R> callBroadcast(JobCaller<R> callable) {
        IgniteCallable<R> igniteCallable = callable::call;
        return igniteCompute.broadcast(igniteCallable);
    }

    @Override
    public <INPUT, OUT> Collection<OUT> callBroadcast(JobClosure<INPUT, OUT> callable,INPUT input) {
        return this.igniteCompute.broadcast(callable,input);
    }

    @Override
    public <R> R call(JobCaller<R> callable) {
        IgniteCallable<R> igniteCallable = callable::call;
        return igniteCompute.call(igniteCallable);
    }

}
