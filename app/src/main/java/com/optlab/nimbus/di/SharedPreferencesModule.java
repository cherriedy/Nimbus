package com.optlab.nimbus.di;

import android.content.Context;

import com.optlab.nimbus.data.preferences.SecurePrefsManager;
import com.optlab.nimbus.data.preferences.UserPreferencesManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class SharedPreferencesModule {
    @Provides
    @Singleton
    public static SecurePrefsManager provideSecurePrefsManager(
            @ApplicationContext Context context) {
        return new SecurePrefsManager(context);
    }

    @Provides
    @Singleton
    public static UserPreferencesManager provideUserPrefsManager(@ApplicationContext Context context) {
        return new UserPreferencesManager(context);
    }
}
