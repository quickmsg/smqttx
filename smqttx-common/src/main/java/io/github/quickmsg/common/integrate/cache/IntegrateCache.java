package io.github.quickmsg.common.integrate.cache;

import java.util.concurrent.locks.Lock;

/**
 * cache
 *
 * @author luxurong
 */
public interface IntegrateCache<K, V>  {

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
