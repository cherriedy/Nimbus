package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.local.entity.ForecastEntity;
import com.optlab.nimbus.data.model.Coordinates;

import io.reactivex.rxjava3.core.Flowable;

/** BaseRepository interface for fetching weather data. */
public interface WeatherRepository {
    Flowable<ForecastEntity> getWeaklyForecast(@NonNull Coordinates coordinates);

    Flowable<ForecastEntity> getCurrentForecast(@NonNull Coordinates coordinates);

    Flowable<ForecastEntity> getHourlyForecast(@NonNull Coordinates coordinates);
}
