package com.optlab.nimbus.worker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.testing.TestDriver;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.optlab.nimbus.data.FakeTomorrowIoRepository;
import com.optlab.nimbus.data.FakeUserPreferenceManager;
import com.optlab.nimbus.data.preferences.UserPreferences;
import com.optlab.nimbus.data.repository.WeatherRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

// | Scenario                                      | Description                                                                 | Expected Result                                 |
// |-----------------------------------------------|-----------------------------------------------------------------------------|-------------------------------------------------|
// | Worker runs with network available            | Device is connected to the internet                                         | Worker executes and fetches weather data        |
// | Worker runs with no network                   | Device is offline                                                           | Worker does not execute (respects constraint)   |
// | Worker runs at scheduled interval             | Worker is triggered at the defined periodic interval                        | Worker executes at correct times                |
// | Worker is enqueued when already scheduled     | Enqueue same unique work again                                              | Existing work is kept (no duplicate execution)  |
// | Worker fails and retries                      | Worker throws an exception during execution                                 | Worker retries as per backoff policy            |
// | Worker completes successfully                 | Worker completes its task without errors                                    | Worker result is SUCCESS                        |
// | Worker receives injected dependencies         | Worker is created via custom WorkerFactory                                  | Dependencies are injected and accessible        |
// | Worker is cancelled                           | Work is cancelled before execution                                          | Worker does not run                             |
// | Worker persists across app restarts           | App is killed and restarted                                                 | Worker continues as scheduled                   |
// | Worker handles input/output data              | Worker receives input and sets output data                                  | Data is processed and output is set correctly   |

@SuppressWarnings("BusyWait")
public class WorkManagerTest {
    private List<Class<? extends Worker>> workers;

    /** FakeTomorrowIoRepository is a mock implementation of the WeatherRepository interface. */
    WeatherRepository repository = new FakeTomorrowIoRepository();

    /** FakeUserPreferenceManager is a mock implementation of the UserPreferences interface. */
    UserPreferences userPrefs = new FakeUserPreferenceManager();

    /** WorkManager is the main entry point for managing work requests. It is used to enqueue, */
    private WorkManager workManager;

    /** TestDriver is used to control the execution of the work such as setting constraints, */
    private TestDriver testDriver;

    @Before
    public void setUp() {

        // Set up the configuration for WorkManager with a custom WorkerFactory.
        Configuration configuration =
                new Configuration.Builder()
                        .setWorkerFactory(new TestWorkerFactory(repository, userPrefs))
                        .setMinimumLoggingLevel(android.util.Log.DEBUG)
                        .build();

        // Initialize WorkManager with the test configuration.
        WorkManagerTestInitHelper.initializeTestWorkManager(
                ApplicationProvider.getApplicationContext(), configuration);

        // Initialize the list of workers to be tested.
        workers =
                List.of(
                        CurrentWeatherWorker.class,
                        HourlyWeatherWorker.class,
                        DailyWeatherWorker.class);

        // Get the WorkManager for test initialization.
        workManager = WorkManager.getInstance(ApplicationProvider.getApplicationContext());

        // Get the TestDriver to control the execution of the work such as setting
        // constraints, checking the state, etc.
        testDriver =
                WorkManagerTestInitHelper.getTestDriver(
                        ApplicationProvider.getApplicationContext());
    }

    /**
     * Test that the WorkManager is initialized correctly and that the workers are not null. This
     * test checks if the WorkManager is set up properly and that the workers can be instantiated.
     */
    @Test
    public void testWorkerRunsWithNetworkAvailable() {
        // Constraint to require network connectivity.
        Constraints networkConstraints =
                new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        workers.forEach(
                worker -> {
                    // Build one time work request with the specified constraints.
                    OneTimeWorkRequest request =
                            new OneTimeWorkRequest.Builder(worker)
                                    .setConstraints(networkConstraints)
                                    .build();

                    try {
                        // Enqueue the work request and wait for it to finish.
                        workManager.enqueue(request).getResult().get();

                        // Stimulate all constraints (in this case, network connectivity) to be met.
                        testDriver.setAllConstraintsMet(request.getId());

                        // Get the WorkInfo for the work request and check its state to retrieve
                        // the result. We have to wait for the work to finish because it runs
                        // asynchronously.
                        WorkInfo workInfo;
                        do {
                            Thread.sleep(1000); // Wait for the work to finish.
                            workInfo = workManager.getWorkInfoById(request.getId()).get();
                        } while (workInfo.getState() == WorkInfo.State.RUNNING);

                        // Check if the work is finished and succeeded.
                        assertEquals(WorkInfo.State.SUCCEEDED, workInfo.getState());
                    } catch (ExecutionException | InterruptedException e) {
                        fail(
                                "Exception in worker "
                                        + worker.getSimpleName()
                                        + ": "
                                        + e.getMessage());
                    }
                });
    }

    /**
     * Test that the WorkManager is initialized correctly and that the workers are not null. This
     * test checks if the WorkManager is set up properly and that the workers can be instantiated.
     */
    @Test
    public void testWorkersRunWithNetworkUnavailable() {
        // Constraint to require network connectivity.
        Constraints networkConstraints =
                new Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build();
        workers.forEach(
                worker -> {
                    // Build one time work request with the specified constraints.
                    OneTimeWorkRequest request =
                            new OneTimeWorkRequest.Builder(worker)
                                    .setConstraints(networkConstraints)
                                    .build();

                    try {
                        // Enforce the repository to fail.
                        ((FakeTomorrowIoRepository) repository).setShouldFail(true);

                        // Enqueue the work request and wait for it to finish.
                        workManager.enqueue(request).getResult().get();

                        // Stimulate all constraints (in this case, network connectivity) to be met.
                        testDriver.setAllConstraintsMet(request.getId());

                        // Get the WorkInfo for the work request and check its state to retrieve
                        // the result. We have to wait for the work to finish because it runs
                        // asynchronously.
                        WorkInfo workInfo;
                        do {
                            Thread.sleep(1000); // Wait for the work to finish.
                            workInfo = workManager.getWorkInfoById(request.getId()).get();
                        } while (workInfo.getState() == WorkInfo.State.RUNNING);

                        // Check if the work is finished and failed.
                        assertEquals(WorkInfo.State.FAILED, workInfo.getState());
                    } catch (ExecutionException | InterruptedException e) {
                        fail(
                                "Exception in worker "
                                        + worker.getSimpleName()
                                        + ": "
                                        + e.getMessage());
                    }
                });
    }
}
