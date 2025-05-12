package com.optlab.nimbus.di;

import android.content.Context;

import androidx.room.Room;

import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.database.WeatherDatabase;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class TestDatabaseModule {
    @Provides
    @Singleton
    public static WeatherDatabase provideDatabase(@ApplicationContext Context context) {
        // Use in-memory database for testing
        return Room.inMemoryDatabaseBuilder(context, WeatherDatabase.class)
                .allowMainThreadQueries() // Allow main thread queries for testing
                .build();
    }

    @Provides
    @Singleton
    public static WeatherDao provideDao(WeatherDatabase db) {
        return db.weatherDao();
    }
}
