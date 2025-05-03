package com.optlab.nimbus.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.constant.TomorrowIoConstant;
import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.UnifiedWeatherResponse;
import com.optlab.nimbus.data.model.mapper.TomorrowIoMapper;
import com.optlab.nimbus.data.preferences.SecurePrefsManager;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.utility.DateTimeUtil;

import io.reactivex.rxjava3.core.Observable;
import timber.log.Timber;

import java.lang.reflect.Type;
import java.util.List;

/** TomorrowIoRepository is responsible for fetching weather data from the Tomorrow.io API. */
public class TomorrowIoRepository implements WeatherRepository {
    private final Context context;
    private final TomorrowIoClient tomorrowIoClient;
    private final String tomorrowIoKey;
    private final WeatherDao weatherDao;

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
     *
     * @param coordinates the coordinates of the location
     * @return an Observable that emits a list of UnifiedWeatherResponse
     */
    public @NonNull Observable<List<UnifiedWeatherResponse>> getDailyWeatherByLocation(
            @NonNull Coordinates coordinates) {
        return Observable.concat(
                getCachedWeather(WeatherEntity.Type.DAILY), fetchAndCacheDailyWeather(coordinates));
    }

    /**
     * Fetches and caches daily weather data by location code (latitude and longitude) using the
     * Tomorrow.io API.
     *
     * @param coordinates the coordinates of the location
     * @return an Observable that emits a list of UnifiedWeatherResponse
     */
    private Observable<List<UnifiedWeatherResponse>> fetchAndCacheDailyWeather(
            @NonNull Coordinates coordinates) {
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
                .map(
                        response -> {
                            List<UnifiedWeatherResponse> weatherData =
                                    TomorrowIoMapper.map(context, response);

                            // Remove expired weather data from the local database
                            long expiryTime =
                                    System.currentTimeMillis() - TomorrowIoConstant.EXPIRY_TIME;
                            weatherDao.deleteExpiry(expiryTime);

                            // Cache the weather data in the local database
                            WeatherEntity entity = new WeatherEntity();
                            entity.setType(WeatherEntity.Type.DAILY);
                            entity.setData(new Gson().toJson(weatherData));
                            entity.setTimestamp(System.currentTimeMillis());
                            weatherDao.insertWeather(entity);
                            return weatherData;
                        });
    }

    /**
     * Fetches cached weather data from the local database.
     *
     * @param type the type of weather data (DAILY, HOURLY, CURRENT)
     * @return an Observable that emits a list of UnifiedWeatherResponse
     */
    private Observable<List<UnifiedWeatherResponse>> getCachedWeather(
            @NonNull WeatherEntity.Type type) {
        return Observable.fromCallable(
                () -> {
                    WeatherEntity entity = weatherDao.getLatestWeather(type);
                    if (entity == null) {
                        Timber.e("No cached weather data found");
                        return null;
                    }
                    Type reflectType = new TypeToken<List<UnifiedWeatherResponse>>() {}.getType();
                    return new Gson().fromJson(entity.getData(), reflectType);
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
    public Observable<List<UnifiedWeatherResponse>> getCurrentWeatherByLocation(
            @NonNull Coordinates coordinates) {
        return tomorrowIoClient
                .getTomorrowIoService()
                .getWeatherByLocationCode(
                        coordinates.getLocationParameter(),
                        TomorrowIoConstant.CURRENT_WEATHER_FIELDS,
                        TomorrowIoConstant.TIMESTEPS_CURRENT,
                        TomorrowIoConstant.METRIC,
                        DateTimeUtil.getTimeZoneId(),
                        tomorrowIoKey)
                // Map the response to a list of UnifiedWeatherResponse, since the API returns a
                // TomorrowIoResponse object so we need to map it to a list of
                // UnifiedWeatherResponse
                .map(response -> TomorrowIoMapper.map(context, response));
    }

    @Override
    public Observable<List<UnifiedWeatherResponse>> getHourlyWeatherByLocation(
            @NonNull Coordinates coordinates) {
        return tomorrowIoClient
                .getTomorrowIoService()
                .getWeatherByLocationCode(
                        coordinates.getLocationParameter(),
                        TomorrowIoConstant.HOURLY_WEATHER_FIELDS,
                        TomorrowIoConstant.TIMESTEPS_ONE_HOUR,
                        TomorrowIoConstant.METRIC,
                        DateTimeUtil.getStartOfDay(),
                        DateTimeUtil.getEndOfDay(),
                        DateTimeUtil.getTimeZoneId(),
                        tomorrowIoKey)
                .map(response -> TomorrowIoMapper.map(context, response));
    }
}
