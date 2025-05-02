package com.optlab.nimbus.di;

import com.optlab.nimbus.data.network.openweather.OpenWeatherClient;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/** Dependency injection module for providing network clients. */
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    @Singleton
    public static OpenWeatherClient provideOpenWeatherClient() {
        return new OpenWeatherClient();
    }

    @Provides
    @Singleton
    public static TomorrowIoClient provideTomorrowIoClient() {
        return new TomorrowIoClient();
    }
}
