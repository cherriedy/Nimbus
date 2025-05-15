package com.optlab.nimbus.di;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.optlab.nimbus.data.local.dao.ForecastDao;
import com.optlab.nimbus.data.local.dao.LocationDao;
import com.optlab.nimbus.data.local.database.LocationDatabase;
import com.optlab.nimbus.data.local.database.ForecastDatabase;

import java.util.List;
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
    public static ForecastDatabase provideForecastDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, ForecastDatabase.class, "nimbus-db")
                .setQueryCallback(DatabaseModule::logQuery, Executors.newSingleThreadExecutor())
                .build();
    }

    @Provides
    @Singleton
    public static ForecastDao provideForecastDao(@NonNull ForecastDatabase forecastDatabase) {
        return forecastDatabase.weatherDao();
    }

    @Provides
    @Singleton
    public static LocationDatabase provideLocationDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, LocationDatabase.class, "location-db")
                .setQueryCallback(DatabaseModule::logQuery, Executors.newSingleThreadExecutor())
                .build();
    }

    @Provides
    @Singleton
    public static LocationDao provideLocationDao(@NonNull LocationDatabase locationDatabase) {
        return locationDatabase.locationDao();
    }

    private static void logQuery(String sqlQuery, List<?> bindArgs) {
        StringBuilder logMessage = new StringBuilder("SQL Query: ").append(sqlQuery);
        if (bindArgs != null) {
            logMessage.append(", Bind Args: ");
            for (Object arg : bindArgs) {
                logMessage.append(arg).append(", ");
            }
        }
        Timber.d(logMessage.toString());
    }
}
