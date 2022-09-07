package io.github.quickmsg.common.integrate.proxy;

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
        return methodProxy.invoke(source, objects);
    }
}
