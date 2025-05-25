package com.optlab.nimbus.data.repository;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.network.WeatherResponse;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoResponse;
import com.optlab.nimbus.data.preferences.SecurePrefsManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;

public class TomorrowIoRepositoryTest {
    @Mock TomorrowIoClient tomorrowIoClient;

    @Mock WeatherDao weatherDao;

    @Mock SecurePrefsManager securePrefsManager;

    TomorrowIoRepository tomorrowIoRepository;

    AutoCloseable autoCloseable;

    Coordinates coordinates = new Coordinates(1.0, 1.0);

    Gson gson = new Gson();
    Type listType = new TypeToken<List<WeatherResponse>>() {}.getType();

    @Before
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        when(securePrefsManager.getApiKey(SecurePrefsManager.TOMORROW_IO_API_KEY))
                .thenReturn("dummy-key");
        tomorrowIoRepository =
                new TomorrowIoRepository(tomorrowIoClient, securePrefsManager, weatherDao);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testGetDailyWeatherReturnCachedData() {
        List<WeatherResponse> cachedList = Collections.singletonList(new WeatherResponse());
        String cachedJson = gson.toJson(cachedList);

        WeatherEntity entity = new WeatherEntity();
        entity.setData(cachedJson);
        entity.setType(WeatherEntity.Type.DAILY);
        entity.setTimestamp(System.currentTimeMillis());
        when(weatherDao.getLatestWeather(WeatherEntity.Type.DAILY)).thenReturn(entity);

        TestObserver<List<WeatherResponse>> testObserver =
                tomorrowIoRepository.getDailyWeatherByLocation(coordinates).test();

        testObserver.assertValue(cachedList);
        testObserver.assertNoErrors();
        verify(weatherDao, times(1)).getLatestWeather(WeatherEntity.Type.DAILY);
        verifyNoInteractions(tomorrowIoClient); // Verify that we do not need to fetch from remote
    }

    @Test
    public void testGetDailyWeatherReturnRemoteData() {
        when(weatherDao.getLatestWeather(WeatherEntity.Type.DAILY)).thenReturn(null);

        TomorrowIoResponse mockRemoteResponse = mock(TomorrowIoResponse.class);

        List<WeatherResponse> expectedList = Collections.singletonList(new WeatherResponse());

        try (var mockedStatic = mockStatic(TomorrowIoResponse.class)) {
            mockedStatic
                    .when(() -> TomorrowIoResponse.mapToResponses(mockRemoteResponse))
                    .thenReturn(expectedList);
            when(tomorrowIoClient.getForecast(
                            eq(coordinates),
                            eq(TomorrowIoClient.DAILY_WEATHER_FIELDS),
                            eq(TomorrowIoClient.TIMESTEPS_ONE_DAY),
                            eq(TomorrowIoClient.PLUS_1_DAYS_FROM_TODAY),
                            eq(TomorrowIoClient.PLUS_5_DAYS_FROM_TODAY),
                            any(TimeZone.class),
                            anyString()))
                    .thenReturn(Observable.just(mockRemoteResponse));

            TestObserver<List<WeatherResponse>> testObserver =
                    tomorrowIoRepository.getDailyWeatherByLocation(coordinates).test();

            testObserver.await();
            testObserver.assertValue(expectedList);
            testObserver.assertNoErrors();
            verify(weatherDao, times(1)).getLatestWeather(WeatherEntity.Type.DAILY);
            verify(tomorrowIoClient, times(1))
                    .getForecast(
                            eq(coordinates),
                            eq(TomorrowIoClient.DAILY_WEATHER_FIELDS),
                            eq(TomorrowIoClient.TIMESTEPS_ONE_DAY),
                            eq(TomorrowIoClient.PLUS_1_DAYS_FROM_TODAY),
                            eq(TomorrowIoClient.PLUS_5_DAYS_FROM_TODAY),
                            any(TimeZone.class),
                            anyString());
            verify(weatherDao, atLeastOnce()).insertWeather(any(WeatherEntity.class));
        } catch (InterruptedException e) {
            fail("Fail in waiting to get data from weather dao");
        }
    }

    @Test
    public void testGetHourlyWeatherReturnCachedData() {
        List<WeatherResponse> mockResponse = Collections.nCopies(3, new WeatherResponse());
        String jsonResponse = gson.toJson(mockResponse);

        WeatherEntity entity = new WeatherEntity();
        entity.setData(jsonResponse);
        entity.setType(WeatherEntity.Type.HOURLY);
        entity.setTimestamp(System.currentTimeMillis());

        when(weatherDao.getLatestWeather(WeatherEntity.Type.HOURLY)).thenReturn(entity);

        TestObserver<List<WeatherResponse>> testObserver =
                tomorrowIoRepository.getHourlyWeatherByLocation(coordinates).test();

        try {
            testObserver.await();
            testObserver.assertValue(mockResponse);
            testObserver.assertNoErrors();

            verifyNoInteractions(tomorrowIoClient);
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void testGetHourlyWeatherReturnRemoteData() {
        when(weatherDao.getLatestWeather(WeatherEntity.Type.HOURLY)).thenReturn(null);

        TomorrowIoResponse mockRemoteResponse = mock(TomorrowIoResponse.class);

        List<WeatherResponse> expectedList = Collections.nCopies(3, new WeatherResponse());

        try (MockedStatic<TomorrowIoResponse> mockTomorrowIoResponse =
                mockStatic(TomorrowIoResponse.class)) {
            mockTomorrowIoResponse
                    .when(() -> TomorrowIoResponse.mapToResponses(mockRemoteResponse))
                    .thenReturn(expectedList);

            when(tomorrowIoClient.getForecast(
                            eq(coordinates),
                            eq(TomorrowIoClient.HOURLY_WEATHER_FIELDS),
                            eq(TomorrowIoClient.TIMESTEPS_ONE_DAY),
                            anyString(),
                            anyString(),
                            any(TimeZone.class),
                            anyString()))
                    .thenReturn(Observable.just(mockRemoteResponse));

            TestObserver<List<WeatherResponse>> testObserver =
                    tomorrowIoRepository.getHourlyWeatherByLocation(coordinates).test();
            testObserver.await();
            testObserver.assertValue(expectedList);
            testObserver.assertNoErrors();

            verify(weatherDao, times(1)).getLatestWeather(WeatherEntity.Type.HOURLY);
            // verify(tomorrowIoClient, times(1))
            //         .getForecast(
            //                 coordinates,
            //                 TomorrowIoClient.HOURLY_WEATHER_FIELDS,
            //                 TomorrowIoClient.TIMESTEPS_ONE_DAY,
            //                 anyString(),
            //                 anyString(),
            //                 any(TimeZone.class),
            //                 anyString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
