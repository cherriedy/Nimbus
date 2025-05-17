package com.optlab.nimbus.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.optlab.nimbus.data.model.ForecastProvider;
import com.optlab.nimbus.data.local.entity.ForecastEntity;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface ForecastDao {
    @Query(
            "SELECT * "
                    + "FROM  forecast_database "
                    + "WHERE provider = :provider "
                    + "AND coordinates = :coordinates "
                    + "ORDER BY timestamp DESC "
                    + "LIMIT 1")
    Maybe<ForecastEntity> getForecast(ForecastProvider provider, String coordinates);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(ForecastEntity entity);

    @Query(
            "DELETE FROM forecast_database "
                    + "WHERE provider = :provider "
                    + "AND coordinates = :coordinates "
                    + "AND timestamp < :expiryTime")
    Completable deleteExpiry(ForecastProvider provider, String coordinates, long expiryTime);
}
