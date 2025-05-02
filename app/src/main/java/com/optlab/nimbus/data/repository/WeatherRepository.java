package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.UnifiedWeatherResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/** BaseRepository interface for fetching weather data. */
public interface WeatherRepository {
    Observable<List<UnifiedWeatherResponse>> getDailyWeatherByLocation(
            @NonNull Coordinates coordinates);

    Observable<List<UnifiedWeatherResponse>> getCurrentWeatherByLocation(
            @NonNull Coordinates coordinates);

    Observable<List<UnifiedWeatherResponse>> getHourlyWeatherByLocation(
            @NonNull Coordinates coordinates);
}
