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

        @Override
        public boolean local() {
            return false;
        }
    },
    MESSAGE("message_cache", "message_data_region") {
        @Override
        public boolean persistence() {
            return true;
        }

        @Override
        public boolean local() {
            return false;
        }
    },
    SESSION("session_message", "session_data_region") {
        @Override
        public boolean persistence() {
            return true;
        }

        @Override
        public boolean local() {
            return false;
        }
    },
    RETAIN("retain_message", "retain_data_region") {
        @Override
        public boolean persistence() {
            return true;
        }

        @Override
        public boolean local() {
            return false;
        }
    },
    ACK("ack_message", "ack_data_region") {
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

    IgniteCacheRegion(String cacheName, String regionName) {
        this.cacheName = cacheName;
        this.regionName = regionName;
    }

    public abstract boolean persistence();

    public abstract boolean local();

}
