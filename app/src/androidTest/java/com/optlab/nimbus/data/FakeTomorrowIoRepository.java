package com.optlab.nimbus.data;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.local.entity.ForecastEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.forecast.ForecastResponse;
import com.optlab.nimbus.data.repository.WeatherRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

// public class FakeTomorrowIoRepository implements WeatherRepository {
//     private boolean shouldFail = false;
//     private List<WeatherResponse> mockResponses = null;
//
//     public void setShouldFail(boolean fail) {
//         shouldFail = fail;
//     }
//
//     public List<WeatherResponse> getMockResponses() {
//         return mockResponses;
//     }
//
//     /**
//      * Emits a fake response for the weather data. If shouldFail is true, it simulates a network
//      * error by returning an error Observable. Otherwise, it returns a list of WeatherResponse
//      * objects.
//      */
//     private Observable<List<WeatherResponse>> emitFakeResponse() {
//         return Observable.defer(
//                 () -> {
//                     if (shouldFail) {
//                         mockResponses = null; // Reset the list to null to simulate failure
//                         // Simulate a network error
//                         return Observable.error(new RuntimeException("Network error"));
//                     }
//
//                     // Simulate a delay to mimic network call
//                     mockResponses = new ArrayList<>(); // Initialize the list
//                     return Observable.just(mockResponses)
//                             .delay(3, TimeUnit.MILLISECONDS, Schedulers.computation());
//                 });
//     }
//
//     @Override
//     public Observable<List<WeatherResponse>> getDailyWeather(
//             @NonNull Coordinates coordinates) {
//         return emitFakeResponse();
//     }
//
//     @Override
//     public Observable<List<WeatherResponse>> getCurrentWeatherByLocation(
//             @NonNull Coordinates coordinates) {
//         return emitFakeResponse();
//     }
//
//     @Override
//     public Observable<List<WeatherResponse>> getHourlyWeatherByLocation(
//             @NonNull Coordinates coordinates) {
//         return emitFakeResponse();
//     }
//
//     @Override
//     public Observable<List<WeatherResponse>> fetchAndCacheDailyWeather(
//             @NonNull Coordinates coordinates) {
//         return emitFakeResponse();
//     }
//
//     @Override
//     public Observable<List<WeatherResponse>> fetchAndCacheCurrentWeather(
//             @NonNull Coordinates coordinates) {
//         return emitFakeResponse();
//     }
//
//     @Override
//     public Observable<List<WeatherResponse>> fetchAndCacheHourlyWeather(
//             @NonNull Coordinates coordinates) {
//         return emitFakeResponse();
//     }
// }

public class FakeTomorrowIoRepository implements WeatherRepository {
    private boolean shouldFail = false;
    private List<ForecastResponse> mockResponses = null;

    public void setShouldFail(boolean fail) {
        shouldFail = fail;
    }

    public List<ForecastResponse> getMockResponses() {
        return mockResponses;
    }

    @Override
    public Flowable<ForecastEntity> getWeaklyForecast(@NonNull Coordinates coordinates) {
        return null;
    }

    @Override
    public Flowable<ForecastEntity> getCurrentForecast(@NonNull Coordinates coordinates) {
        return null;
    }

    @Override
    public Flowable<ForecastEntity> getHourlyForecast(@NonNull Coordinates coordinates) {
        return null;
    }
}
