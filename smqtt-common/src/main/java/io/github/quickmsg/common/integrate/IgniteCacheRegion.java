package io.github.quickmsg.common.integrate;

import lombok.Getter;

/**
 * @author luxurong
 */
@Getter
public enum IgniteCacheRegion {

    TOPIC("topic_cache", "topic_data_region") {
        @Override
        public boolean persistence() {
            return false;
        }
    },
    MESSAGE("message_cache", "message_data_region") {
        @Override
        public boolean persistence() {
            return true;
        }
    },
    SESSION("session_message", "session_data_region") {
        @Override
        public boolean persistence() {
            return true;
        }
    },
    RETAIN("retain_message", "retain_data_region") {
        @Override
        public boolean persistence() {
            return true;
        }
    }

    ;


    private final String cacheName;

    private final String regionName;

    IgniteCacheRegion(String cacheName, String regionName) {
        this.cacheName = cacheName;
        this.regionName = regionName;
    }

    public abstract boolean persistence();

}
