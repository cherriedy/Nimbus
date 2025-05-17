package com.optlab.nimbus.worker;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.optlab.nimbus.data.model.Forecast;
import com.optlab.nimbus.data.preferences.interfaces.SettingPreferences;
import com.optlab.nimbus.data.repository.interfaces.ForecastRepository;

import java.util.List;

import timber.log.Timber;

public class DailyWeatherWorker extends Worker {
    private final ForecastRepository repository;
    private final SettingPreferences userPrefs;

    public DailyWeatherWorker(
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
    @SuppressLint({"CheckResult"})
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Result doWork() {
        // try {
        //     repository
        //             .fetchAndCacheDailyWeather(userPrefs.getLocation(0))
        //             .subscribeOn(Schedulers.io())
        //             .subscribe(this::onSuccess, this::onError);
        //     Timber.d("Periodic work request for daily weather data completed");
        //     return Result.success();
        // } catch (Exception e) {
        //     Timber.e("Daily weather sync failed: %s", e.getMessage());
        //     return Result.failure();
        // }
        return  Result.success();
    }

    private void onError(Throwable throwable) {
        Timber.e("Error fetching daily weather data: %s", throwable.getMessage());
    }

    private void onSuccess(List<Forecast> forecastRespons) {
        Timber.d("Daily weather data fetched successfully");
    }
}
