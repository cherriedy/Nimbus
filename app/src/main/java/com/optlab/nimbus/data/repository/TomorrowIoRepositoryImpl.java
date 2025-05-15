package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.optlab.nimbus.constant.ResponseConstant;
import com.optlab.nimbus.constant.TagConstant;
import com.optlab.nimbus.constant.TomorrowIoConstant;
import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.network.NetworkBoundResource;
import com.optlab.nimbus.data.local.dao.ForecastDao;
import com.optlab.nimbus.data.local.entity.ForecastEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.forecast.ForecastResponse;
import com.optlab.nimbus.data.model.tomorrowio.TomorrowIoResponse;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.preferences.ForecastApiPreferences;
import com.optlab.nimbus.utility.DateTimeUtil;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.schedulers.Schedulers;

import timber.log.Timber;

/** TomorrowIoRepository is responsible for fetching weather data from the Tomorrow.io API. */
public class TomorrowIoRepositoryImpl implements WeatherRepository {
    private final TomorrowIoClient tomorrowIoClient;
    private final String tomorrowIoKey;
    private final ForecastDao forecastDao;

    public TomorrowIoRepositoryImpl(
            @NonNull TomorrowIoClient tomorrowIoClient,
            @NonNull ForecastApiPreferences forecastApiPreferences,
            @NonNull ForecastDao forecastDao) {
        this.tomorrowIoClient = tomorrowIoClient;
        this.forecastDao = forecastDao;
        this.tomorrowIoKey = forecastApiPreferences.getApiKey(ForecastProvider.TOMORROW_IO.name());
    }

    @Override
    public Flowable<ForecastEntity> getWeaklyForecast(@NonNull Coordinates coordinates) {
        return new NetworkBoundResource<ForecastEntity, ForecastEntity>() {
            @Override
            protected Maybe<ForecastEntity> getFromLocal() {
                return forecastDao.getForecast(
                        ForecastEntity.Type.WEAKLY,
                        ForecastProvider.TOMORROW_IO,
                        coordinates.getLocationParameter());
            }

            @Override
            protected boolean shouldFetch(ForecastEntity data) {
                return data == null || data.isExpired();
            }

            @Override
            protected Single<ForecastEntity> fetchFromRemote() {
                return tomorrowIoClient
                        .getTomorrowIoService()
                        .getWeatherByLocationCode(
                                coordinates.getLocationParameter(),
                                TomorrowIoConstant.DAILY_WEATHER_FIELDS,
                                TomorrowIoConstant.TIMESTEPS_ONE_DAY,
                                TomorrowIoConstant.METRIC,
                                TomorrowIoConstant.PLUS_1_DAYS_FROM_TODAY,
                                TomorrowIoConstant.PLUS_5_DAYS_FROM_TODAY,
                                DateTimeUtil.getTimeZoneId(),
                                tomorrowIoKey)
                        .map(TomorrowIoResponse::mapToWeatherResponse)
                        .map(
                                responses ->
                                        ForecastResponse.mapToForecastEntity(
                                                responses, coordinates, ForecastEntity.Type.WEAKLY))
                        .onErrorResumeNext(TomorrowIoRepositoryImpl.this::onErrorFetchWeather);
            }

            @Override
            protected void cacheFetchResult(ForecastEntity item) {
                forecastDao
                        .insert(item)
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(
                                () -> {
                                    Timber.tag(TagConstant.DATABASE)
                                            .d("Inserted weather data successfully");
                                    forecastDao.deleteExpiry(
                                            ForecastEntity.Type.WEAKLY,
                                            ForecastProvider.TOMORROW_IO,
                                            coordinates.getLocationParameter(),
                                            System.currentTimeMillis()
                                                    + ResponseConstant.DAILY_EXPIRY_TIME);
                                })
                        .doOnError(
                                e -> {
                                    Timber.tag(TagConstant.DATABASE)
                                            .e("Error caching weather data: %s", e.getMessage());
                                })
                        .subscribe();
            }
        }.asFlowable();
    }

