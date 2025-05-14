package com.optlab.nimbus;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.worker.CurrentWeatherWorker;
import com.optlab.nimbus.worker.DailyWeatherWorker;
import com.optlab.nimbus.worker.FetchingWorkerFactory;
import com.optlab.nimbus.worker.HourlyWeatherWorker;

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
public class NimbusApplication extends Application implements Configuration.Provider {
    @Inject protected SettingPreferences settingPreferences;
    @Inject protected FetchingWorkerFactory fetchingWorkerFactory;

    private static final String CURRENT_WEATHER_SYNC = "CurrentWeatherSync";
    private static final String HOURLY_WEATHER_SYNC = "HourlyWeatherSync";
    private static final String DAILY_WEATHER_SYNC = "DailyWeatherSync";

    /**
     * Network connection constraint for WorkManager. This constraint ensures that the work will
     * only run when the device is connected to the internet. It is used to prevent the
     * synchronization of weather data when there is no network connectivity.
     */
    private static final Constraints NETWORK_CONNECTION_CONSTRAINT =
            new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

    @Override
    public void onCreate() {
        super.onCreate();
        initTimber(); // Initialize Timber for logging.
        // syncWeatherData(); // Start syncing weather data.
    }

    /**
     * Syncs weather data by scheduling periodic work requests for current, hourly, and daily
     * weather updates.
     *
     * <p>This method uses WorkManager to schedule periodic work requests for fetching weather data.
     * It ensures that the work requests are unique and will not be duplicated if they already
     * exist. The work requests are scheduled with different intervals for current, hourly, and
     * daily weather updates. It will run in the background even if the app is not running.
     */
    private void syncWeatherData() {
        WorkManager workManager = WorkManager.getInstance(this);

        workManager.enqueueUniquePeriodicWork(
                CURRENT_WEATHER_SYNC, // Unique work name for current weather sync
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if it exists
                getCurrentWeatherSyncRequest() // Request for current weather sync
                );

        workManager.enqueueUniquePeriodicWork(
                HOURLY_WEATHER_SYNC, // Unique work name for hourly weather sync
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if it exists
                getHourlyWeatherSyncRequest() // Request for hourly weather sync
                );

        workManager.enqueueUniquePeriodicWork(
                DAILY_WEATHER_SYNC, // Unique work name for daily weather sync
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing work if it exists
                getDailyWeatherSyncRequest() // Request for daily weather sync
                );
    }

    @NonNull
    private static PeriodicWorkRequest getHourlyWeatherSyncRequest() {
        return new PeriodicWorkRequest.Builder(HourlyWeatherWorker.class, 1, TimeUnit.HOURS)
                .setConstraints(NETWORK_CONNECTION_CONSTRAINT)
                .build();
    }

    @NonNull
    private static PeriodicWorkRequest getCurrentWeatherSyncRequest() {
        return new PeriodicWorkRequest.Builder(CurrentWeatherWorker.class, 1, TimeUnit.HOURS)
                .setConstraints(NETWORK_CONNECTION_CONSTRAINT)
                .build();
    }

    @NonNull
    private static PeriodicWorkRequest getDailyWeatherSyncRequest() {
        return new PeriodicWorkRequest.Builder(DailyWeatherWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(NETWORK_CONNECTION_CONSTRAINT)
                .build();
    }

    /**
     * Initializes Timber for logging. In debug builds, it plants a DebugTree for logging debug
     * information.
     */
    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        // Provide the WorkManager configuration with a custom WorkerFactory for dependency
        // injection and other configurations. Beside, we have to declare the provider in the
        // AndroidManifest.xml file to enable the WorkManager. Otherwise it will not work, and
        // NoSuchMethodError will be thrown.
        return new Configuration.Builder().setWorkerFactory(fetchingWorkerFactory).build();
    }
}
