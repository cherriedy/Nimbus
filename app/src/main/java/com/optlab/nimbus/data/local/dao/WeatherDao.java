package com.optlab.nimbus.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.local.entity.WeatherEntity;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface WeatherDao {
    @Query(
            "SELECT * "
                    + "FROM weather_database "
                    + "WHERE type = :type "
                    + "AND provider = :provider "
                    + "AND coordinates = :coordinates "
                    + "ORDER BY timestamp DESC "
                    + "LIMIT 1")
    Maybe<WeatherEntity> getLatestWeather(
            WeatherEntity.Type type, WeatherProvider provider, String coordinates);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertWeather(WeatherEntity weatherEntity);

    @Query(
            "DELETE FROM weather_database "
                    + "WHERE type = :type "
                    + "AND provider = :provider "
                    + "AND coordinates = :coordinates "
                    + "AND timestamp < :expiryTime")
    Completable deleteExpiry(
            WeatherEntity.Type type,
            WeatherProvider provider,
            String coordinates,
            long expiryTime);
}
