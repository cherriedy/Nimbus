package com.optlab.nimbus.worker;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
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
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

// | Scenario                                      | Description                                                                 | Expected Result                                 |
// |-----------------------------------------------|-----------------------------------------------------------------------------|-------------------------------------------------|
// | Worker runs with network available            | Device is connected to the internet                                         | Worker executes and fetches weather data        |
// | Worker runs with no network                   | Device is offline                                                           | Worker does not execute (respects constraint)   |
// | Worker runs at scheduled interval             | Worker is triggered at the defined periodic interval                        | Worker executes at correct times                |
// | Worker is enqueued when already scheduled     | Enqueue same unique work again                                              | Existing work is kept (no duplicate execution)  |
// | Worker fails and retries                      | Worker throws an exception during execution                                 | Worker retries as per backoff policy            |
// | Worker is cancelled                           | Work is cancelled before execution                                          | Worker does not run                             |
// | Worker persists across app restarts           | App is killed and restarted                                                 | Worker continues as scheduled                   |

@RunWith(AndroidJUnit4.class)
public class WorkManagerWithFakeRepositoryTest {
    private List<Class<? extends Worker>> workers;

    /** FakeTomorrowIoRepository is a mock implementation of the WeatherRepository interface. */
    WeatherRepository repository = new FakeTomorrowIoRepository();

    /** FakeUserPreferenceManager is a mock implementation of the UserPreferences interface. */
    UserPreferences userPrefs = new FakeUserPreferenceManager();

    /** WorkManager is the main entry point for managing work requests. It is used to enqueue, */
    private WorkManager workManager;

    /** TestDriver is used to control the execution of the work such as setting constraints, */
    private TestDriver testDriver;

    /** Network constraints are used to specify the conditions under which the work should run. */
    private Constraints connectedNetworkConstraints;

    @Before
    public void setUp() {

        // Initialize the WorkManager for testing with a custom WorkerFactory.
        initializeTestWorkManager();

        // Initialize the list of workers to be tested.
        workers =
                List.of(
                        CurrentWeatherWorker.class,
                        HourlyWeatherWorker.class,
                        DailyWeatherWorker.class);

        // Set up constraint for network connectivity.
        connectedNetworkConstraints =
                new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        // Get the WorkManager for test initialization.
        workManager = WorkManager.getInstance(ApplicationProvider.getApplicationContext());

        // Get the TestDriver to control the execution of the work such as setting
        // constraints, checking the state, etc.
        testDriver =
                WorkManagerTestInitHelper.getTestDriver(
                        ApplicationProvider.getApplicationContext());
    }

    /**
     * Initialize the WorkManager for testing with a custom WorkerFactory. This method sets up the
     * WorkManager with a custom configuration that includes a WorkerFactory for creating workers.
     */
    private void initializeTestWorkManager() {
        // Set up the configuration for WorkManager with a custom WorkerFactory.
        Configuration configuration =
                new Configuration.Builder()
                        .setWorkerFactory(new TestWorkerFactory(repository, userPrefs))
                        .setMinimumLoggingLevel(android.util.Log.DEBUG)
                        .build();

        // Initialize WorkManager with the test configuration.
        WorkManagerTestInitHelper.initializeTestWorkManager(
                ApplicationProvider.getApplicationContext(), configuration);
    }

