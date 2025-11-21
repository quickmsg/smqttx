package io.github.quickmsg.interate.hazelcast;

import com.hazelcast.map.IMap;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

/**
 * Hazelcast implementation of IntegrateCache
 *
 * @author hxx
 */
public class HazelcastIntegrateCache<K, V> implements IntegrateCache<K, V> {

    private final IMap<K, V> hazelcastMap;

    public HazelcastIntegrateCache(IMap<K, V> hazelcastMap) {
        this.hazelcastMap = hazelcastMap;
    }

    @Override
    public void forEach(Consumer<Map.Entry<K, V>> consumer) {
        hazelcastMap.entrySet().forEach(entry -> {
            Map.Entry<K, V> mapEntry = new Map.Entry<K, V>() {
                @Override
                public K getKey() {
                    return entry.getKey();
                }

                @Override
                public V getValue() {
                    return entry.getValue();
                }

                @Override
                public V setValue(V value) {
                    return entry.setValue(value);
                }
            };
            consumer.accept(mapEntry);
        });
    }

    @Override
    public void put(K k, V v) {
        hazelcastMap.put(k, v);
    }

    @Override
    public V getAndPut(K k, V v) {
        return hazelcastMap.put(k, v);
    }

    @Override
    public V getAndPutIfAbsent(K k, V v) {
        return hazelcastMap.putIfAbsent(k, v);
    }

    @Override
    public V get(K k) {
        return hazelcastMap.get(k);
    }

    @Override
    public boolean remove(K k) {
        return hazelcastMap.remove(k) != null;
    }

    @Override
    public boolean remove(K k, V v) {
        return hazelcastMap.remove(k, v);
    }

    @Override
    public boolean exist(K k) {
        return hazelcastMap.containsKey(k);
    }

    @Override
    public Lock lock(K k) {
        // Hazelcast lock implementation
        hazelcastMap.lock(k);
        return new Lock() {
            @Override
            public void lock() {
                hazelcastMap.lock(k);
            }

            @Override
            public void lockInterruptibly() throws InterruptedException {
                throw new UnsupportedOperationException("lockInterruptibly not supported");
            }

            @Override
            public boolean tryLock() {
                return hazelcastMap.tryLock(k);
            }

            @Override
            public boolean tryLock(long time, java.util.concurrent.TimeUnit unit) throws InterruptedException {
                return hazelcastMap.tryLock(k, time, unit);
            }

            @Override
            public void unlock() {
                hazelcastMap.unlock(k);
            }

            @Override
            public java.util.concurrent.locks.Condition newCondition() {
                throw new UnsupportedOperationException("newCondition not supported");
            }
        };
    }

    @Override
    public void clear() {
        hazelcastMap.clear();
    }

    @Override
    public void close() {
        hazelcastMap.destroy();
    }
}