package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.WeatherResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/** BaseRepository interface for fetching weather data. */
public interface WeatherRepository {
    Observable<List<WeatherResponse>> getDailyWeatherByLocation(
            @NonNull Coordinates coordinates);

    Observable<List<WeatherResponse>> getCurrentWeatherByLocation(
            @NonNull Coordinates coordinates);

    Observable<List<WeatherResponse>> getHourlyWeatherByLocation(
            @NonNull Coordinates coordinates);

    Observable<List<WeatherResponse>> fetchAndCacheDailyWeather(
            @NonNull Coordinates coordinates);

    Observable<List<WeatherResponse>> fetchAndCacheCurrentWeather(
            @NonNull Coordinates coordinates);

    Observable<List<WeatherResponse>> fetchAndCacheHourlyWeather(
            @NonNull Coordinates coordinates);
}
