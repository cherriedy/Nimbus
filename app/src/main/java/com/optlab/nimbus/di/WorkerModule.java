package com.optlab.nimbus.di;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.preferences.interfaces.SettingPreferences;
import com.optlab.nimbus.data.repository.interfaces.ForecastRepository;
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
            @NonNull ForecastRepository repository, @NonNull SettingPreferences userPrefs) {
        return new FetchingWorkerFactory(repository, userPrefs);
    }
}
