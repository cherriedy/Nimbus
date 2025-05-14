package com.optlab.nimbus.di;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.data.preferences.SettingPreferencesImpl;
import com.optlab.nimbus.data.repository.WeatherRepository;
import com.optlab.nimbus.worker.FetchingWorkerFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class WorkerModule {
    @Provides
    @Singleton
    public static FetchingWorkerFactory provideFetchingWorkerFactory(
            @NonNull WeatherRepository repository, @NonNull SettingPreferences userPrefs) {
        return new FetchingWorkerFactory(repository, userPrefs);
    }
}
