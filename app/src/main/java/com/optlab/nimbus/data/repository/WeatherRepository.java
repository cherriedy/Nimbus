package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.common.Coordinates;

import io.reactivex.rxjava3.core.Flowable;

/** BaseRepository interface for fetching weather data. */
public interface WeatherRepository {
    Flowable<WeatherEntity> getWeaklyForecast(@NonNull Coordinates coordinates);

    Flowable<WeatherEntity> getCurrentForecast(@NonNull Coordinates coordinates);

    Flowable<WeatherEntity> getHourlyForecast(@NonNull Coordinates coordinates);
}
