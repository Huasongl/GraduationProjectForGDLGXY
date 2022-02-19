package com.gdlgxy.internshipcommunity.network.cache;

import com.gdlgxy.internshipcommunity.CommunityApplication;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Cache.class}, version = 2)
public abstract class CacheDatabase extends RoomDatabase {
    private static final CacheDatabase database;

    static {
        database = Room.databaseBuilder(CommunityApplication.getApplication(), CacheDatabase.class, "community_cache")
                .allowMainThreadQueries()
                .build();
    }

    public abstract CacheDao getCache();

    public static CacheDatabase get() {
        return database;
    }
}
