package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.constant.ResponseConstant;
import com.optlab.nimbus.constant.TagConstant;
import com.optlab.nimbus.constant.TomorrowIoConstant;
import com.optlab.nimbus.data.common.NetworkBoundResource;
import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.local.dao.WeatherDao;
import com.optlab.nimbus.data.local.entity.Converters;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.model.mapper.TomorrowIoMapper;
import com.optlab.nimbus.data.model.mapper.WeatherResponseMapper;
import com.optlab.nimbus.data.network.tomorrowio.TomorrowIoClient;
import com.optlab.nimbus.data.preferences.SecurePrefsManager;
import com.optlab.nimbus.utility.DateTimeUtil;

import io.reactivex.rxjava3.core.Flowable;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import java.lang.reflect.Type;
import java.util.List;

/** TomorrowIoRepository is responsible for fetching weather data from the Tomorrow.io API. */
public class TomorrowIoRepository implements WeatherRepository {
    private final TomorrowIoClient tomorrowIoClient;
    private final String tomorrowIoKey;
    private final WeatherDao weatherDao;

    public TomorrowIoRepository(
            @NonNull TomorrowIoClient tomorrowIoClient,
            @NonNull SecurePrefsManager securePrefsManager,
            @NonNull WeatherDao weatherDao) {
        this.tomorrowIoClient = tomorrowIoClient;
        this.weatherDao = weatherDao;
        this.tomorrowIoKey = securePrefsManager.getApiKey(SecurePrefsManager.TOMORROW_IO_API_KEY);
    }

    @Override
    public Flowable<WeatherEntity> getDailyWeather(@NonNull Coordinates coordinates) {
        return new NetworkBoundResource<WeatherEntity, WeatherEntity>() {
            @Override
            protected Flowable<WeatherEntity> getFromLocal() {
                return weatherDao.getLatestWeather(
                        WeatherEntity.Type.DAILY,
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
                        .map(responses -> WeatherResponseMapper.map(responses, coordinates))
                        .onErrorResumeNext(TomorrowIoRepository.this::onErrorFetchWeather);
            }

            @Override
            protected void cacheFetchResult(WeatherEntity item) {
                weatherDao
                        .insertWeather(item)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(
                                () -> {
                                    Timber.tag(TagConstant.DATABASE).d("Weather data cached");

                                    weatherDao.deleteExpiry(
                                            WeatherEntity.Type.DAILY,
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
    public Flowable<WeatherEntity> getCurrentWeather(@NonNull Coordinates coordinates) {
        return new NetworkBoundResource<WeatherEntity, WeatherEntity>() {
            @Override
            protected Flowable<WeatherEntity> getFromLocal() {
                return weatherDao.getLatestWeather(
                        WeatherEntity.Type.CURRENT,
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
                                TomorrowIoConstant.CURRENT_WEATHER_FIELDS,
                                TomorrowIoConstant.TIMESTEPS_ONE_HOUR,
                                TomorrowIoConstant.METRIC,
                                DateTimeUtil.getTimeZoneId(),
                                tomorrowIoKey)
                        .map(TomorrowIoMapper::map)
                        .map(responses -> WeatherResponseMapper.map(responses, coordinates))
                        .onErrorResumeNext(TomorrowIoRepository.this::onErrorFetchWeather);
            }

            @Override
            protected void cacheFetchResult(WeatherEntity item) {
                weatherDao
                        .insertWeather(item)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(
                                () -> {
                                    Timber.tag(TagConstant.DATABASE).d("Weather data cached");

                                    weatherDao.deleteExpiry(
                                            WeatherEntity.Type.CURRENT,
                                            WeatherProvider.TOMORROW_IO,
                                            Converters.fromCoordinates(coordinates),
                                            System.currentTimeMillis()
                                                    + ResponseConstant.CURRENT_EXPIRY_TIME);
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
    public Flowable<WeatherEntity> getHourlyWeather(@NonNull Coordinates coordinates) {
        return new NetworkBoundResource<WeatherEntity, WeatherEntity>() {
            @Override
            protected Flowable<WeatherEntity> getFromLocal() {
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
                        .map(responses -> WeatherResponseMapper.map(responses, coordinates))
                        .onErrorResumeNext(TomorrowIoRepository.this::onErrorFetchWeather);
            }

            @Override
            protected void cacheFetchResult(WeatherEntity item) {
                weatherDao
                        .insertWeather(item)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .doOnComplete(
                                () -> {
                                    Timber.tag(TagConstant.DATABASE).d("Weather data cached");

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
