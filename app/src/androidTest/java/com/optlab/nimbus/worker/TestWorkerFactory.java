package com.optlab.nimbus.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.data.repository.WeatherRepository;

public class TestWorkerFactory extends WorkerFactory {
    private final WeatherRepository repository;
    private final SettingPreferences userPrefs;

    public TestWorkerFactory(WeatherRepository repository, SettingPreferences userPrefs) {
        this.repository = repository;
        this.userPrefs = userPrefs;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(
            @NonNull Context context,
            @NonNull String workerClassName,
            @NonNull WorkerParameters workerParameters) {
        if (workerClassName.equals(CurrentWeatherWorker.class.getName())) {
            return new CurrentWeatherWorker(context, workerParameters, repository, userPrefs);
        }
        if (workerClassName.equals(DailyWeatherWorker.class.getName())) {
            return new DailyWeatherWorker(context, workerParameters, repository, userPrefs);
        }
        if (workerClassName.equals(HourlyWeatherWorker.class.getName())) {
            return new HourlyWeatherWorker(context, workerParameters, repository, userPrefs);
        }
        return null;
    }
}
