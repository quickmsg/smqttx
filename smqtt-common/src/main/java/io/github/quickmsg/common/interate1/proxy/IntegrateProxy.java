package io.github.quickmsg.common.interate1.proxy;

import io.github.quickmsg.common.interate1.job.JobFor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.ignite.IgniteCompute;

import java.lang.reflect.Method;

/**
 * @author luxurong
 */
public class IntegrateProxy<T> implements MethodInterceptor {


    private final Enhancer enhancer;

    private final T source;

    private final IgniteCompute compute;

    public IntegrateProxy(T object, Class<T> tClass, IgniteCompute compute) {
        this.compute = compute;
        this.enhancer = new Enhancer();
        this.source = object;
        enhancer.setSuperclass(tClass);
        enhancer.setCallback(this);
    }

    @SuppressWarnings("unchecked")
    public T proxy() {
        return (T) enhancer.create();
    }

    @Override

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        JobFor jobFor = method.getAnnotation(JobFor.class);
        if (jobFor != null) {
            return compute.broadcast(() -> {
                try {
                   return methodProxy.invoke(source, objects);
                } catch (Throwable e) {
                    e.printStackTrace();
                    return null;
                }
            });
        } else {
            return methodProxy.invoke(source, objects);
        }
    }
}
