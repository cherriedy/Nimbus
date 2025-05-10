package com.optlab.nimbus.data;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.repository.WeatherRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class FakeTomorrowIoRepository implements WeatherRepository {
    private boolean shouldFail = false;

    public void setShouldFail(boolean fail) {
        shouldFail = fail;
    }

    private Observable<List<WeatherResponse>> emitFakeResponse() {
        return shouldFail
                ? Observable.error(new RuntimeException("Stimulate fail"))
                : Observable.just(new ArrayList<>());
    }

    @Override
    public Observable<List<WeatherResponse>> getDailyWeatherByLocation(
            @NonNull Coordinates coordinates) {
        return emitFakeResponse();
    }

    @Override
    public Observable<List<WeatherResponse>> getCurrentWeatherByLocation(
            @NonNull Coordinates coordinates) {
        return emitFakeResponse();
    }

    @Override
    public Observable<List<WeatherResponse>> getHourlyWeatherByLocation(
            @NonNull Coordinates coordinates) {
        return emitFakeResponse();
    }

    @Override
    public Observable<List<WeatherResponse>> fetchAndCacheDailyWeather(
            @NonNull Coordinates coordinates) {
        return emitFakeResponse();
    }

    @Override
    public Observable<List<WeatherResponse>> fetchAndCacheCurrentWeather(
            @NonNull Coordinates coordinates) {
        return emitFakeResponse();
    }

    @Override
    public Observable<List<WeatherResponse>> fetchAndCacheHourlyWeather(
            @NonNull Coordinates coordinates) {
        return emitFakeResponse();
    }
}
