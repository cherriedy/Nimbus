package com.optlab.nimbus.di;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.preferences.UserPrefsManager;
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
            @NonNull WeatherRepository repository, @NonNull UserPrefsManager userPrefs) {
        return new FetchingWorkerFactory(repository, userPrefs);
    }
}
