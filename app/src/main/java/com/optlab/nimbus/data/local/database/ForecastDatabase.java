package com.optlab.nimbus.data.local.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.optlab.nimbus.data.local.dao.ForecastDao;
import com.optlab.nimbus.data.local.entity.ForecastEntity;

@Database(
        entities = {ForecastEntity.class},
        version = 1,
        exportSchema = false)
public abstract class ForecastDatabase extends RoomDatabase {
    public abstract ForecastDao weatherDao();
}
