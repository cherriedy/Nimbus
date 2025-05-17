package com.optlab.nimbus.data.repository;

import com.google.gson.Gson;
import com.optlab.nimbus.data.local.dao.ForecastDao;
import com.optlab.nimbus.data.local.entity.ForecastEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.Forecast;
import com.optlab.nimbus.data.model.ForecastProvider;
import com.optlab.nimbus.data.network.NetworkBoundResource;
import com.optlab.nimbus.data.network.ResponseConstant;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoResponse;
import com.optlab.nimbus.data.preferences.interfaces.ForecastApiPreferences;
import com.optlab.nimbus.data.repository.interfaces.ForecastRepository;
import com.optlab.nimbus.utility.DateTimeUtil;

import java.util.TimeZone;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.schedulers.Schedulers;

import timber.log.Timber;

/** TomorrowIoRepository is responsible for fetching weather data from the Tomorrow.io API. */
public class TomorrowIoRepositoryImpl implements ForecastRepository {
    private final TomorrowIoClient tomorrowIoClient;
    private final String tomorrowIoKey;
    private final ForecastDao forecastDao;

    public TomorrowIoRepositoryImpl(
            TomorrowIoClient tomorrowIoClient,
            ForecastApiPreferences forecastApiPreferences,
            ForecastDao forecastDao) {
        this.tomorrowIoClient = tomorrowIoClient;
        this.forecastDao = forecastDao;
        this.tomorrowIoKey = forecastApiPreferences.getApiKey(ForecastProvider.TOMORROW_IO.name());
    }

    @Override
    public Flowable<ForecastEntity> getForecast(Coordinates coordinates) {
        return new NetworkBoundResource<ForecastEntity, Forecast>() {
            @Override
            protected Maybe<ForecastEntity> fromLocal() {
                return forecastDao.getForecast(
                        ForecastProvider.TOMORROW_IO, coordinates.toString());
            }

            @Override
            protected boolean shouldFetch(ForecastEntity data) {
                return true;
            }

            @Override
            protected Single<Forecast> fromRemote() {
                TimeZone timeZone = TimeZone.getDefault();
                return tomorrowIoClient
                        .getForecast(coordinates, timeZone, tomorrowIoKey)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .map(TomorrowIoResponse::mapToReports)
                        .map(reports -> new Forecast(reports, timeZone));
            }

            @Override
            protected void cacheRefreshing(Forecast item) {
                String jsonForecast = new Gson().toJson(item);
                forecastDao.insert(
                        ForecastEntity.builder()
                                .coordinates(coordinates)
                                .timestamp(System.currentTimeMillis())
                                .provider(ForecastProvider.TOMORROW_IO)
                                .data(jsonForecast)
                                .build());
            }
        }.asFlowable();
    }
}
