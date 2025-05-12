package com.optlab.nimbus.worker;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.preferences.UserPreferences;
import com.optlab.nimbus.data.repository.WeatherRepository;

import java.util.List;

import io.reactivex.rxjava3.schedulers.Schedulers;

import timber.log.Timber;

public class CurrentWeatherWorker extends Worker {
    private final WeatherRepository repository;
    private final UserPreferences userPrefs;

    public CurrentWeatherWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams,
            @NonNull WeatherRepository repository,
            @NonNull UserPreferences userPrefs) {
        super(context, workerParams);
        this.repository = repository;
        this.userPrefs = userPrefs;
    }

    @NonNull
    @Override
    @SuppressLint({"CheckResult"})
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Result doWork() {
        try {
            repository
                    .fetchAndCacheCurrentWeather(userPrefs.getLocation(0))
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::onSuccess, this::onError);
            Timber.d("Periodic work request for current weather data completed");
            return Result.success();
        } catch (Exception e) {
            Timber.e("Current weather sync failed: %s", e.getMessage());
            return Result.failure();
        }
    }

    private void onError(Throwable throwable) {
        Timber.e("Error fetching current weather data: %s", throwable.getMessage());
    }

    private void onSuccess(List<WeatherResponse> weatherResponses) {
        Timber.d("Current weather data fetched successfully");
    }
}
