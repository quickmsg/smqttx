package io.github.quickmsg.common.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author luxurong
 */
public class InterceptorHandler implements InvocationHandler {

    private final Interceptor interceptor;

    private final Object target;

    public InterceptorHandler(Interceptor interceptor, Object target) {
        this.interceptor = interceptor;
        this.target = target;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        Intercept intercept = method.getAnnotation(Intercept.class);
        if (intercept == null) {
            return method.invoke(proxy, args);
        } else {
            return interceptor.intercept(new Invocation(method, target, args));
        }
    }
}