    /**
     * Test that workers run with network available. This test checks if the workers are able to
     * execute successfully when the network is available. It uses the TestDriver to simulate the
     * availability of network
     */
    @Test
    public void testWorkerRunsWithNetworkAvailable() {
        workers.forEach(
                worker -> {
                    String uniqueWorkName =
                            "test_worker_run_with_network_available_" + worker.getSimpleName();

                    // Build one time work request with the specified constraints.
                    PeriodicWorkRequest request =
                            new PeriodicWorkRequest.Builder(worker, 15, TimeUnit.MINUTES)
                                    .setConstraints(connectedNetworkConstraints)
                                    .build();

                    // Enqueue the work request and wait for it to finish.
                    workManager.enqueueUniquePeriodicWork(
                            uniqueWorkName, ExistingPeriodicWorkPolicy.KEEP, request);

                    ((FakeTomorrowIoRepository) repository).setShouldFail(false);

                    // Simulate the periodic delay to trigger the work.
                    testDriver.setPeriodDelayMet(request.getId());
                    // Simulate all constraints (in this case, network connectivity) to be met.
                    testDriver.setAllConstraintsMet(request.getId());

                    // Check if the responses are not null.
                    await().atMost(5, TimeUnit.SECONDS)
                            .untilAsserted(
                                    () ->
                                            assertNotNull(
                                                    ((FakeTomorrowIoRepository) repository)
                                                            .getMockResponses()));

                    try {
                        List<WorkInfo> workInfos =
                                workManager.getWorkInfosForUniqueWork(uniqueWorkName).get();
                        assertEquals(1, workInfos.size());
                        assertEquals(WorkInfo.State.ENQUEUED, workInfos.getFirst().getState());
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
        workers.forEach(
                worker -> {
                    String uniqueWorkName =
                            "test_worker_run_with_network_unavailable_" + worker.getSimpleName();

                    // Build one time work request with the specified constraints.
                    PeriodicWorkRequest request =
                            new PeriodicWorkRequest.Builder(worker, 15, TimeUnit.MINUTES)
                                    .setConstraints(connectedNetworkConstraints)
                                    .build();

                    try {
                        // Enforce the repository to fail.
                        ((FakeTomorrowIoRepository) repository).setShouldFail(true);

                        workManager.enqueueUniquePeriodicWork(
                                uniqueWorkName, ExistingPeriodicWorkPolicy.KEEP, request);

                        // Simulate the initial delay to trigger the work.
                        testDriver.setInitialDelayMet(request.getId());

                        List<WorkInfo> workInfos =
                                workManager.getWorkInfosForUniqueWork(uniqueWorkName).get();

                        // Check if the work is in the ENQUEUED state.
                        assertEquals(WorkInfo.State.ENQUEUED, workInfos.getFirst().getState());

                        // Check if the mock responses are null.
                        assertNull(((FakeTomorrowIoRepository) repository).getMockResponses());
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
     * Test that the workers run at scheduled intervals. This test checks if the workers are
     * triggered at the defined periodic interval and that they execute correctly. It uses the
     * TestDriver to simulate the passage of time and trigger the work.
     */
    @Test
    public void testWorkerRunsAtScheduledInterval() {
        workers.forEach(
                worker -> {
                    String uniqueWorkName =
                            "test_worker_run_at_scheduled_interval_" + worker.getSimpleName();

                    // Build one time work request with the specified constraints.
                    PeriodicWorkRequest request =
                            new PeriodicWorkRequest.Builder(worker, 15, TimeUnit.MINUTES)
                                    .setConstraints(connectedNetworkConstraints)
                                    .build();

                    ((FakeTomorrowIoRepository) repository).setShouldFail(false);

                    // Enqueue the work request and wait for it to finish.
                    workManager.enqueueUniquePeriodicWork(
                            uniqueWorkName, ExistingPeriodicWorkPolicy.KEEP, request);

                    // Simulate the periodic delay to trigger the work.
                    testDriver.setPeriodDelayMet(request.getId());
                    // Simulate meeting all constraints (in this case, network connectivity).
                    testDriver.setAllConstraintsMet(request.getId());

                    // Check if the responses are not null. This will wait for a maximum of 3
                    // seconds for the condition to be met.
                    await().atMost(5, TimeUnit.SECONDS)
                            .untilAsserted(
                                    () ->
                                            assertNotNull(
                                                    ((FakeTomorrowIoRepository) repository)
                                                            .getMockResponses()));


                    try {
                        List<WorkInfo> workInfos =
                                workManager.getWorkInfosForUniqueWork(uniqueWorkName).get();
                        assertEquals(1, workInfos.size());
                        assertEquals(WorkInfo.State.ENQUEUED, workInfos.getFirst().getState());
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
     * Test that the workers are enqueued when already scheduled. This test checks if the
     * enqueueUniquePeriodicWork method works correctly when the same unique work is enqueued again.
     * It uses the ExistingPeriodicWorkPolicy.KEEP policy to ensure that the existing work is kept
     * and not replaced.
     */
    @Test
    public void testWorkerIsEnqueueWhenAlreadyScheduled() {
        workers.forEach(
                worker -> {
                    // Unique name for the worker to ensure that it is not replaced.
                    String workerUniqueName = "test_keep_worker" + worker.getSimpleName();

                    // The first request is enqueued and should be kept.
                    PeriodicWorkRequest firstRequest =
                            new PeriodicWorkRequest.Builder(worker, 15, TimeUnit.MINUTES).build();

                    // Enqueue the first request with the KEEP policy.
                    workManager.enqueueUniquePeriodicWork(
                            workerUniqueName, ExistingPeriodicWorkPolicy.KEEP, firstRequest);

                    // The second request is enqueued with the same unique name and should not
                    // replace the first one.
                    PeriodicWorkRequest secondRequest =
                            new PeriodicWorkRequest.Builder(worker, 15, TimeUnit.MINUTES).build();

                    // Enqueue the second request with the KEEP policy.
                    workManager
                            .enqueueUniquePeriodicWork(
                                    workerUniqueName,
                                    ExistingPeriodicWorkPolicy.KEEP,
                                    secondRequest);

                    // Check if the first request is still present in the WorkManager.
                    List<WorkInfo> workInfos;
                    try {
                        workInfos = workManager.getWorkInfosForUniqueWork(workerUniqueName).get();
                        assertEquals(1, workInfos.size()); // Only one work should be present.
                        // Check if the first request is still present.
                        assertEquals(firstRequest.getId(), workInfos.getFirst().getId());
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
     * Test that the workers are cancelled before execution. This test checks if the work is
     * cancelled correctly before it is executed. It uses the cancelWorkById method to cancel the
     * work and then checks the state of the work.
     */
    @Test
    public void testWorkerIsCancelledBeforeExecution() {
        workers.forEach(
                worker -> {
                    String uniqueWorkName =
                            "test_worker_cancelled_before_execution_" + worker.getSimpleName();

                    PeriodicWorkRequest request =
                            new PeriodicWorkRequest.Builder(worker, 15, TimeUnit.MINUTES)
                                    .setConstraints(connectedNetworkConstraints)
                                    .build();

                    workManager.enqueueUniquePeriodicWork(
                            uniqueWorkName, ExistingPeriodicWorkPolicy.KEEP, request);

                    // Simulate the work to be cancelled before execution.
                    workManager.cancelWorkById(request.getId());
                    // Simulate the periodic delay to trigger the work.
                    testDriver.setPeriodDelayMet(request.getId());
                    // Simulate all constraints (in this case, network connectivity) to be met.
                    testDriver.setAllConstraintsMet(request.getId());

                    try {
                        List<WorkInfo> workInfos =
                                workManager.getWorkInfosForUniqueWork(uniqueWorkName).get();
                        assertEquals(1, workInfos.size());
                        assertEquals(WorkInfo.State.CANCELLED, workInfos.getFirst().getState());
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
     * Test that the workers persist across app restarts. This test checks if the workers are
     * correctly persisted and can continue to run even after the app is restarted. It uses the
     * TestDriver to simulate the app restart and checks if the work is still present in the
     * WorkManager.
     */
    @Test
    public void testWorkerPersistsAcrossAppRestarts() {
        workers.forEach(
                worker -> {
                    String uniqueWorkName =
                            "test_worker_persists_across_app_restarts_" + worker.getSimpleName();

                    PeriodicWorkRequest request =
                            new PeriodicWorkRequest.Builder(worker, 15, TimeUnit.MINUTES)
                                    .setConstraints(connectedNetworkConstraints)
                                    .build();

                    workManager.enqueueUniquePeriodicWork(
                            uniqueWorkName, ExistingPeriodicWorkPolicy.KEEP, request);

                    // Simulate the app restart by reinitializing the WorkManager.
                    initializeTestWorkManager();

                    // Simulate the periodic delay to trigger the work.
                    testDriver.setPeriodDelayMet(request.getId());
                    // Simulate all constraints (in this case, network connectivity) to be met.
                    testDriver.setAllConstraintsMet(request.getId());

                    await().atMost(5, TimeUnit.SECONDS)
                            .untilAsserted(
                                    () ->
                                            assertNotNull(
                                                    ((FakeTomorrowIoRepository) repository)
                                                            .getMockResponses()));

                    try {
                        List<WorkInfo> workInfos =
                                workManager.getWorkInfosForUniqueWork(uniqueWorkName).get();
                        assertEquals(1, workInfos.size());
                        assertEquals(WorkInfo.State.ENQUEUED, workInfos.getFirst().getState());
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