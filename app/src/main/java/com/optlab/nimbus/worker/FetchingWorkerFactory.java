package com.optlab.nimbus.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.data.repository.WeatherRepository;

import timber.log.Timber;

/**
 * The FetchingWorkerFactory class is responsible for creating instances of different worker classes
 * based on the worker class name provided. It extends the WorkerFactory class and overrides the
 * createWorker method to instantiate the appropriate worker.
 */
public class FetchingWorkerFactory extends WorkerFactory {
    private final WeatherRepository repository;
    private final SettingPreferences userPrefs;

    public FetchingWorkerFactory(
            @NonNull WeatherRepository repository, @NonNull SettingPreferences userPrefs) {
        this.repository = repository;
        this.userPrefs = userPrefs;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(
            @NonNull Context context, // Context in which the worker is running
            @NonNull String workerClassName, // Name of the worker class to be created
            @NonNull WorkerParameters workerParameters) {
        try {
            if (workerClassName.equals(CurrentWeatherWorker.class.getName())) {
                return new CurrentWeatherWorker(context, workerParameters, repository, userPrefs);
            }
            if (workerClassName.equals(DailyWeatherWorker.class.getName())) {
                return new DailyWeatherWorker(context, workerParameters, repository, userPrefs);
            }
            if (workerClassName.equals(HourlyWeatherWorker.class.getName())) {
                return new HourlyWeatherWorker(context, workerParameters, repository, userPrefs);
            }
        } catch (Exception e) {
            Timber.e("Error creating worker: %s", e.getMessage());
        }
        return null;
    }
}
