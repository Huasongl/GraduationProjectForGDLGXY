package com.gdlgxy.internshipcommunity.network.cache;

import com.gdlgxy.internshipcommunity.CommunityApplication;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Cache.class}, version = 2)
public abstract class CacheDatabase extends RoomDatabase {
    private static final String NAME = "network_cache";
    private static CacheDatabase sNetworkCacheDatabase;
    public static synchronized CacheDatabase getInstance() {
        if (sNetworkCacheDatabase == null) {
            sNetworkCacheDatabase = Room.databaseBuilder(CommunityApplication.getApplication(),
                    CacheDatabase.class, NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return sNetworkCacheDatabase;
    }

    public abstract CacheDao getCache();
}
