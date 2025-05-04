package com.optlab.nimbus;

import android.app.Application;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.optlab.nimbus.data.preferences.UserPrefsManager;
import com.optlab.nimbus.worker.DailyWeatherWorker;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

/**
 * The NimbusApplication class is the entry point of the application, used to initialize Hilt for
 * dependency injection, Timber for logging, and other application-wide configurations. We have to
 * set the application name in the AndroidManifest.xml file to use this class as the application
 * class.
 */
@HiltAndroidApp // Annotation to trigger Hilt's code generation and setup.
public class NimbusApplication extends Application {
    @Inject protected UserPrefsManager userPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        initTimber(); // Initialize Timber for logging.
        syncWeatherData(); // Start syncing weather data.
    }

    private void syncWeatherData() {
        WorkRequest weatherSyncRequest =
                new PeriodicWorkRequest.Builder(DailyWeatherWorker.class, 1, TimeUnit.DAYS).build();

        // Enqueue the work request to start syncing weather data.
        WorkManager.getInstance(this).enqueue(weatherSyncRequest);
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
