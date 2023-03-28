package io.github.quickmsg.common.integrate;

import io.github.quickmsg.common.integrate.cache.ConnectCache;
import lombok.Getter;
import org.apache.ignite.cache.CacheMode;

import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;

/**
 * @author luxurong
 */
@Getter
public enum IgniteCacheRegion {

    CONFIG("config", "config_region", CacheMode.REPLICATED, null) {
        @Override
        public boolean persistence() {
            return false;
        }

        @Override
        public boolean local() {
            return false;
        }

        @Override
        public Factory<? extends ExpiryPolicy> getExpiryPolicyFactory() {
            return null;
        }
    },
    CHANNEL("channel_cache", "channel_data_region", CacheMode.PARTITIONED, new Class[]{Integer.class, ConnectCache.class}) {
        @Override
        public boolean persistence() {
            return false;
        }

        @Override
        public boolean local() {
            return false;
        }

        @Override
        public Factory<? extends ExpiryPolicy> getExpiryPolicyFactory() {
            return null;
        }
    },
    RETAIN("retain_message", "retain_data_region", CacheMode.PARTITIONED, null) {
        @Override
        public boolean persistence() {
            return false;
        }

        @Override
        public boolean local() {
            return false;
        }

        @Override
        public Factory<? extends ExpiryPolicy> getExpiryPolicyFactory() {
            return null;
        }
    }
    ;


    private final String cacheName;

    private final String regionName;

    private final CacheMode  cacheMode;

    private final Class<?>[] indexedTypes;

    IgniteCacheRegion(String cacheName, String regionName, CacheMode cacheMode, Class<?>[] indexedTypes) {
        this.cacheName = cacheName;
        this.regionName = regionName;
        this.cacheMode = cacheMode;
        this.indexedTypes = indexedTypes;
    }

    public abstract boolean persistence();

    public abstract boolean local();

    public abstract Factory<? extends ExpiryPolicy> getExpiryPolicyFactory();


}
