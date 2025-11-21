package io.github.quickmsg.common.integrate;

import lombok.Getter;

/**
 * @author hxx
 */
@Getter
public enum CacheRegion {

    CONFIG("config", "config_region", true, false, null),
    CHANNEL("channel_cache", "channel_data_region", false, false, null),
    RETAIN("retain_message", "retain_data_region", false, false, null);


    private final String cacheName;

    private final String regionName;
    
    private final boolean replicated;
    
    private final boolean partitioned;

    private final Class<?>[] indexedTypes;

    CacheRegion(String cacheName, String regionName, boolean replicated, boolean partitioned, Class<?>[] indexedTypes) {
        this.cacheName = cacheName;
        this.regionName = regionName;
        this.replicated = replicated;
        this.partitioned = partitioned;
        this.indexedTypes = indexedTypes;
    }

    public boolean persistence() {
        return false;
    }

    public boolean local() {
        return false;
    }
    
    public Object getExpiryPolicyFactory() {
        return null;
    }
}