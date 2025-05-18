package com.optlab.nimbus.data.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.constant.ResponseConstant;
import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoResponse;
import com.optlab.nimbus.data.network.WeatherResponse;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.preferences.SecurePrefsManager;
import com.optlab.nimbus.utility.DateTimeUtil;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;

import timber.log.Timber;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/** TomorrowIoRepository is responsible for fetching weather data from the Tomorrow.io API. */
public class TomorrowIoRepository implements WeatherRepository {
    private final TomorrowIoClient tomorrowIoClient;
    private final String tomorrowIoKey;
    private final WeatherDao weatherDao;
    private final Gson gson = new Gson();
    private final Type reflectType = new TypeToken<List<WeatherResponse>>() {}.getType();

    /** Constructor injection for TomorrowIoClient */
    public TomorrowIoRepository(
            TomorrowIoClient tomorrowIoClient,
            SecurePrefsManager securePrefsManager,
            WeatherDao weatherDao) {
        this.tomorrowIoClient = tomorrowIoClient;
        this.weatherDao = weatherDao;

        this.tomorrowIoKey = securePrefsManager.getApiKey(SecurePrefsManager.TOMORROW_IO_API_KEY);
    }

    /**
     * Fetches daily weather data by location code (latitude and longitude) using the Tomorrow.io
     * API.
     *
     * <p>This method first checks if there is cached data in the local database. If cached data is
     * available and not expired, it returns the cached data. Otherwise, it fetches new data from
     * the API and caches it locally.
     *
     * @param coordinates the coordinates of the location
     * @return an Observable that emits a list of UnifiedWeatherResponse
     */
    public @NonNull Observable<List<WeatherResponse>> getDailyWeatherByLocation(
            @androidx.annotation.NonNull Coordinates coordinates) {
        return getCachedWeather(WeatherEntity.Type.DAILY)
                .flatMap(
                        cachedData -> {
                            if (!cachedData.isEmpty()) {
                                return Observable.just(cachedData);
                            }
                            return fetchAndCacheDailyWeather(coordinates);
                        });
    }

    /**
     * Fetches and caches daily weather data by location code (latitude and longitude) using the
     * Tomorrow.io API.
     *
     * @param coordinates the coordinates of the location
     * @return an Observable that emits a list of UnifiedWeatherResponse
     */
    @Override
    public Observable<List<WeatherResponse>> fetchAndCacheDailyWeather(
            @androidx.annotation.NonNull @NonNull Coordinates coordinates) {
        WeatherEntity.Type type = WeatherEntity.Type.DAILY;
        return tomorrowIoClient
                .getForecast(
                        coordinates,
                        TomorrowIoClient.DAILY_WEATHER_FIELDS,
                        TomorrowIoClient.TIMESTEPS_ONE_DAY,
                        TomorrowIoClient.PLUS_1_DAYS_FROM_TODAY,
                        TomorrowIoClient.PLUS_5_DAYS_FROM_TODAY,
                        TimeZone.getDefault(),
                        tomorrowIoKey)
                .map(response -> cacheWeatherDataLocally(response, type))
                .onErrorResumeNext(throwable -> fallbackToEmptyList(throwable, type));
    }

    /**
     * Fetches cached weather data from the local database.
     *
     * @param type the type of weather data (DAILY, HOURLY, CURRENT)
     * @return an Observable that emits a list of UnifiedWeatherResponse
     */
    public Observable<List<WeatherResponse>> getCachedWeather(@NonNull WeatherEntity.Type type) {
        return Observable.fromCallable(
                () -> {
                    WeatherEntity entity = weatherDao.getLatestWeather(type);
                    if (entity == null) {
                        return Collections.emptyList(); // Return an empty list if no data is found
                    }

                    if (entity.isExpired()) {
                        weatherDao.deleteExpiry(System.currentTimeMillis()); // Delete expired data
                        return Collections.emptyList(); // Return an empty list if data is expired
                    }

                    return gson.fromJson(entity.getData(), reflectType);
                });
    }

    /**
     * Fetches current weather data by location code (latitude and longitude) using the Tomorrow.io
     * API. This endpoint supports real-time weather data retrieval.
     *
     * @param coordinates the coordinates of the location
     * @return an Observable that emits a list of UnifiedWeatherResponse
     */
    @Override
    public @NonNull Observable<List<WeatherResponse>> getCurrentWeatherByLocation(
            @androidx.annotation.NonNull @NonNull Coordinates coordinates) {
        return getCachedWeather(WeatherEntity.Type.CURRENT)
                .flatMap(
                        cachedWeather -> {
                            if (!cachedWeather.isEmpty()) {
                                return Observable.just(cachedWeather);
                            }
                            return fetchAndCacheCurrentWeather(coordinates);
                        });
    }

