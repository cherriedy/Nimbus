package com.optlab.nimbus.data.local.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.entity.WeatherEntity;

@Database(
        entities = {WeatherEntity.class},
        version = 1,
        exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao weatherDao();
}
