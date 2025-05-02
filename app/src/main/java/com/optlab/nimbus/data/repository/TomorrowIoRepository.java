package com.optlab.nimbus.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.optlab.nimbus.constant.TomorrowIoConstant;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.UnifiedWeatherResponse;
import com.optlab.nimbus.data.model.mapper.TomorrowIoMapper;
import com.optlab.nimbus.data.preferences.SecurePrefsManager;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.utility.DateTimeUtil;

import io.reactivex.rxjava3.core.Observable;

import java.util.List;
import java.util.TimeZone;

/** TomorrowIoRepository is responsible for fetching weather data from the Tomorrow.io API. */
public class TomorrowIoRepository implements WeatherRepository {
    private final Context context;
    private final TomorrowIoClient tomorrowIoClient;
    private final String tomorrowIoKey;

    /** Constructor injection for TomorrowIoClient */
    public TomorrowIoRepository(
            @NonNull Context context,
            @NonNull TomorrowIoClient tomorrowIoClient,
            @NonNull SecurePrefsManager securePrefsManager) {
        this.context = context;
        this.tomorrowIoClient = tomorrowIoClient;

        tomorrowIoKey = securePrefsManager.getApiKey(SecurePrefsManager.TOMORROW_IO_API_KEY);
    }

    /**
     * Fetches weather data by location code (latitude and longitude) using the Tomorrow.io API.
     *
     * <p>This method retrieves weather forecast data (5 days for free api) for a specific location
     * based on the provided coordinates and API key. It returns an Observable that emits a list of
     * UnifiedWeatherResponse objects by mapping the API response to the UnifiedWeatherResponse
     * model.
     *
     * @param coordinates the coordinates of the location
     * @return an Observable that emits a list of UnifiedWeatherResponse
     */
    public @NonNull Observable<List<UnifiedWeatherResponse>> getDailyWeatherByLocation(
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
                // Map the response to a list of UnifiedWeatherResponse, since the API returns a
                // TomorrowIoResponse object so we need to map it to a list of
                // UnifiedWeatherResponse
                .map(response -> TomorrowIoMapper.map(context, response));
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
