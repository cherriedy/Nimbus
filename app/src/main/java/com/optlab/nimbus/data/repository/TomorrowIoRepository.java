package com.optlab.nimbus.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.constant.ResponseConstant;
import com.optlab.nimbus.constant.TagConstant;
import com.optlab.nimbus.constant.TomorrowIoConstant;
import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.model.mapper.TomorrowIoMapper;
import com.optlab.nimbus.data.model.tomorrowio.TomorrowIoResponse;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.preferences.SecurePrefsManager;
import com.optlab.nimbus.utility.DateTimeUtil;

import io.reactivex.rxjava3.core.Observable;

import timber.log.Timber;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/** TomorrowIoRepository is responsible for fetching weather data from the Tomorrow.io API. */
public class TomorrowIoRepository implements WeatherRepository {
    private final Context context;
    private final TomorrowIoClient tomorrowIoClient;
    private final String tomorrowIoKey;
    private final WeatherDao weatherDao;
    private final Gson gson = new Gson();
    private final Type reflectType = new TypeToken<List<WeatherResponse>>() {}.getType();

    /** Constructor injection for TomorrowIoClient */
    public TomorrowIoRepository(
            @NonNull Context context,
            @NonNull TomorrowIoClient tomorrowIoClient,
            @NonNull SecurePrefsManager securePrefsManager,
            @NonNull WeatherDao weatherDao) {
        this.context = context;
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
            @NonNull Coordinates coordinates) {
        return getCachedWeather(WeatherEntity.Type.DAILY)
                .flatMap(
                        cachedData -> {
                            if (!cachedData.isEmpty()) {
                                Timber.tag(TagConstant.DATABASE)
                                        .d("Daily: Returning cached daily weather data");
                                return Observable.just(cachedData);
                            }
                            Timber.tag(TagConstant.NETWORK)
                                    .d("Daily: Fetching new daily weather data from API");
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
            @NonNull Coordinates coordinates) {
        WeatherEntity.Type type = WeatherEntity.Type.DAILY;
        return tomorrowIoClient
                .getTomorrowIoService()
                .getWeatherByLocationCode(
                        coordinates.getLocationParameter(),
                        TomorrowIoConstant.DAILY_WEATHER_FIELDS,
                        TomorrowIoConstant.TIMESTEPS_ONE_DAY,
                        TomorrowIoConstant.METRIC,
                        TomorrowIoConstant.PLUS_1_DAYS_FROM_TODAY,
                        TomorrowIoConstant.PLUS_5_DAYS_FROM_TODAY,
                        DateTimeUtil.getTimeZoneId(),
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
                        Timber.tag(TagConstant.DATABASE)
                                .e("%s: No cached weather data found", type.name());
                        return Collections.emptyList(); // Return an empty list if no data is found
                    }

                    if (entity.isExpired()) {
                        Timber.tag(TagConstant.DATABASE)
                                .e("%s: Cached weather data expired", type.name());
                        weatherDao.deleteExpiry(System.currentTimeMillis()); // Delete expired data
                        return Collections.emptyList(); // Return an empty list if data is expired
                    }

                    List<WeatherResponse> data = gson.fromJson(entity.getData(), reflectType);
                    Timber.tag(TagConstant.DATABASE)
                            .d("%s: Cached weather data found, size: %s", type.name(), data.size());
                    return data;
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
            @NonNull Coordinates coordinates) {
        return getCachedWeather(WeatherEntity.Type.CURRENT)
                .flatMap(
                        cachedWeather -> {
                            if (!cachedWeather.isEmpty()) {
                                Timber.tag(TagConstant.DATABASE)
                                        .d("Current: Returning cached current weather data");
                                return Observable.just(cachedWeather);
                            }
                            Timber.tag(TagConstant.NETWORK)
                                    .d("Current: Fetching new current weather data from API");
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
            @NonNull Coordinates coordinates) {
        WeatherEntity.Type type = WeatherEntity.Type.CURRENT;
        return tomorrowIoClient
                .getTomorrowIoService()
                .getWeatherByLocationCode(
                        coordinates.getLocationParameter(),
                        TomorrowIoConstant.CURRENT_WEATHER_FIELDS,
                        TomorrowIoConstant.TIMESTEPS_CURRENT,
                        TomorrowIoConstant.METRIC,
                        DateTimeUtil.getTimeZoneId(),
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
        // Map the TomorrowIoResponse to a list of UnifiedWeatherResponse
        List<WeatherResponse> weatherData = TomorrowIoMapper.map(context, response);

        switch (type) { // Delete expired weather data based on the type
            case CURRENT -> weatherDao.deleteExpiry(ResponseConstant.CURRENT_EXPIRY_TIME);
            case DAILY, HOURLY -> weatherDao.deleteExpiry(ResponseConstant.DAILY_EXPIRY_TIME);
            default -> Timber.tag(TagConstant.DATABASE).e("Unknown weather type: %s", type.name());
        }

        // Insert the new weather data into the database
        WeatherEntity entity = new WeatherEntity();
        entity.setType(type);
        entity.setData(new Gson().toJson(weatherData));
        entity.setTimestamp(System.currentTimeMillis());
        if (weatherDao.insertWeather(entity) != -1) {
            Timber.tag(TagConstant.DATABASE).d("%s: Weather data cached successfully", type.name());
        } else {
            Timber.tag(TagConstant.DATABASE).e("%s: Failed to cache weather data", type.name());
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
            @NonNull Coordinates coordinates) {
        return getCachedWeather(WeatherEntity.Type.HOURLY)
                .flatMap(
                        cachedData -> {
                            if (!cachedData.isEmpty()) {
                                Timber.tag(TagConstant.DATABASE)
                                        .d("Hourly: Returning cached hourly weather data");
                                return Observable.just(cachedData);
                            }
                            Timber.tag(TagConstant.NETWORK)
                                    .d("Hourly: Fetching new hourly weather data from API");
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
            @NonNull Coordinates coordinates) {
        WeatherEntity.Type type = WeatherEntity.Type.HOURLY;
        return tomorrowIoClient
                .getTomorrowIoService()
                .getWeatherByLocationCode(
                        coordinates.getLocationParameter(),
                        TomorrowIoConstant.HOURLY_WEATHER_FIELDS,
                        TomorrowIoConstant.TIMESTEPS_ONE_HOUR,
                        TomorrowIoConstant.METRIC,
                        DateTimeUtil.getAnHourLater(),
                        DateTimeUtil.getAnHourLaterTomorrow(),
                        DateTimeUtil.getTimeZoneId(),
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
        Timber.tag(TagConstant.DATABASE).e("%s: %s", type.name(), throwable.getMessage());
        return Observable.just(Collections.emptyList());
    }
}
