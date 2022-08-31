package io.github.quickmsg.common.pool;

import io.netty.util.internal.ObjectPool;

/**
 * @author luxurong
 */
public class RecycleObject<T> {

    private final T object;

    private final ObjectPool.Handle<RecycleObject<T>> handle;

    public RecycleObject(T object, ObjectPool.Handle<RecycleObject<T>> handle) {
        this.object = object;
        this.handle = handle;
    }

    public void recycle() {
        handle.recycle(this);
    }


    public T getObject() {
        return this.object;
    }



}
