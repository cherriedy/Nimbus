package com.optlab.nimbus.di;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.database.WeatherDatabase;
import com.optlab.nimbus.data.local.entity.Converters;
import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.data.preferences.WeatherApiPreferences;
import com.optlab.nimbus.data.repository.PreferencesRepository;
import com.optlab.nimbus.data.repository.PreferencesRepositoryImpl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import timber.log.Timber;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {
    @Provides
    @Singleton
    public static WeatherDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, WeatherDatabase.class, "nimbus-db")
                .setQueryCallback(
                        (sqlQuery, bindArgs) -> {
                            StringBuilder logMessage =
                                    new StringBuilder("SQL Query: ").append(sqlQuery);
                            if (bindArgs != null) {
                                logMessage.append(", Bind Args: ");
                                for (Object arg : bindArgs) {
                                    logMessage.append(arg).append(", ");
                                }
                            }
                            Timber.d(logMessage.toString());
                        },
                        Executors.newSingleThreadExecutor())
                .build();
    }

    @Provides
    @Singleton
    public static WeatherDao provideWeatherDao(@NonNull WeatherDatabase weatherDatabase) {
        return weatherDatabase.weatherDao();
    }

    @Provides
    @Singleton
    public static PreferencesRepository providePreferencesRepository(
            SettingPreferences settingPreferences, WeatherApiPreferences weatherApiPreferences) {
        return new PreferencesRepositoryImpl(settingPreferences, weatherApiPreferences);
    }
}
