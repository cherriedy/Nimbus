package com.optlab.nimbus.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.data.local.entity.ForecastEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.Forecast;
import com.optlab.nimbus.data.model.Pressure;
import com.optlab.nimbus.data.model.Temperature;
import com.optlab.nimbus.data.model.WindSpeed;
import com.optlab.nimbus.data.repository.interfaces.ForecastRepository;
import com.optlab.nimbus.data.repository.interfaces.PreferencesRepository;

import java.lang.reflect.Type;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class CurrentForecastViewModel extends ViewModel {
    private final ForecastRepository forecastRepository;
    private final PreferencesRepository preferencesRepository;
    private final Gson gson = new Gson();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Forecast.Current> current = new MutableLiveData<>();
    private final MutableLiveData<Forecast.Today> today = new MutableLiveData<>();
    private final MutableLiveData<Temperature.Unit> temperatureUnit = new MutableLiveData<>();
    private final MutableLiveData<WindSpeed.Unit> windSpeedUnit = new MutableLiveData<>();
    private final MutableLiveData<Pressure.Unit> pressureUnit = new MutableLiveData<>();

    @Inject
    public CurrentForecastViewModel(
            @NonNull ForecastRepository forecastRepository,
            @NonNull PreferencesRepository preferencesRepository) {
        this.forecastRepository = forecastRepository;
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

    public LiveData<Forecast.Current> getCurrent() {
        return current;
    }

    public LiveData<Forecast.Today> getToday() {
        return today;
    }

    public LiveData<Temperature.Unit> getTemperatureUnit() {
        return temperatureUnit;
    }

    public LiveData<WindSpeed.Unit> getWindSpeedUnit() {
        return windSpeedUnit;
    }

    public LiveData<Pressure.Unit> getPressureUnit() {
        return pressureUnit;
    }

    public void fetchForecast(@NonNull Coordinates coordinates) {
        disposables.add(
                forecastRepository
                        .getForecast(coordinates)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::mapToUI, e -> Timber.e(e, "Error fetching forecast")));
    }

    private void mapToUI(ForecastEntity entity) {
        Forecast forecast =
                new Gson().fromJson(entity.getData(), new TypeToken<Forecast>() {}.getType());
        current.setValue(forecast.getCurrent());
        today.setValue(forecast.getToday());
    }
}
