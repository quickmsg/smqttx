package io.github.quickmsg.common.callable;

import io.github.quickmsg.common.integrate.job.JobClosure;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class HazelcastJobCallable<INPUT, OUT>
        implements Callable<OUT>, Serializable {

    private static final long serialVersionUID = 1L;
    
    private final JobClosure<INPUT, OUT> jobClosure;
    private final INPUT input;

    public HazelcastJobCallable(JobClosure<INPUT, OUT> jobClosure, INPUT input) {
        this.jobClosure = jobClosure;
        this.input = input;
    }

    @Override
    public OUT call() throws Exception {
        return jobClosure.apply(input);
    }
}