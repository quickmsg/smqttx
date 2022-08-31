package io.github.quickmsg.common.pool;

import io.netty.util.internal.ObjectPool;

import java.util.function.Supplier;

/**
 * @author luxurong
 */
public class RecycleObjectPool<T> {

    private Supplier<T> objectSupplier;

    public RecycleObjectPool(Supplier<T> objectSupplier) {
        this.objectSupplier = objectSupplier;
    }

    private final ObjectPool<RecycleObject<T>> objectPool =
            ObjectPool.newPool(handle -> new RecycleObject<>(objectSupplier.get(), handle));

    public RecycleObject<T> get() {
        return objectPool.get();
    }


}
