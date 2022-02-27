package com.gdlgxy.internshipcommunity.network.networktask;

public interface CacheManager {
    CacheEntry queryCacheEntry(String url, String tag, String language);
    boolean saveCacheEntry(String url, String tag, String language, CacheEntry cache);
    boolean deleteCacheEntry(String url, String tag);
}
