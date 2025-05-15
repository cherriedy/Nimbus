package com.optlab.nimbus.data.local.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.optlab.nimbus.data.local.dao.LocationDao;
import com.optlab.nimbus.data.local.entity.LocationEntity;

@Database(
        entities = {LocationEntity.class},
        version = 1,
        exportSchema = false)
public abstract class LocationDatabase extends RoomDatabase {
    public abstract LocationDao locationDao();
}
