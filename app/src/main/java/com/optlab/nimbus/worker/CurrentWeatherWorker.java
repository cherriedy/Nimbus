package com.optlab.nimbus.worker;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.optlab.nimbus.data.preferences.interfaces.SettingPreferences;
import com.optlab.nimbus.data.repository.interfaces.ForecastRepository;

public class CurrentWeatherWorker extends Worker {
    private final ForecastRepository repository;
    private final SettingPreferences userPrefs;

    public CurrentWeatherWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams,
            @NonNull ForecastRepository repository,
            @NonNull SettingPreferences userPrefs) {
        super(context, workerParams);
        this.repository = repository;
        this.userPrefs = userPrefs;
    }

    @NonNull
    @Override
    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Result doWork() {
        // try {
        //     repository
        //             .getCurrentWeather(userPrefs.getLocation(0))
        //             .firstOrError()
        //             .timeout(30, TimeUnit.SECONDS)
        //             .blockingGet();
        //     return Result.success();
        // } catch (Exception e) {
        //     Timber.e(e, "Error fetching current weather");
        //     return Result.failure();
        // }
        return Result.failure();
    }
}
