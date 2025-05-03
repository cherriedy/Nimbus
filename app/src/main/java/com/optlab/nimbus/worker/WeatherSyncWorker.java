package com.optlab.nimbus.worker;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.optlab.nimbus.data.preferences.UserPrefsManager;
import com.optlab.nimbus.data.repository.WeatherRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class WeatherSyncWorker extends Worker {
    private final WeatherRepository repository;
    private final UserPrefsManager userPrefs;

    @Inject
    public WeatherSyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams,
            @NonNull WeatherRepository repository,
            @NonNull UserPrefsManager userPrefs) {
        super(context, workerParams);
        this.repository = repository;
        this.userPrefs = userPrefs;
    }

    @SuppressLint("CheckResult")
    @NonNull
    @Override
    public Result doWork() {
        try {
            repository
                    .getDailyWeatherByLocation(userPrefs.getLocation(0))
                    .subscribeOn(Schedulers.io())
                    .blockingFirst();
            Timber.d("Sync completed successfully");
            return Result.success();
        } catch (Exception e) {
            Timber.e("Sync failed: %s", e.getMessage());
            return Result.failure();
        }
    }
}
