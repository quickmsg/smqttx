package io.github.quickmsg.interate;

import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import org.apache.ignite.IgniteCache;

import javax.cache.Cache;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

/**
 * @author luxurong
 */
public class IgniteIntegrateCache<K,V> implements IntegrateCache<K,V> {

    private  final IgniteCache<K,V> igniteCache;

    public IgniteIntegrateCache(IgniteCache<K, V> igniteCache) {
        this.igniteCache = igniteCache;
    }

    @Override
    public IgniteCache<K, V> getOriginCache() {
        return this.igniteCache;
    }

    @Override
    public void forEach(Consumer<Cache.Entry<K, V>> consumer) {
        igniteCache.forEach(consumer);
    }

    @Override
    public void put(K k, V v) {
        igniteCache.put(k,v);
    }

    @Override
    public V getAndPut(K k, V v) {
        return igniteCache.getAndPut(k,v);
    }

    @Override
    public V getAndPutIfAbsent(K k, V v) {
        return igniteCache.getAndPutIfAbsent(k,v);
    }

    @Override
    public V get(K k) {
        return igniteCache.get(k);
    }

    @Override
    public boolean remove(K k) {
        return igniteCache.remove(k);
    }

    @Override
    public boolean remove(K k, V v) {
        return igniteCache.remove(k,v);
    }

    @Override
    public boolean exist(K k) {
        return igniteCache.containsKey(k);
    }

    @Override
    public Lock lock(K k) {
        return igniteCache.lock(k);
    }

    @Override
    public void clear() {
        this.igniteCache.clear();
    }

    @Override
    public void close() {
        this.igniteCache.close();
    }


}
