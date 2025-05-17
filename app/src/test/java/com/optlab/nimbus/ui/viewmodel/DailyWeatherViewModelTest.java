package com.optlab.nimbus.ui.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.repository.WeatherRepository;

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

public class DailyWeatherViewModelTest {
    /** Rule that allows LiveData to be observed synchronously in tests. */
    @Rule public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock WeatherRepository repository;
    @Mock Observer<List<WeatherResponse>> dailyObserver;

    @InjectMocks DailyWeatherViewModel viewModel;

    AutoCloseable closeable;

    Coordinates coordinates = new Coordinates("1", "1");
    List<WeatherResponse> dailyResponse = List.of(new WeatherResponse(), new WeatherResponse());

    @Before
    public void setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                schedulerCallable -> Schedulers.trampoline());
        closeable = MockitoAnnotations.openMocks(this);
        // viewModel = new DailyWeatherViewModel(repository);
        viewModel.getDaily().observeForever(dailyObserver);
    }

    @After
    public void tearDown() throws Exception {
        viewModel.getDaily().removeObserver(dailyObserver);
        RxAndroidPlugins.reset();
        closeable.close();
    }

    @Test
    public void getDaily_returns_initial_LiveData() {
        assertNotNull(viewModel.getDaily());
    }

    @Test
    public void fetchDailyWeatherByLocation_successful_fetch_updates_LiveData() {
        when(repository.getDailyWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.just(dailyResponse));
        viewModel.fetchDailyWeatherByLocation(coordinates);
        ArgumentCaptor<List<WeatherResponse>> captor = ArgumentCaptor.forClass(List.class);
        verify(dailyObserver).onChanged(captor.capture());
        assertEquals(dailyResponse, captor.getValue());
    }

    @Test
    public void fetchDailyWeatherByLocation_successful_fetch_with_empty_list() {
        when(repository.getDailyWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.just(Collections.emptyList()));
        viewModel.fetchDailyWeatherByLocation(coordinates);

        verify(dailyObserver, never()).onChanged(any());
        assertNull(viewModel.getDaily().getValue());
    }

    @Test
    public void fetchDailyWeatherByLocation_failure_sets_LiveData_to_null() {
        when(repository.getDailyWeatherByLocation(any(Coordinates.class)))
                .thenReturn(Observable.error(new Exception("error")));
        viewModel.fetchDailyWeatherByLocation(coordinates);

        verify(dailyObserver, never()).onChanged(any());
        assertNull(viewModel.getDaily().getValue());
    }

    @Test
    public void fetchDailyWeatherByLocation_adds_disposable_to_CompositeDisposable() {
        when(repository.getDailyWeatherByLocation(coordinates))
                .thenReturn(Observable.just(Collections.emptyList()));
        int before = getCompositeDisposableSize(viewModel);
        viewModel.fetchDailyWeatherByLocation(coordinates);
        int after = getCompositeDisposableSize(viewModel);
        assertTrue(after > before);
    }

    @Test
    public void onCleared_clears_disposables() {
        when(repository.getDailyWeatherByLocation(coordinates))
                .thenReturn(Observable.just(Collections.emptyList()));
        viewModel.fetchDailyWeatherByLocation(coordinates);
        viewModel.onCleared();
        assertEquals(0, getCompositeDisposableSize(viewModel));
    }

    @Test(expected = NullPointerException.class)
    public void fetchDailyWeatherByLocation_with_null_Coordinates_argument() {
        viewModel.fetchDailyWeatherByLocation(null);
    }

    // Helper to access private CompositeDisposable size via reflection
    private int getCompositeDisposableSize(DailyWeatherViewModel vm) {
        try {
            java.lang.reflect.Field field =
                    DailyWeatherViewModel.class.getDeclaredField("disposable");
            field.setAccessible(true);
            CompositeDisposable d = (CompositeDisposable) field.get(vm);
            return d.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
