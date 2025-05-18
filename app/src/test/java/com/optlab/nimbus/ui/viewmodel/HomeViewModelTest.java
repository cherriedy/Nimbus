package com.optlab.nimbus.ui.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.network.WeatherResponse;
import com.optlab.nimbus.data.repository.WeatherRepository;

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class HomeViewModelTest {
    /** InstantTaskExecutorRule is used to execute tasks synchronously in the main thread. */
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Use Mockito to create a mock instance of WeatherRepository. This allows us to simulate the
     * behavior of the repository without needing a real implementation.
     */
    @Mock WeatherRepository repository;

    @Mock Observer<List<WeatherResponse>> currentObserver;
    @Mock Observer<List<WeatherResponse>> hourlyObserver;

    /**
     * HomeViewModel is the class under test. It is a ViewModel that fetches weather data from a
     * repository and exposes it to the UI. It uses RxJava to handle asynchronous operations.
     */
    @InjectMocks private HomeViewModel viewModel;

    /**
     * AutoCloseable is used to close the Mockito annotations after each test. This is important to
     * avoid memory leaks and ensure that the mock objects are properly cleaned up.
     */
    private AutoCloseable closeable;

    private Coordinates coordinates;
    private List<WeatherResponse> currentResponses;
    private List<WeatherResponse> hourlyResponses;

    @Before
    public void setUp() {
        // Set the RxJava scheduler to use the trampoline scheduler for testing, which executes
        // tasks immediately on the current thread. This handles AndroidSchedulers.mainThread()
        // and Schedulers.io(), allowing us to test the ViewModel without needing a real Android
        // environment.
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());

        // Method initializes the mock objects and injects them into the ViewModel.
        closeable = MockitoAnnotations.openMocks(this);

        // Use @InjectMocks instead.
        // Create an instance of the ViewModel and pass the mock repository to it.
        // viewModel = new HomeViewModel(repository);

        // Set up observers to observe the LiveData in the ViewModel. This allows us to verify
        // that the correct data is set to the observers when the ViewModel fetches weather
        // data.
        viewModel.getCurrent().observeForever(currentObserver);
        viewModel.getHourly().observeForever(hourlyObserver);

        // Test data setup: use new objects for each test
        coordinates = new Coordinates(1.0, 2.0);
        currentResponses = List.of(new WeatherResponse(), new WeatherResponse());
        hourlyResponses = List.of(new WeatherResponse());
    }

    @After
    public void tearDown() throws Exception {
        // Remove observers to avoid cross-test interference
        viewModel.getCurrent().removeObserver(currentObserver);
        viewModel.getHourly().removeObserver(hourlyObserver);

        // Reset the RxJava scheduler to its default behavior after each test. This is important to
        // avoid side effects in other tests that may use RxJava.
        RxAndroidPlugins.reset();

        // Close the AutoCloseable instance to clean up the mock objects and avoid memory leaks.
        closeable.close();
    }

    @Test
    public void getCurrent_returns_initial_LiveData() {
        assertNotNull(viewModel.getCurrent());
    }

    @Test
    public void getHourly_returns_initial_LiveData() {
        assertNotNull(viewModel.getHourly());
    }

    /**
     * Test method to verify the behavior of the ViewModel when fetching current weather data
     * successfully. It simulates a successful response from the repository and verifies that the
     * correct data is set to the observer.
     *
     * <p>Scenario:
     *
     * <ul>
     *   <li>Mock the repository to return a list of WeatherResponse objects when
     *       getCurrentWeatherByLocation is called.
     *   <li>Call the fetchCurrentWeatherByLocation method in the ViewModel with a sample
     *       Coordinates object.
     *   <li>Verify that the observer receives the correct list of WeatherResponse objects.
     *   <li>Assert that the value set to the LiveData in the ViewModel matches the expected list
     * </ul>
     */
    @Test
    public void fetchCurrentWeatherByLocation_success_setsValueToLiveData() {
        when(repository.getCurrentWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.just(currentResponses));
        viewModel.fetchCurrentWeatherByLocation(coordinates);
        ArgumentCaptor<List<WeatherResponse>> captor = ArgumentCaptor.forClass(List.class);
        verify(currentObserver).onChanged(captor.capture());
        assertEquals(currentResponses, captor.getValue());
    }

    /**
     * Test method to verify the behavior of the ViewModel when fetching current weather data
     * unsuccessfully. It simulates an error response from the repository and verifies that the
     * observer does not receive any data.
     *
     * <p>Scenario:
     *
     * <ul>
     *   <li>Mock the repository to return an error when getCurrentWeatherByLocation is called.
     *   <li>Call the fetchCurrentWeatherByLocation method in the ViewModel with a sample
     *       Coordinates object.
     *   <li>Verify that the onChanged() method was not called
     *   <li>Assert that the value set to the LiveData in the ViewModel is null.
     */
    @Test
    public void fetchCurrentWeatherByLocation_error_doesNotSetToLiveData() {
        when(repository.getCurrentWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.error(new Exception("error")));
        viewModel.fetchCurrentWeatherByLocation(coordinates);
        verify(currentObserver, never()).onChanged(any());
        assertNull(viewModel.getCurrent().getValue());
    }

    /**
     * Test method to verify the behavior of the ViewModel when fetching current weather data
     * returns an empty list. It simulates a response from the repository with an empty list and
     * verifies that the observer does not receive any data.
     *
     * <p>Scenario:
     *
     * <ul>
     *   <li>Mock the repository to return an empty list when getCurrentWeatherByLocation is called.
     *   <li>Call the fetchCurrentWeatherByLocation method in the ViewModel with a sample
     *       Coordinates object.
     *   <li>Verify that the onChanged() method was not called
     *   <li>Assert that the value set to the LiveData in the ViewModel is null.
     */
    @Test
    public void fetchCurrentWeatherByLocation_emptyList_doesNotSetToLiveData() {
        when(repository.getCurrentWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.just(Arrays.asList()));
        viewModel.fetchCurrentWeatherByLocation(coordinates);
        verify(currentObserver, never()).onChanged(any());
        assertNull(viewModel.getCurrent().getValue());
    }

    /**
     * Test method to verify the behavior of the ViewModel when fetching hourly weather data
     * successfully. It simulates a successful response from the repository and verifies that the
     * correct data is set to the observer.
     *
     * <p>Scenario:
     *
     * <ul>
     *   <li>Mock the repository to return a list of WeatherResponse objects when
     *       getHourlyWeatherByLocation is called.
     *   <li>Call the fetchHourlyWeathersByLocation method in the ViewModel with a sample
     *       Coordinates object.
     *   <li>Verify that the observer receives the correct list of WeatherResponse objects.
     *   <li>Assert that the value set to the LiveData in the ViewModel matches the expected list
     * </ul>
     */
    @Test
    public void fetchHourlyWeathersByLocation_success_setsValueToLiveData() {
        when(repository.getHourlyWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.just(hourlyResponses));
        viewModel.fetchHourlyWeathersByLocation(coordinates);
        ArgumentCaptor<List<WeatherResponse>> captor = ArgumentCaptor.forClass(List.class);
        verify(hourlyObserver).onChanged(captor.capture());
        assertEquals(hourlyResponses, captor.getValue());
        assertEquals(hourlyResponses, viewModel.getHourly().getValue());
    }

    /**
     * Test method to verify the behavior of the ViewModel when fetching hourly weather data
     * unsuccessfully. It simulates an error response from the repository and verifies that the
     * observer does not receive any data.
     *
     * <p>Scenario:
     *
     * <ul>
     *   <li>Mock the repository to return an error when getHourlyWeatherByLocation is called.
     *   <li>Call the fetchHourlyWeathersByLocation method in the ViewModel with a sample
     *       Coordinates object.
     *   <li>Verify that the onChanged() method was not called
     *   <li>Assert that the value set to the LiveData in the ViewModel is null.
     */
    @Test
    public void fetchHourlyWeathersByLocation_error_doesNotSetToLiveData() {
        when(repository.getHourlyWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.error(new Exception("error")));
        viewModel.fetchHourlyWeathersByLocation(coordinates);
        verify(hourlyObserver, never()).onChanged(any());
        assertNull(viewModel.getHourly().getValue());
    }

    /**
     * Test method to verify the behavior of the ViewModel when fetching hourly weather data returns
     * an empty list. It simulates a response from the repository with an empty list and verifies
     * that the observer does not receive any data.
     *
     * <p>Scenario:
     *
     * <ul>
     *   <li>Mock the repository to return an empty list when getHourlyWeatherByLocation is called.
     *   <li>Call the fetchHourlyWeathersByLocation method in the ViewModel with a sample
     *       Coordinates object.
     *   <li>Verify that the onChanged() method was not called
     *   <li>Assert that the value set to the LiveData in the ViewModel is null.
     */
    @Test
    public void fetchHourlyWeathersByLocation_emptyList_doesNotSetToLiveData() {
        when(repository.getHourlyWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.just(Arrays.asList()));
        viewModel.fetchHourlyWeathersByLocation(coordinates);
        verify(hourlyObserver, never()).onChanged(any());
        assertNull(viewModel.getHourly().getValue());
    }

    @Test
    public void fetchCurrentWeatherByLocation_adds_disposable_to_CompositeDisposable() {
        when(repository.getCurrentWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.just(currentResponses));
        int before = getCompositeDisposableSize(viewModel);
        viewModel.fetchCurrentWeatherByLocation(coordinates);
        int after = getCompositeDisposableSize(viewModel);
        assertTrue(after > before);
    }

    @Test
    public void fetchHourlyWeathersByLocation_adds_disposable_to_CompositeDisposable() {
        when(repository.getHourlyWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.just(hourlyResponses));
        int before = getCompositeDisposableSize(viewModel);
        viewModel.fetchHourlyWeathersByLocation(coordinates);
        int after = getCompositeDisposableSize(viewModel);
        assertTrue(after > before);
    }

    @Test
    public void onCleared_clears_disposables() {
        when(repository.getCurrentWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.just(currentResponses));
        viewModel.fetchCurrentWeatherByLocation(coordinates);
        viewModel.onCleared();
        assertEquals(0, getCompositeDisposableSize(viewModel));
    }

    @Test(expected = NullPointerException.class)
    public void fetchCurrentWeatherByLocation_with_null_Coordinates_argument() {
        viewModel.fetchCurrentWeatherByLocation(null);
    }

    @Test(expected = NullPointerException.class)
    public void fetchHourlyWeathersByLocation_with_null_Coordinates_argument() {
        viewModel.fetchHourlyWeathersByLocation(null);
    }

    // Helper to access private CompositeDisposable size via reflection
    private int getCompositeDisposableSize(HomeViewModel vm) {
        try {
            java.lang.reflect.Field field = HomeViewModel.class.getDeclaredField("disposable");
            field.setAccessible(true);
            CompositeDisposable d = (CompositeDisposable) field.get(vm);
            return d.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
