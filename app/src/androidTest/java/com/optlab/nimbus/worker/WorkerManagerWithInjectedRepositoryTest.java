package com.optlab.nimbus.worker;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.testing.TestDriver;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.preferences.UserPreferencesManager;
import com.optlab.nimbus.data.repository.TomorrowIoRepository;
import com.optlab.nimbus.data.repository.WeatherRepository;
import com.optlab.nimbus.di.DatabaseModule;
import com.optlab.nimbus.di.RepositoryModule;
import com.optlab.nimbus.di.SharedPreferencesModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

@HiltAndroidTest
@UninstallModules({DatabaseModule.class, SharedPreferencesModule.class, RepositoryModule.class})
@RunWith(AndroidJUnit4.class)
public class WorkerManagerWithInjectedRepositoryTest {
    /** Hilt rule to inject dependencies into the test class. */
    @Rule public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    /**
     * Repository to be used in the test. This is injected by Hilt and is used to provide data to
     * the workers.
     */
    @Inject protected WeatherRepository repository;

    /**
     * UserPreferencesManager to be used in the test. This is injected by Hilt and is used to
     * provide user preferences to the workers.
     */
    @Inject protected UserPreferencesManager userPreferencesManager;

    private WorkManager workManager;
    private TestDriver testDriver;
    private Constraints conectivityConstraints;
    private List<Class<? extends Worker>> workers;

    @Before
    public void setUp() {
        hiltRule.inject(); // Inject dependencies into the test class.

        // Initialize the list of workers to be tested.
        workers =
                List.of(
                        CurrentWeatherWorker.class,
                        DailyWeatherWorker.class,
                        HourlyWeatherWorker.class);

        initializeTestWorkManage();

        workManager = WorkManager.getInstance(ApplicationProvider.getApplicationContext());
        testDriver =
                WorkManagerTestInitHelper.getTestDriver(
                        ApplicationProvider.getApplicationContext());

        conectivityConstraints =
                new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
    }

    private void initializeTestWorkManage() {
        Configuration configuration =
                new Configuration.Builder()
                        .setWorkerFactory(new TestWorkerFactory(repository, userPreferencesManager))
                        .setMinimumLoggingLevel(Log.DEBUG)
                        .build();

        WorkManagerTestInitHelper.initializeTestWorkManager(
                ApplicationProvider.getApplicationContext(), configuration);
    }

    @Test
    public void testWorkersRunWithNetworkAvailable() {
        workers.forEach(
                worker -> {
                    String uniqueWorkName = "test_with_network_available_" + worker.getSimpleName();
                    PeriodicWorkRequest request =
                            new PeriodicWorkRequest.Builder(
                                            worker, 30, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
                                    .setConstraints(conectivityConstraints)
                                    .build();

                    workManager.enqueueUniquePeriodicWork(
                            uniqueWorkName, ExistingPeriodicWorkPolicy.KEEP, request);

                    testDriver.setPeriodDelayMet(request.getId());
                    testDriver.setAllConstraintsMet(request.getId());

                    if (worker.getSimpleName().equals(CurrentWeatherWorker.class.getSimpleName())) {
                        try {
                            ((TomorrowIoRepository) repository)
                                    .getCachedWeather(WeatherEntity.Type.CURRENT)
                                    .test()
                                    .await()
                                    .assertValue(response -> !response.isEmpty());
                        } catch (InterruptedException e) {
                            fail(
                                    "Error while waiting for "
                                            + worker.getSimpleName()
                                            + " to complete: "
                                            + e.getMessage());
                        }
                    }
                    if (worker.getSimpleName().equals(DailyWeatherWorker.class.getSimpleName())) {
                        try {
                            ((TomorrowIoRepository) repository)
                                    .getCachedWeather(WeatherEntity.Type.DAILY)
                                    .test()
                                    .await()
                                    .assertValue(response -> !response.isEmpty());
                        } catch (InterruptedException e) {
                            fail(
                                    "Error while waiting for "
                                            + worker.getSimpleName()
                                            + " to complete: "
                                            + e.getMessage());
                        }
                    }
                    if (worker.getSimpleName().equals(HourlyWeatherWorker.class.getSimpleName())) {
                        try {
                            ((TomorrowIoRepository) repository)
                                    .getCachedWeather(WeatherEntity.Type.HOURLY)
                                    .test()
                                    .await()
                                    .assertValue(response -> !response.isEmpty());
                        } catch (InterruptedException e) {
                            fail(
                                    "Error while waiting for "
                                            + worker.getSimpleName()
                                            + " to complete: "
                                            + e.getMessage());
                        }
                    }
                });
    }

    @Test
    public void testWorkersRunWithNetworkUnavailable() {
        workers.forEach(
                worker -> {
                    String uniqueWorkName =
                            "test_with_network_unavailable_" + worker.getSimpleName();

                    PeriodicWorkRequest request =
                            new PeriodicWorkRequest.Builder(
                                            worker, 30, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
                                    .setConstraints(conectivityConstraints)
                                    .build();

                    workManager.enqueueUniquePeriodicWork(
                            uniqueWorkName, ExistingPeriodicWorkPolicy.KEEP, request);

                    testDriver.setPeriodDelayMet(request.getId());

                    if (worker.getSimpleName().equals(CurrentWeatherWorker.class.getSimpleName())) {
                        try {
                            ((TomorrowIoRepository) repository)
                                    .getCachedWeather(WeatherEntity.Type.CURRENT)
                                    .test()
                                    .await()
                                    .assertValue(List::isEmpty)
                                    .assertNoErrors()
                                    .assertComplete();
                        } catch (InterruptedException e) {
                            fail(
                                    "Error while waiting for "
                                            + worker.getSimpleName()
                                            + " to complete: "
                                            + e.getMessage());
                        }
                    }
                    if (worker.getSimpleName().equals(DailyWeatherWorker.class.getSimpleName())) {
                        try {
                            ((TomorrowIoRepository) repository)
                                    .getCachedWeather(WeatherEntity.Type.DAILY)
                                    .test()
                                    .await()
                                    .assertValue(List::isEmpty)
                                    .assertNoErrors()
                                    .assertComplete();
                        } catch (InterruptedException e) {
                            fail(
                                    "Error while waiting for "
                                            + worker.getSimpleName()
                                            + " to complete: "
                                            + e.getMessage());
                        }
                    }
                    if (worker.getSimpleName().equals(HourlyWeatherWorker.class.getSimpleName())) {
                        try {
                            ((TomorrowIoRepository) repository)
                                    .getCachedWeather(WeatherEntity.Type.HOURLY)
                                    .test()
                                    .await()
                                    .assertValue(List::isEmpty)
                                    .assertNoErrors()
                                    .assertComplete();
                        } catch (InterruptedException e) {
                            fail(
                                    "Error while waiting for "
                                            + worker.getSimpleName()
                                            + " to complete: "
                                            + e.getMessage());
                        }
                    }
                });
    }
}
