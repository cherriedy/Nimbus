package com.optlab.nimbus.di;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.local.dao.ForecastDao;
import com.optlab.nimbus.data.local.dao.LocationDao;
import com.optlab.nimbus.data.network.openstreetmap.OpenStreetMapClient;
import com.optlab.nimbus.data.network.openstreetmap.OpenStreetMapService;
import com.optlab.nimbus.data.preferences.ForecastApiPreferences;
import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.repository.LocationRepository;
import com.optlab.nimbus.data.repository.LocationRepositoryImpl;
import com.optlab.nimbus.data.repository.PreferencesRepository;
import com.optlab.nimbus.data.repository.PreferencesRepositoryImpl;
import com.optlab.nimbus.data.repository.WeatherRepository;
import com.optlab.nimbus.data.repository.TomorrowIoRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * RepositoryModule is a Dagger module that provides the necessary dependencies for the repositories
 * used in the application.
 */
@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {
    @Provides
    @Singleton
    public static ForecastProvider provideEndpoint() {
        // Later: get the endpoint from the build config or any other source
        return ForecastProvider.TOMORROW_IO;
    }

    /** Inject WeatherRepository implementation based on the selected endpoint. */
    @Provides
    @Singleton
    public static WeatherRepository provideWeatherRepository(
            @NonNull ForecastProvider forecastProvider,
            @NonNull TomorrowIoClient tomorrowIoClient,
            @NonNull ForecastApiPreferences forecastApiPreferences,
            @NonNull ForecastDao forecastDao) {
        return switch (forecastProvider) {
            case OPEN_WEATHER -> null;
            case TOMORROW_IO ->
                    new TomorrowIoRepositoryImpl(
                            tomorrowIoClient, forecastApiPreferences, forecastDao);
        };
    }

    @Provides
    @Singleton
    public static PreferencesRepository providePreferencesRepository(
            SettingPreferences settingPreferences, ForecastApiPreferences forecastApiPreferences) {
        return new PreferencesRepositoryImpl(settingPreferences, forecastApiPreferences);
    }

    @Provides
    @Singleton
    public static LocationRepository provideLocationRepository(
            LocationDao locationDao, OpenStreetMapClient openStreetMapClient) {
        return new LocationRepositoryImpl(locationDao, openStreetMapClient);
    }
}
