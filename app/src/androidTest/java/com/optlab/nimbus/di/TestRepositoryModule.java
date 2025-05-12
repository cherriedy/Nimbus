package com.optlab.nimbus.di;

import android.content.Context;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.common.Endpoint;
import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.preferences.SecurePrefsManager;
import com.optlab.nimbus.data.repository.TomorrowIoRepository;
import com.optlab.nimbus.data.repository.WeatherRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class TestRepositoryModule {
    @Provides
    @Singleton
    public static Endpoint provideEndpoint() {
        // Later: get the endpoint from the build config or any other source
        return Endpoint.TOMORROW_IO;
    }

    /** Inject WeatherRepository implementation based on the selected endpoint. */
    @Provides
    @Singleton
    public static WeatherRepository provideRepository(
            @ApplicationContext Context context,
            @NonNull Endpoint endpoint,
            @NonNull TomorrowIoClient tomorrowIoClient,
            @NonNull SecurePrefsManager securePrefsManager,
            @NonNull WeatherDao weatherDao) {
        return switch (endpoint) {
            case OPEN_WEATHER -> null;
            case TOMORROW_IO ->
                    new TomorrowIoRepository(
                            context, tomorrowIoClient, securePrefsManager, weatherDao);
        };
    }
}
