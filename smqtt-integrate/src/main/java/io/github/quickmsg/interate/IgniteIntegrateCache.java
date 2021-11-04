package io.github.quickmsg.interate;

import io.github.quickmsg.common.interate1.cache.IntegrateCache;
import org.apache.ignite.IgniteCache;

import java.util.concurrent.locks.Lock;

/**
 * @author luxurong
 */
public class IgniteIntegrateCache<K,V> implements IntegrateCache<K,V> {

    private  final IgniteCache<K,V> igniteCache;

    public IgniteIntegrateCache(IgniteCache<K, V> igniteCache) {
        this.igniteCache = igniteCache;
    }

    @Override
    public void put(K k, V v) {
        igniteCache.put(k,v);
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
