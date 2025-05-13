package com.optlab.nimbus.di;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.database.WeatherDatabase;
import com.optlab.nimbus.data.local.entity.Converters;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {
    @Provides
    @Singleton
    public static WeatherDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, WeatherDatabase.class, "nimbus-db").build();
    }

    @Provides
    @Singleton
    public static WeatherDao provideWeatherDao(@NonNull WeatherDatabase weatherDatabase) {
        return weatherDatabase.weatherDao();
    }
}
