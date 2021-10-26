package io.github.quickmsg.common.interate1.job;

import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteRunnable;

import java.util.Collection;

/**
 * @author luxurong
 */
public interface JobExecutor {

    void execute(IgniteRunnable runnable);

    <V> Collection<V> execute(IgniteCallable<V> callable);

}
