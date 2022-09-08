package io.github.quickmsg.common.integrate.cache;

import javax.cache.Cache;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

/**
 * cache
 *
 * @author luxurong
 */
public interface IntegrateCache<K, V>  {


    void forEach(Consumer<Cache.Entry<K,V>> consumer);

    void put(K k, V v);

    V getAndPut(K k, V v);

    V getAndPutIfAbsent(K k, V v);

    V get(K k);

    boolean remove(K k);

    boolean remove(K k,V v);

    boolean exist(K k);

    Lock lock(K k);

    void clear();

    void close();






}
