package com.optlab.nimbus.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.optlab.nimbus.data.local.entity.ForecastEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.common.PressureUnit;
import com.optlab.nimbus.data.common.TemperatureUnit;
import com.optlab.nimbus.data.model.forecast.ForecastResponse;
import com.optlab.nimbus.data.common.WindSpeedUnit;
import com.optlab.nimbus.data.repository.PreferencesRepository;
import com.optlab.nimbus.data.repository.WeatherRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class CurrentForecastViewModel extends ViewModel {
    private final WeatherRepository weatherRepository;
    private final PreferencesRepository preferencesRepository;
    private final Gson gson = new Gson();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<List<ForecastResponse>> current = new MutableLiveData<>();
    private final MutableLiveData<List<ForecastResponse>> hourly = new MutableLiveData<>();
    private final MutableLiveData<TemperatureUnit> temperatureUnit = new MutableLiveData<>();
    private final MutableLiveData<WindSpeedUnit> windSpeedUnit = new MutableLiveData<>();
    private final MutableLiveData<PressureUnit> pressureUnit = new MutableLiveData<>();

    @Inject
    public CurrentForecastViewModel(
            @NonNull WeatherRepository weatherRepository,
            @NonNull PreferencesRepository preferencesRepository) {
        this.weatherRepository = weatherRepository;
        this.preferencesRepository = preferencesRepository;

        temperatureUnit.setValue(preferencesRepository.getTemperatureUnit().blockingFirst());
        windSpeedUnit.setValue(preferencesRepository.getWindSpeedUnit().blockingFirst());
        pressureUnit.setValue(preferencesRepository.getPressureUnit().blockingFirst());
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }

    public LiveData<List<ForecastResponse>> getCurrent() {
        return current;
    }

    public LiveData<List<ForecastResponse>> getHourly() {
        return hourly;
    }

    public LiveData<TemperatureUnit> getTemperatureUnit() {
        return temperatureUnit;
    }

    public LiveData<WindSpeedUnit> getWindSpeedUnit() {
        return windSpeedUnit;
    }

    public LiveData<PressureUnit> getPressureUnit() {
        return pressureUnit;
    }

    public void fetchForecast(@NonNull Coordinates coordinates) {
        fetchWeather(weatherRepository.getCurrentForecast(coordinates), current, "Current");
        fetchWeather(weatherRepository.getHourlyForecast(coordinates), hourly, "Hourly");
    }

    private void fetchWeather(
            Flowable<ForecastEntity> flowable,
            MutableLiveData<List<ForecastResponse>> liveData,
            String tag) {
        disposables.add(
                flowable.map(ForecastResponse::mapFromForecastEntity)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                liveData::setValue,
                                e -> Timber.e("%s weather fetch failed: %s", tag, e.getMessage()),
                                () -> Timber.d("%s weather fetch completed", tag)));
    }
}
