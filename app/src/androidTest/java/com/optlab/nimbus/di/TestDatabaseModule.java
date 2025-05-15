package com.optlab.nimbus.di;

import android.content.Context;

import androidx.room.Room;

import com.optlab.nimbus.data.local.dao.ForecastDao;
import com.optlab.nimbus.data.local.database.ForecastDatabase;


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
    public static ForecastDatabase provideDatabase(@ApplicationContext Context context) {
        // Use in-memory database for testing
        return Room.inMemoryDatabaseBuilder(context, ForecastDatabase.class)
                .allowMainThreadQueries() // Allow main thread queries for testing
                .build();
    }

    @Provides
    @Singleton
    public static ForecastDao provideDao(ForecastDatabase db) {
        return db.weatherDao();
    }
}
