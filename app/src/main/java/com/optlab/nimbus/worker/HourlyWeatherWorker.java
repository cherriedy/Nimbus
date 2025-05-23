package com.optlab.nimbus.worker;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.optlab.nimbus.data.network.WeatherResponse;
import com.optlab.nimbus.data.preferences.UserPreferences;
import com.optlab.nimbus.data.repository.WeatherRepository;

import java.util.List;

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

    @NonNull
    @Override
    @SuppressLint({"CheckResult"})
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Result doWork() {
        try {
            repository
                    .fetchAndCacheHourlyWeather(userPrefs.getLocation(0))
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::onSuccess, this::onError);
            Timber.d("Periodic work request for hourly weather data completed");
            return Result.success();
        } catch (Exception e) {
            Timber.e("Hourly weather sync failed: %s", e.getMessage());
            return Result.failure();
        }
    }

    private void onError(Throwable throwable) {
        Timber.e("Error fetching hourly weather data: %s", throwable.getMessage());
    }

    private void onSuccess(List<WeatherResponse> weatherResponses) {
        Timber.d("Hourly weather data fetched successfully");
    }
}
