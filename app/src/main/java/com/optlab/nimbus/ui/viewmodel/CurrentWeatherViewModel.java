package com.optlab.nimbus.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.repository.WeatherRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;
import timber.log.Timber;

@HiltViewModel
public class CurrentWeatherViewModel extends ViewModel {
    private final WeatherRepository repository;
    private final Gson gson = new Gson();

    @Getter
    private LiveData<List<WeatherResponse>> current = new MutableLiveData<>(new ArrayList<>());

    @Getter
    private LiveData<List<WeatherResponse>> hourly = new MutableLiveData<>(new ArrayList<>());

    @Inject
    public CurrentWeatherViewModel(@NonNull WeatherRepository repository) {
        this.repository = repository;
    }

    public void fetchCurrent(@NonNull Coordinates coordinates) {
        current =
                LiveDataReactiveStreams.fromPublisher(
                        repository
                                .getCurrentWeather(coordinates)
                                .subscribeOn(Schedulers.io())
                                .map(this::parseWeatherEntity)
                                .doOnComplete(
                                        () -> {
                                            Timber.d("Current weather fetch completed");
                                        })
                                .doOnError(
                                        throwable -> {
                                            Timber.e(
                                                    "Current weather fetch failed: %s",
                                                    throwable.getMessage());
                                        }));
    }

    public void fetchHourly(@NonNull Coordinates coordinates) {
        hourly =
                LiveDataReactiveStreams.fromPublisher(
                        repository
                                .getHourlyWeather(coordinates)
                                .subscribeOn(Schedulers.io())
                                .map(this::parseWeatherEntity)
                                .doOnComplete(
                                        () -> {
                                            Timber.d("Hourly weather fetch completed");
                                        })
                                .doOnError(
                                        throwable -> {
                                            Timber.e(
                                                    "Hourly weather fetch failed: %s",
                                                    throwable.getMessage());
                                        }));
    }

    private List<WeatherResponse> parseWeatherEntity(WeatherEntity entity) {
        if (entity == null || entity.getData() == null) {
            Timber.e("WeatherEntity or its data is null");
            return Collections.emptyList();
        }
        try {
            Type listType = new TypeToken<List<WeatherResponse>>() {}.getType();
            return gson.fromJson(entity.getData(), listType);
        } catch (Exception e) {
            Timber.e("Failed to parse WeatherEntity data: %s", e.getMessage());
            return Collections.emptyList();
        }
    }
}
