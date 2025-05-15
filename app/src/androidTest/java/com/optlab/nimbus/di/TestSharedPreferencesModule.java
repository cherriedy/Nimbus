package com.optlab.nimbus.di;

import android.content.Context;

import com.optlab.nimbus.data.preferences.ForecastApiPreferencesImpl;
import com.optlab.nimbus.data.preferences.SettingPreferencesImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class TestSharedPreferencesModule {
    @Provides
    @Singleton
    public static ForecastApiPreferencesImpl provideSecurePrefsManager(
            @ApplicationContext Context context) {
        return new ForecastApiPreferencesImpl(context);
    }

    @Provides
    @Singleton
    public static SettingPreferencesImpl provideUserPrefsManager(
            @ApplicationContext Context context) {
        return new SettingPreferencesImpl(context);
    }
}
