package com.optlab.nimbus.data.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.network.WeatherResponse;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoResponse;
import com.optlab.nimbus.data.preferences.SecurePrefsManager;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Unit tests for TomorrowIoRepository that verify its behavior with different weather types. Uses
 * parameterized tests to run the same test suite against daily, hourly and current weather types.
 */
@RunWith(Parameterized.class)
public class TomorrowIoRepositoryTest {
    @Mock TomorrowIoClient tomorrowIoClient;

    @Mock WeatherDao weatherDao;

    @Mock SecurePrefsManager securePrefsManager;

    TomorrowIoRepository tomorrowIoRepository;
    AutoCloseable autoCloseable;
    Coordinates coordinates = new Coordinates(1.0, 1.0);
    Gson gson = new Gson();

    // Shared test data to reduce duplication
    TomorrowIoResponse mockRemoteResponse;
    List<WeatherResponse> expectedList;

    @Parameterized.Parameter() public WeatherEntity.Type weatherType;

    @Parameterized.Parameter(1)
    public String weatherFields;

    @Parameterized.Parameter(2)
    public String timesteps;

    @Parameterized.Parameter(3)
    public String startTime;

    @Parameterized.Parameter(4)
    public String endTime;

    /**
     * Provides test data for each parameterized test run. Creates configurations for daily, hourly,
     * and current weather types.
     *
     * @return Collection of test parameter arrays containing weather type and API parameters
     */
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return List.of(
                new Object[][] {
                    {
                        WeatherEntity.Type.DAILY,
                        TomorrowIoClient.DAILY_WEATHER_FIELDS,
                        TomorrowIoClient.TIMESTEPS_ONE_DAY,
                        TomorrowIoClient.PLUS_1_DAYS_FROM_TODAY,
                        TomorrowIoClient.PLUS_5_DAYS_FROM_TODAY
                    },
                    {
                        WeatherEntity.Type.HOURLY,
                        TomorrowIoClient.HOURLY_WEATHER_FIELDS,
                        TomorrowIoClient.TIMESTEPS_ONE_HOUR,
                        "start",
                        "end"
                    },
                    {
                        WeatherEntity.Type.CURRENT,
                        TomorrowIoClient.CURRENT_WEATHER_FIELDS,
                        TomorrowIoClient.TIMESTEPS_CURRENT,
                        null,
                        null
                    }
                });
    }

    /**
     * Sets up the test environment before each test method runs. Initializes mocks, repository, and
     * shared test objects.
     */
    @Before
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        // Configure API key mock response
        when(securePrefsManager.getApiKey(SecurePrefsManager.TOMORROW_IO_API_KEY))
                .thenReturn("dummy-key");
        // Create repository with mocked dependencies
        tomorrowIoRepository =
                new TomorrowIoRepository(tomorrowIoClient, securePrefsManager, weatherDao);

        // Initialize reusable test objects
        mockRemoteResponse = mock(TomorrowIoResponse.class);
        expectedList = Collections.nCopies(3, new WeatherResponse());
    }

    /** Clean up resources after test execution. */
    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    /**
     * Helper method to execute a test observer and perform common assertions. Waits for observer
     * completion and verifies that the expected list is received without errors.
     *
     * @param testObserver The test observer to execute and verify
     */
    private void executeAndAssertObserver(TestObserver<List<WeatherResponse>> testObserver) {
        try {
            // Wait for observer to complete
            testObserver.await();
            // Verify results match expected list
            testObserver.assertValue(expectedList);
            // Verify no errors occurred
            testObserver.assertNoErrors();
        } catch (Exception e) {
            fail("Fail in waiting to get data: " + e.getMessage());
        }
    }

    /**
     * Sets up a static mock for TomorrowIoResponse.mapToResponses. Configures the mock to return
     * the expected list when mapToResponses is called.
     *
     * @return A handle to the static mock that must be closed
     */
    private AutoCloseable setupResponseMapping() {
        var mockStatic = mockStatic(TomorrowIoResponse.class);
        // Configure mock to return expected list when mapToResponses is called
        mockStatic
                .when(() -> TomorrowIoResponse.mapToResponses(mockRemoteResponse))
                .thenReturn(expectedList);
        return mockStatic;
    }

    /**
     * Sets up the appropriate mock response for the TomorrowIoClient based on weather type. The
     * method handles different parameter combinations for daily, hourly, and current forecasts.
     *
     * @param returnObservable The Observable to return from the mock client
     * @return A test observer for the repository response
     */
    private TestObserver<List<WeatherResponse>> setupRemoteResponse(
            Observable<TomorrowIoResponse> returnObservable) {
        if (weatherType == WeatherEntity.Type.DAILY) {
            // Configure daily forecast mock with specific date parameters
            when(tomorrowIoClient.getForecast(
                            eq(coordinates),
                            eq(weatherFields),
                            eq(timesteps),
                            eq(startTime),
                            eq(endTime),
                            any(TimeZone.class),
                            anyString()))
                    .thenReturn(returnObservable);
        } else if (weatherType == WeatherEntity.Type.HOURLY) {
            // Configure hourly forecast mock with any date strings
            when(tomorrowIoClient.getForecast(
                            eq(coordinates),
                            eq(weatherFields),
                            eq(timesteps),
                            anyString(),
                            anyString(),
                            any(TimeZone.class),
                            anyString()))
                    .thenReturn(returnObservable);
        } else {
            // Configure current weather mock (no date parameters)
            when(tomorrowIoClient.getForecast(
                            eq(coordinates),
                            eq(weatherFields),
                            eq(timesteps),
                            any(TimeZone.class),
                            anyString()))
                    .thenReturn(returnObservable);
        }

        // Get the appropriate weather observable based on type and test it
        return getWeatherByType().test();
    }

    /**
     * Returns the appropriate Observable from the repository based on weather type.
     *
     * @return Observable for the weather type being tested
     */
    private Observable<List<WeatherResponse>> getWeatherByType() {
        switch (weatherType) {
            case DAILY -> {
                return tomorrowIoRepository.getDailyWeatherByLocation(coordinates);
            }
            case HOURLY -> {
                return tomorrowIoRepository.getHourlyWeatherByLocation(coordinates);
            }
            case CURRENT -> {
                return tomorrowIoRepository.getCurrentWeatherByLocation(coordinates);
            }
            default -> throw new IllegalArgumentException("Unknown weather type");
        }
    }

    /**
     * Tests that the repository returns cached data when valid data exists in the database.
     * Verifies that the remote API is not called when cache is available.
     */
    @Test
    public void testGetWeatherReturnCachedData() {
        // Set up the mock cached entity
        WeatherEntity entity = new WeatherEntity();
        entity.setData(gson.toJson(expectedList));
        entity.setType(weatherType);
        entity.setTimestamp(System.currentTimeMillis());
        // Configure DAO to return cached entity
        when(weatherDao.getLatestWeather(weatherType)).thenReturn(entity);

        // Execute repository method
        TestObserver<List<WeatherResponse>> testObserver = getWeatherByType().test();

        // Execute the observer and assert the results
        executeAndAssertObserver(testObserver);

        // Verify that the local database was queried
        verify(weatherDao, times(1)).getLatestWeather(weatherType);
        // Verify that the remote client was not called
        verifyNoInteractions(tomorrowIoClient);
    }

    /**
     * Tests that the repository fetches data from remote API when no cache exists. Verifies that
     * remote data is properly stored in the database.
     */
    @Test
    public void testGetWeatherReturnRemoteData() throws Exception {
        // Setup repository to return null from database (no cache)
        when(weatherDao.getLatestWeather(weatherType)).thenReturn(null);

        try (var ignored = setupResponseMapping()) {
            // Set up the remote response and execute
            var testObserver = setupRemoteResponse(Observable.just(mockRemoteResponse));
            executeAndAssertObserver(testObserver);

            // Verify database checked for cache
            verify(weatherDao, times(1)).getLatestWeather(weatherType);
            // Verify remote data was stored in database
            verify(weatherDao, times(1)).insertWeather(any(WeatherEntity.class));
        }
    }

    /**
     * Tests that the repository fetches from remote API when cache exists but is expired. Verifies
     * the expiration check and remote data fetching.
     */
    @Test
    public void testGetWeatherReturnRemoteDataWhenExpired() throws Exception {
        // Setup cached but expired data
        WeatherEntity entity = mock(WeatherEntity.class);
        when(weatherDao.getLatestWeather(weatherType)).thenReturn(entity);
        // Configure entity to report as expired
        Mockito.when(entity.isExpired()).thenReturn(true);

        try (var ignored = setupResponseMapping()) {
            // Set up the remote response and execute
            var testObserver = setupRemoteResponse(Observable.just(mockRemoteResponse));
            executeAndAssertObserver(testObserver);

            // Verify cache was checked
            verify(weatherDao, times(1)).getLatestWeather(weatherType);
            // Verify expiration was checked
            verify(entity, times(1)).isExpired();
        }
    }

    /**
     * Tests that the repository properly handles database insertion failures. Verifies the app
     * still returns valid data even when caching fails.
     */
    @Test
    public void testGetWeatherReturnRemoteDataAndCacheToLocalFails() throws Exception {
        // Capture the entity being inserted to verify its properties
        ArgumentCaptor<WeatherEntity> weatherEntityArgumentCaptor =
                ArgumentCaptor.forClass(WeatherEntity.class);
        // Configure database to return error code on insertion
        when(weatherDao.insertWeather(weatherEntityArgumentCaptor.capture())).thenReturn((long) -1);

        try (var ignored = setupResponseMapping()) {
            // Set up the remote response and execute
            var testObserver = setupRemoteResponse(Observable.just(mockRemoteResponse));
            executeAndAssertObserver(testObserver);

            // Get captured entity and verify insertion attempt
            WeatherEntity entity = weatherEntityArgumentCaptor.getValue();
            verify(weatherDao, times(1)).insertWeather(entity);
            // Verify error code was returned
            assertEquals(-1, weatherDao.insertWeather(entity));
        }
    }

    /**
     * Tests that the repository properly handles remote API errors. Verifies the fallback to empty
     * list when API fails.
     */
    @Test
    public void testRemoteDataReturnNullThroughFallback() throws Exception {
        try (var ignored = setupResponseMapping()) {
            // Set up error response from remote API
            var testObserver =
                    setupRemoteResponse(Observable.error(new RuntimeException("Mock API error")));

            // Wait for observer to complete
            testObserver.await();
            // Verify no errors are propagated (error is handled internally)
            testObserver.assertNoErrors();
            // Verify fallback to empty list
            testObserver.assertValue(Collections.emptyList());
        }
    }
}