    /**
     * Fetches and caches current weather data by location code (latitude and longitude) using the
     * Tomorrow.io API.
     *
     * @param coordinates the coordinates of the location
     * @return an Observable that emits a list of UnifiedWeatherResponse
     */
    @Override
    public Observable<List<WeatherResponse>> fetchAndCacheCurrentWeather(
            @androidx.annotation.NonNull @NonNull Coordinates coordinates) {
        WeatherEntity.Type type = WeatherEntity.Type.CURRENT;
        return tomorrowIoClient
                .getForecast(
                        coordinates,
                        TomorrowIoClient.CURRENT_WEATHER_FIELDS,
                        TomorrowIoClient.TIMESTEPS_CURRENT,
                        TimeZone.getDefault(),
                        tomorrowIoKey)
                .map(response -> cacheWeatherDataLocally(response, type))
                .onErrorResumeNext(throwable -> fallbackToEmptyList(throwable, type));
    }

    /**
     * Caches the weather response to the local database.
     *
     * <p>This method converts the TomorrowIoResponse to a list of UnifiedWeatherResponse and caches
     * it in the local database. It also deletes any expired weather data from the database before
     * inserting the new data.
     *
     * @param response the TomorrowIoResponse object containing the weather data
     * @param type the type of weather data (DAILY, HOURLY, CURRENT)
     * @return a list of UnifiedWeatherResponse
     */
    private List<WeatherResponse> cacheWeatherDataLocally(
            @NonNull TomorrowIoResponse response, @NonNull WeatherEntity.Type type) {
        List<WeatherResponse> weatherData = TomorrowIoResponse.mapToResponses(response);

        switch (type) { // Delete expired weather data based on the type
            case CURRENT -> weatherDao.deleteExpiry(ResponseConstant.CURRENT_EXPIRY_TIME);
            case DAILY, HOURLY -> weatherDao.deleteExpiry(ResponseConstant.DAILY_EXPIRY_TIME);
        }

        // Insert the new weather data into the database
        WeatherEntity entity = new WeatherEntity();
        entity.setType(type);
        entity.setData(new Gson().toJson(weatherData));
        entity.setTimestamp(System.currentTimeMillis());
        if (weatherDao.insertWeather(entity) != -1) {
            Timber.d("%s: Weather data cached successfully", type.name());
        } else {
            Timber.e("%s: Failed to cache weather data", type.name());
        }

        return weatherData;
    }

    /**
     * Fetches hourly weather data by location code (latitude and longitude) using the Tomorrow.io
     * API.
     *
     * <p>This method first checks if there is cached data in the local database. If cached data is
     * available and not expired, it returns the cached data. Otherwise, it fetches new data from
     * the API and caches it locally.
     *
     * @param coordinates the coordinates of the location
     * @return an Observable that emits a list of WeatherResponse
     */
    @Override
    public Observable<List<WeatherResponse>> getHourlyWeatherByLocation(
            @androidx.annotation.NonNull @NonNull Coordinates coordinates) {
        return getCachedWeather(WeatherEntity.Type.HOURLY)
                .flatMap(
                        cachedData -> {
                            if (!cachedData.isEmpty()) {
                                return Observable.just(cachedData);
                            }
                            return fetchAndCacheHourlyWeather(coordinates);
                        });
    }

    /**
     * Fetches hourly weather data by location code (latitude and longitude) using the Tomorrow.io
     * API.
     *
     * <p>This method fetches hourly weather data from the Tomorrow.io API and caches it locally. It
     * uses the getWeatherByLocationCode method of the TomorrowIoService to retrieve the data.
     *
     * @param coordinates the coordinates of the location
     * @return an Observable that emits a list of WeatherResponse
     */
    @Override
    public Observable<List<WeatherResponse>> fetchAndCacheHourlyWeather(
            @androidx.annotation.NonNull @NonNull Coordinates coordinates) {
        WeatherEntity.Type type = WeatherEntity.Type.HOURLY;
        return tomorrowIoClient
                .getForecast(
                        coordinates,
                        TomorrowIoClient.HOURLY_WEATHER_FIELDS,
                        TomorrowIoClient.TIMESTEPS_ONE_HOUR,
                        DateTimeUtil.getAnHourLater(),
                        DateTimeUtil.getAnHourLaterTomorrow(),
                        TimeZone.getDefault(),
                        tomorrowIoKey)
                .map(response -> cacheWeatherDataLocally(response, type))
                .onErrorResumeNext(throwable -> fallbackToEmptyList(throwable, type));
    }

    /**
     * Fallback method to handle errors when fetching weather data.
     *
     * <p>This method logs the error and returns an empty list of weather data.
     *
     * @param throwable the error that occurred
     * @param type the type of weather data (DAILY, HOURLY, CURRENT)
     * @return an Observable that emits an empty list of WeatherResponse
     */
    private Observable<List<WeatherResponse>> fallbackToEmptyList(
            @NonNull Throwable throwable, @NonNull WeatherEntity.Type type) {
        return Observable.just(Collections.emptyList());
    }
}
