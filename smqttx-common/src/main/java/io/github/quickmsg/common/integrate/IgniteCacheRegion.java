package io.github.quickmsg.common.integrate;

import lombok.Getter;
import org.apache.ignite.cache.CacheMode;

/**
 * @author luxurong
 */
@Getter
public enum IgniteCacheRegion {

    MQTT_CACHE("mqtt-*", "topic_data_region", CacheMode.PARTITIONED) {
        @Override
        public boolean persistence() {
            return false;
        }

        @Override
        public boolean local() {
            return false;
        }
    },
    CHANNEL("channel_cache", "channel_data_region", CacheMode.PARTITIONED) {
        @Override
        public boolean persistence() {
            return false;
        }

        @Override
        public boolean local() {
            return false;
        }
    },
    RETAIN("retain_message", "retain_data_region", CacheMode.PARTITIONED) {
        @Override
        public boolean persistence() {
            return false;
        }

        @Override
        public boolean local() {
            return false;
        }
    },
    LOCK("lock", "lock_region", CacheMode.PARTITIONED) {
        @Override
        public boolean persistence() {
            return false;
        }

        @Override
        public boolean local() {
            return false;
        }
    },
    ;


    private final String cacheName;

    private final String regionName;

    private final CacheMode  cacheMode;

    IgniteCacheRegion(String cacheName, String regionName, CacheMode cacheMode) {
        this.cacheName = cacheName;
        this.regionName = regionName;
        this.cacheMode = cacheMode;
    }

    public abstract boolean persistence();

    public abstract boolean local();


}
