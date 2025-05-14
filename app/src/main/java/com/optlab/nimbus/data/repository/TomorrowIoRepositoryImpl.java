package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.optlab.nimbus.constant.ResponseConstant;
import com.optlab.nimbus.constant.TagConstant;
import com.optlab.nimbus.constant.TomorrowIoConstant;
import com.optlab.nimbus.data.common.NetworkBoundResource;
import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.entity.Converters;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.mapper.TomorrowIoMapper;
import com.optlab.nimbus.data.model.mapper.WeatherMapper;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.preferences.WeatherApiPreferences;
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
    private final WeatherDao weatherDao;

    public TomorrowIoRepositoryImpl(
            @NonNull TomorrowIoClient tomorrowIoClient,
            @NonNull WeatherApiPreferences weatherApiPreferences,
            @NonNull WeatherDao weatherDao) {
        this.tomorrowIoClient = tomorrowIoClient;
        this.weatherDao = weatherDao;
        this.tomorrowIoKey = weatherApiPreferences.getApiKey(WeatherProvider.TOMORROW_IO.name());
    }

    @Override
    public Flowable<WeatherEntity> getWeaklyForecast(@NonNull Coordinates coordinates) {
        return new NetworkBoundResource<WeatherEntity, WeatherEntity>() {
            @Override
            protected Maybe<WeatherEntity> getFromLocal() {
                return weatherDao.getLatestWeather(
                        WeatherEntity.Type.WEAKLY,
                        WeatherProvider.TOMORROW_IO,
                        Converters.fromCoordinates(coordinates));
            }

            @Override
            protected boolean shouldFetch(WeatherEntity data) {
                return data == null || data.isExpired();
            }

            @Override
            protected Single<WeatherEntity> fetchFromRemote() {
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
                        .map(TomorrowIoMapper::map)
                        .map(
                                responses ->
                                        WeatherMapper.mapToWeatherEntity(
                                                responses, coordinates, WeatherEntity.Type.WEAKLY))
                        .onErrorResumeNext(TomorrowIoRepositoryImpl.this::onErrorFetchWeather);
            }

            @Override
            protected void cacheFetchResult(WeatherEntity item) {
                weatherDao
                        .insertWeather(item)
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(
                                () -> {
                                    Timber.tag(TagConstant.DATABASE)
                                            .d("Inserted weather data successfully");
                                    weatherDao.deleteExpiry(
                                            WeatherEntity.Type.WEAKLY,
                                            WeatherProvider.TOMORROW_IO,
                                            Converters.fromCoordinates(coordinates),
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
    public Flowable<WeatherEntity> getCurrentForecast(@NonNull Coordinates coordinates) {
        return new NetworkBoundResource<WeatherEntity, WeatherEntity>() {
            @Override
            protected Maybe<WeatherEntity> getFromLocal() {
                Timber.d("Fetching current weather from local database");
                return weatherDao.getLatestWeather(
                        WeatherEntity.Type.CURRENT,
                        WeatherProvider.TOMORROW_IO,
                        Converters.fromCoordinates(coordinates));
            }

            @Override
            protected boolean shouldFetch(WeatherEntity data) {
                Timber.d("Checking if current weather should be fetched");
                return data == null || data.isExpired();
            }

            @Override
            protected Single<WeatherEntity> fetchFromRemote() {
                return tomorrowIoClient
                        .getTomorrowIoService()
                        .getWeatherByLocationCode(
                                coordinates.getLocationParameter(),
                                TomorrowIoConstant.CURRENT_WEATHER_FIELDS,
                                TomorrowIoConstant.TIMESTEPS_ONE_HOUR,
                                TomorrowIoConstant.METRIC,
                                DateTimeUtil.getTimeZoneId(),
                                tomorrowIoKey)
                        .map(TomorrowIoMapper::map)
                        .map(
                                responses ->
                                        WeatherMapper.mapToWeatherEntity(
                                                responses, coordinates, WeatherEntity.Type.CURRENT))
                        .onErrorResumeNext(TomorrowIoRepositoryImpl.this::onErrorFetchWeather);
            }

            @Override
            protected void cacheFetchResult(WeatherEntity item) {
                weatherDao
                        .insertWeather(item)
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
    public Flowable<WeatherEntity> getHourlyForecast(@NonNull Coordinates coordinates) {
        return new NetworkBoundResource<WeatherEntity, WeatherEntity>() {
            @Override
            protected Maybe<WeatherEntity> getFromLocal() {
                return weatherDao.getLatestWeather(
                        WeatherEntity.Type.HOURLY,
                        WeatherProvider.TOMORROW_IO,
                        Converters.fromCoordinates(coordinates));
            }

            @Override
            protected boolean shouldFetch(WeatherEntity data) {
                return data == null || data.isExpired();
            }

            @Override
            protected Single<WeatherEntity> fetchFromRemote() {
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
                        .map(TomorrowIoMapper::map)
                        .map(
                                responses ->
                                        WeatherMapper.mapToWeatherEntity(
                                                responses, coordinates, WeatherEntity.Type.HOURLY))
                        .onErrorResumeNext(TomorrowIoRepositoryImpl.this::onErrorFetchWeather);
            }

            @Override
            protected void cacheFetchResult(WeatherEntity item) {
                weatherDao
                        .insertWeather(item)
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(
                                () -> {
                                    Timber.tag(TagConstant.DATABASE)
                                            .d("Inserted hourly weather data successfully");
                                    weatherDao.deleteExpiry(
                                            WeatherEntity.Type.HOURLY,
                                            WeatherProvider.TOMORROW_IO,
                                            Converters.fromCoordinates(coordinates),
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

    private SingleSource<? extends WeatherEntity> onErrorFetchWeather(Throwable throwable) {
        Timber.tag(TagConstant.NETWORK)
                .e("Error fetching weather data: %s", throwable.getMessage());
        return Single.just(new WeatherEntity());
    }
}
