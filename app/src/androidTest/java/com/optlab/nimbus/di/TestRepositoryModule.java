package com.optlab.nimbus.di;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.local.dao.ForecastDao;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.preferences.ForecastApiPreferencesImpl;
import com.optlab.nimbus.data.repository.TomorrowIoRepositoryImpl;
import com.optlab.nimbus.data.repository.WeatherRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class TestRepositoryModule {
    @Provides
    @Singleton
    public static ForecastProvider provideEndpoint() {
        // Later: get the endpoint from the build config or any other source
        return ForecastProvider.TOMORROW_IO;
    }

    /** Inject WeatherRepository implementation based on the selected endpoint. */
    @Provides
    @Singleton
    public static WeatherRepository provideRepository(
            @NonNull ForecastProvider forecastProvider,
            @NonNull TomorrowIoClient tomorrowIoClient,
            @NonNull ForecastApiPreferencesImpl weatherApiPreferencesImpl,
            @NonNull ForecastDao forecastDao) {
        return switch (forecastProvider) {
            case OPEN_WEATHER -> null;
            case TOMORROW_IO ->
                    new TomorrowIoRepositoryImpl(tomorrowIoClient, weatherApiPreferencesImpl, forecastDao);
        };
    }
}
