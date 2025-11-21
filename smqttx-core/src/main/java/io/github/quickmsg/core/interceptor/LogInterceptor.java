package io.github.quickmsg.core.interceptor;

import io.github.quickmsg.common.interceptor.Interceptor;
import io.github.quickmsg.common.interceptor.Invocation;

import java.util.Arrays;

public class LogInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) {
        System.out.println("拦截了信息:"+ Arrays.toString(invocation.getArgs()));
        return invocation.proceed();
    }

    @Override
    public int sort() {
        return 0;
    }
}
