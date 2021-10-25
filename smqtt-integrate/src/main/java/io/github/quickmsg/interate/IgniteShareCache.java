package io.github.quickmsg.interate;

import io.github.quickmsg.common.interate1.cache.ShareCache;
import org.apache.ignite.IgniteCache;

/**
 * @author luxurong
 */
public class IgniteShareCache<K,V> implements ShareCache<K,V> {

    private  final IgniteCache<K,V> igniteCache;

    public IgniteShareCache(IgniteCache<K, V> igniteCache) {
        this.igniteCache = igniteCache;
    }
}
