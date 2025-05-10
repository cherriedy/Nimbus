package com.optlab.nimbus.worker;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.optlab.nimbus.data.preferences.UserPreferences;
import com.optlab.nimbus.data.preferences.UserPreferencesManager;
import com.optlab.nimbus.data.repository.WeatherRepository;

import io.reactivex.rxjava3.schedulers.Schedulers;

import timber.log.Timber;

public class HourlyWeatherWorker extends Worker {
    private final WeatherRepository repository;
    private final UserPreferences userPrefs;

    public HourlyWeatherWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams,
            @NonNull WeatherRepository repository,
            @NonNull UserPreferences userPrefs) {
        super(context, workerParams);
        this.repository = repository;
        this.userPrefs = userPrefs;
    }

    /**
     * @noinspection ResultOfMethodCallIgnored
     */
    @SuppressLint("CheckResult")
    @NonNull
    @Override
    public Result doWork() {
        try {
            repository
                    .fetchAndCacheHourlyWeather(userPrefs.getLocation(0))
                    .subscribeOn(Schedulers.io())
                    .blockingFirst();
            Timber.d("Hourly weather data fetched and cached successfully.");
            return Result.success();
        } catch (Exception e) {
            Timber.e("Error fetching hourly weather data: %s", e.getMessage());
            return Result.failure();
        }
    }
}
