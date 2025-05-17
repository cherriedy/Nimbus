package com.optlab.nimbus.di;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.preferences.SecurePrefsManager;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.repository.WeatherRepository;
import com.optlab.nimbus.data.model.WeatherProvider;
import com.optlab.nimbus.data.repository.TomorrowIoRepository;

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
    public static WeatherProvider provideEndpoint() {
        // Later: get the endpoint from the build config or any other source
        return WeatherProvider.TOMORROW_IO;
    }

    /** Inject WeatherRepository implementation based on the selected endpoint. */
    @Provides
    @Singleton
    public static WeatherRepository provideRepository(
            @NonNull WeatherProvider weatherProvider,
            @NonNull TomorrowIoClient tomorrowIoClient,
            @NonNull SecurePrefsManager securePrefsManager,
            @NonNull WeatherDao weatherDao) {
        return switch (weatherProvider) {
            case OPEN_WEATHER -> null;
            case TOMORROW_IO ->
                    new TomorrowIoRepository(tomorrowIoClient, securePrefsManager, weatherDao);
        };
    }
}
