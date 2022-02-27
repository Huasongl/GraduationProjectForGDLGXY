package com.gdlgxy.internshipcommunity.network.networktask;

import java.util.Collections;
import java.util.Map;

public class CacheEntry {
    /** Database _id. 目前只有query出来的才填充了这个字段，为了效率*/
    public long id;

    /** The data returned from cache. */
    public byte[] data;

    /** ETag for cache coherency. */
    public String etag;

    /** Date of this response as reported by the server. */
    public long serverDate;

    /** Last-Modified of this response as reported by the server. */
    public long lastModified;

    /** Local update time */
    public long localUpdateTime;

    /** TTL for this record. */
    public long ttl;

    /** Soft TTL for this record. */
    public long softTtl;
    
    /** Language */
    public String language;

    /**request of app version code*/
    public int versionCode;

    /** Immutable response headers as received from server; must be non-null. */
    public Map<String, String> responseHeaders = Collections.emptyMap();

    /** True if the entry is expired. */
    public boolean isExpired() {
        return this.ttl < System.currentTimeMillis();
    }

    /** True if a refresh is needed from the original data source. */
    public boolean refreshNeeded() {
        return this.softTtl < System.currentTimeMillis();
    }
}
