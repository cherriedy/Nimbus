package com.optlab.nimbus.data.network.openweather;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class OpenWeatherClient {
    private final OpenWeatherService openWeatherService;

    @Inject
    public OpenWeatherClient() {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://api.openweathermap.org/data/3.0/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .build();
        this.openWeatherService = retrofit.create(OpenWeatherService.class);
    }

    public OpenWeatherService getWeatherService() {
        return openWeatherService;
    }
}
