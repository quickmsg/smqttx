package io.github.quickmsg.interate.hazelcast;

import com.hazelcast.core.IExecutorService;
import com.hazelcast.cluster.Member;
import io.github.quickmsg.common.callable.HazelcastJobCallable;
import io.github.quickmsg.common.integrate.job.Job;
import io.github.quickmsg.common.integrate.job.JobCaller;
import io.github.quickmsg.common.integrate.job.JobClosure;
import io.github.quickmsg.common.integrate.job.JobExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Hazelcast implementation of JobExecutor
 *
 * @author hxx
 */
public class HazelcastExecutor implements JobExecutor {

    private final IExecutorService executorService;

    public HazelcastExecutor(IExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void execute(Job job) {
        Runnable runnable = job::run;
        if (job.isBroadcast()) {
            executorService.executeOnAllMembers(runnable);
        } else {
            executorService.submit(runnable);
        }
    }

    @Override
    public <R> Collection<R> callBroadcast(JobCaller<R> callable) {
        Callable<R> hazelcastCallable = callable::call;
        Map<Member, Future<R>> futureMap = executorService.submitToAllMembers(hazelcastCallable);
        
        Collection<R> results = new ArrayList<>();
        for (Future<R> future : futureMap.values()) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Error executing broadcast task", e);
            }
        }
        return results;
    }

    @Override
    public <INPUT, OUT> Collection<OUT> callBroadcast(JobClosure<INPUT, OUT> callable, INPUT input) {
        Callable<OUT> hazelcastCallable = new HazelcastJobCallable<>(callable, input);
        Map<Member, Future<OUT>> futureMap = executorService.submitToAllMembers(hazelcastCallable);


        Collection<OUT> results = new ArrayList<>();
        for (Future<OUT> future : futureMap.values()) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Error executing broadcast closure task", e);
            }
        }
        return results;
    }

    @Override
    public <R> R call(JobCaller<R> callable) {
        Callable<R> hazelcastCallable = callable::call;
        try {
            Future<R> future = executorService.submit(hazelcastCallable);
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error executing task", e);
        }
    }
}