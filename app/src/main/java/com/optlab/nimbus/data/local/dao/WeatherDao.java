package com.optlab.nimbus.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.optlab.nimbus.data.local.entity.WeatherEntity;

@Dao
public interface WeatherDao {
    @Query("SELECT * FROM weather_database WHERE type = :type ORDER BY timestamp DESC LIMIT 1")
    WeatherEntity getLatestWeather(WeatherEntity.Type type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWeather(WeatherEntity weatherEntity);

    @Query("DELETE FROM weather_database WHERE timestamp < :expiryTime")
    void deleteExpiry(long expiryTime);
}
