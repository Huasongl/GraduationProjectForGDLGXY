package com.gdlgxy.internshipcommunity.network.cache;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cache")
public class Cache implements Serializable {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    public String key;

    @ColumnInfo(name = "data")
    public byte[] data;
}