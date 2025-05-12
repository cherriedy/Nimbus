package com.optlab.nimbus.data;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.repository.WeatherRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FakeTomorrowIoRepository implements WeatherRepository {
    private boolean shouldFail = false;
    private List<WeatherResponse> mockResponses = null;

    public void setShouldFail(boolean fail) {
        shouldFail = fail;
    }

    public List<WeatherResponse> getMockResponses() {
        return mockResponses;
    }

    /**
     * Emits a fake response for the weather data. If shouldFail is true, it simulates a network
     * error by returning an error Observable. Otherwise, it returns a list of WeatherResponse
     * objects.
     */
    private Observable<List<WeatherResponse>> emitFakeResponse() {
        return Observable.defer(
                () -> {
                    if (shouldFail) {
                        mockResponses = null; // Reset the list to null to simulate failure
                        // Simulate a network error
                        return Observable.error(new RuntimeException("Network error"));
                    }

                    // Simulate a delay to mimic network call
                    mockResponses = new ArrayList<>(); // Initialize the list
                    return Observable.just(mockResponses)
                            .delay(3, TimeUnit.MILLISECONDS, Schedulers.computation());
                });
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
