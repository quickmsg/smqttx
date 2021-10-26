package io.github.quickmsg.interate;

import io.github.quickmsg.common.interate1.job.JobExecutor;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteRunnable;

import java.util.Collection;

/**
 * @author luxurong
 */
public class IgniteJobExecutor implements JobExecutor {

    private final IgniteCompute igniteCompute;

    public IgniteJobExecutor(IgniteCompute igniteCompute) {
        this.igniteCompute = igniteCompute;
    }


    @Override
    public void execute(IgniteRunnable runnable) {
        igniteCompute.run();
        igniteCompute.execute()
    }

    @Override
    public <V> Collection<V> execute(IgniteCallable<V> callable) {
        return null;
    }
}
