package com.optlab.nimbus.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class WeaklyForecastViewModel extends ViewModel {
    private final WeatherRepository weatherRepository;
    private final PreferencesRepository preferencesRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MutableLiveData<List<ForecastResponse>> weakly = new MutableLiveData<>();
    private final MutableLiveData<TemperatureUnit> temperatureUnit = new MutableLiveData<>();
    private final MutableLiveData<WindSpeedUnit> windSpeedUnit = new MutableLiveData<>();
    private final MutableLiveData<PressureUnit> pressureUnit = new MutableLiveData<>();

    @Inject
    public WeaklyForecastViewModel(
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
        disposable.clear();
        super.onCleared();
    }

    public LiveData<List<ForecastResponse>> getWeakly() {
        return weakly;
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

    public void fetchDaily(@NonNull Coordinates coordinates) {
        disposable.add(
                weatherRepository
                        .getWeaklyForecast(coordinates)
                        .map(ForecastResponse::mapFromForecastEntity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(weakly::setValue, e -> Timber.e("onError: %s", e.getMessage())));
    }
}
