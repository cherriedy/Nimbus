package com.optlab.nimbus.data.network.tomorrowio;

import io.reactivex.rxjava3.core.Single;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * TomorrowIoService is an interface that defines the API endpoints for the Tomorrow.io weather
 */
public interface TomorrowIoService {
    String FIELDS = String.join(",",
            "temperature",
            "temperatureMax",
            "temperatureMin",
            "sunriseTime",
            "sunsetTime",
            "windSpeed",
            "humidity",
            "pressureSurfaceLevel",
            "visibility",
            "uvIndex",
            "windDirection",
            "weatherCode"
    );
    String PLUS_1_DAYS_FROM_TODAY = "nowPlus1d";
    String PLUS_5_DAYS_FROM_TODAY = "nowPlus5d";
    String TIMESTEPS = "1d,1h";
    String METRIC = "metric";


    /**
     * Get weather data by location code with start and end time.
     *
     * @param location  location coordinates in the format "latitude,longitude"
     * @param fields    which fields to include in the response
     * @param timesteps how long to get the next forecast
     * @param units     temperature unit
     * @param startTime start time for the forecast
     * @param endTime   end time for the forecast
     * @param timezone  timezone for the forecast
     * @param apiKey    API key for authentication
     * @return an observable of TomorrowIoResponse
     */
    @GET("v4/timelines")
    Single<TomorrowIoResponse> getForecast(
            @Query("location") String location,
            @Query("fields") String fields,
            @Query("timesteps") String timesteps,
            @Query("units") String units,
            @Query("startTime") String startTime,
            @Query("endTime") String endTime,
            @Query("timezone") String timezone,
            @Query("apikey") String apiKey);
}