    @Override
    public Flowable<ForecastEntity> getCurrentForecast(@NonNull Coordinates coordinates) {
        return new NetworkBoundResource<ForecastEntity, ForecastEntity>() {
            @Override
            protected Maybe<ForecastEntity> getFromLocal() {
                Timber.d("Fetching current weather from local database");
                return forecastDao.getForecast(
                        ForecastEntity.Type.CURRENT,
                        ForecastProvider.TOMORROW_IO,
                        coordinates.getLocationParameter());
            }

            @Override
            protected boolean shouldFetch(ForecastEntity data) {
                Timber.d("Checking if current weather should be fetched");
                return data == null || data.isExpired();
            }

            @Override
            protected Single<ForecastEntity> fetchFromRemote() {
                return tomorrowIoClient
                        .getTomorrowIoService()
                        .getWeatherByLocationCode(
                                coordinates.getLocationParameter(),
                                TomorrowIoConstant.CURRENT_WEATHER_FIELDS,
                                TomorrowIoConstant.TIMESTEPS_ONE_HOUR,
                                TomorrowIoConstant.METRIC,
                                DateTimeUtil.getTimeZoneId(),
                                tomorrowIoKey)
                        .map(TomorrowIoResponse::mapToWeatherResponse)
                        .map(
                                responses ->
                                        ForecastResponse.mapToForecastEntity(
                                                responses, coordinates, ForecastEntity.Type.CURRENT))
                        .onErrorResumeNext(TomorrowIoRepositoryImpl.this::onErrorFetchWeather);
            }

            @Override
            protected void cacheFetchResult(ForecastEntity item) {
                forecastDao
                        .insert(item)
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(
                                () -> {
                                    Timber.tag(TagConstant.DATABASE)
                                            .d("Inserted current weather data successfully");
                                    // weatherDao
                                    //         .deleteExpiry(
                                    //                 WeatherEntity.Type.CURRENT,
                                    //                 WeatherProvider.TOMORROW_IO,
                                    //                 Converters.fromCoordinates(coordinates),
                                    //                 System.currentTimeMillis()
                                    //                         +
                                    // ResponseConstant.CURRENT_EXPIRY_TIME)
                                    //         .doOnComplete(
                                    //                 () -> {
                                    //                     Timber.tag(TagConstant.DATABASE)
                                    //                             .d(
                                    //                                     "Deleted expired current
                                    // weather data");
                                    //                 })
                                    //         .doOnError(
                                    //                 e -> {
                                    //                     Timber.tag(TagConstant.DATABASE)
                                    //                             .e(
                                    //                                     "Error deleting expired
                                    // current weather data: %s",
                                    //                                     e.getMessage());
                                    //                 })
                                    //         .subscribe();
                                })
                        .doOnError(
                                e -> {
                                    Timber.tag(TagConstant.DATABASE)
                                            .e("Error caching weather data: %s", e.getMessage());
                                })
                        .subscribe();
            }
        }.asFlowable();
    }

    @Override
    public Flowable<ForecastEntity> getHourlyForecast(@NonNull Coordinates coordinates) {
        return new NetworkBoundResource<ForecastEntity, ForecastEntity>() {
            @Override
            protected Maybe<ForecastEntity> getFromLocal() {
                return forecastDao.getForecast(
                        ForecastEntity.Type.HOURLY,
                        ForecastProvider.TOMORROW_IO,
                        coordinates.getLocationParameter());
            }

            @Override
            protected boolean shouldFetch(ForecastEntity data) {
                return data == null || data.isExpired();
            }

            @Override
            protected Single<ForecastEntity> fetchFromRemote() {
                return tomorrowIoClient
                        .getTomorrowIoService()
                        .getWeatherByLocationCode(
                                coordinates.getLocationParameter(),
                                TomorrowIoConstant.HOURLY_WEATHER_FIELDS,
                                TomorrowIoConstant.TIMESTEPS_ONE_HOUR,
                                TomorrowIoConstant.METRIC,
                                DateTimeUtil.getAnHourLater(),
                                DateTimeUtil.getAnHourLaterTomorrow(),
                                DateTimeUtil.getTimeZoneId(),
                                tomorrowIoKey)
                        .map(TomorrowIoResponse::mapToWeatherResponse)
                        .map(
                                responses ->
                                        ForecastResponse.mapToForecastEntity(
                                                responses, coordinates, ForecastEntity.Type.HOURLY))
                        .onErrorResumeNext(TomorrowIoRepositoryImpl.this::onErrorFetchWeather);
            }

            @Override
            protected void cacheFetchResult(ForecastEntity item) {
                forecastDao
                        .insert(item)
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(
                                () -> {
                                    Timber.tag(TagConstant.DATABASE)
                                            .d("Inserted hourly weather data successfully");
                                    forecastDao.deleteExpiry(
                                            ForecastEntity.Type.HOURLY,
                                            ForecastProvider.TOMORROW_IO,
                                            coordinates.getLocationParameter(),
                                            System.currentTimeMillis()
                                                    + ResponseConstant.DAILY_EXPIRY_TIME);
                                })
                        .doOnError(
                                e -> {
                                    Timber.tag(TagConstant.DATABASE)
                                            .e("Error caching weather data: %s", e.getMessage());
                                })
                        .subscribe();
            }
        }.asFlowable();
    }

    private SingleSource<? extends ForecastEntity> onErrorFetchWeather(Throwable throwable) {
        Timber.tag(TagConstant.NETWORK)
                .e("Error fetching weather data: %s", throwable.getMessage());
        return Single.just(new ForecastEntity());
    }
}
