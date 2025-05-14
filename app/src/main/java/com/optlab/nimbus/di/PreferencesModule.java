package com.optlab.nimbus.di;

import android.content.Context;

import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.data.preferences.SettingPreferencesImpl;
import com.optlab.nimbus.data.preferences.WeatherApiPreferences;
import com.optlab.nimbus.data.preferences.WeatherApiPreferencesImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class PreferencesModule {
    @Provides
    @Singleton
    public static WeatherApiPreferences provideWeatherApiPreferences(
            @ApplicationContext Context context) {
        return new WeatherApiPreferencesImpl(context);
    }

    @Provides
    @Singleton
    public static SettingPreferences provideSettingPreferences(
            @ApplicationContext Context context) {
        return new SettingPreferencesImpl(context);
    }
}
