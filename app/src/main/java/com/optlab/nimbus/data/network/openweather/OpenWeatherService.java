package com.optlab.nimbus.data.network.openweather;

import io.reactivex.rxjava3.core.Observable;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherService {
    @GET("onecall?exclude=minutely")
    Observable<OpenWeatherResponse> getWeatherByLocationCode(
            @Query("lat") String lat, @Query("lon") String lon, @Query("appid") String apiKey);

    @GET("weather")
    Observable<OpenWeatherResponse> getWeatherByCityName(
            @Query("q") String cityName, @Query("appid") String apiKey);
}
