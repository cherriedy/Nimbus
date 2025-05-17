package com.optlab.nimbus.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.Forecast;
import com.optlab.nimbus.data.model.Pressure;
import com.optlab.nimbus.data.model.Temperature;
import com.optlab.nimbus.data.model.WindSpeed;
import com.optlab.nimbus.data.repository.interfaces.ForecastRepository;
import com.optlab.nimbus.data.repository.interfaces.PreferencesRepository;

import dagger.hilt.android.lifecycle.HiltViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import timber.log.Timber;

import java.util.List;

import javax.inject.Inject;

@HiltViewModel
public class WeaklyForecastViewModel extends ViewModel {
    private final ForecastRepository forecastRepository;
    private final PreferencesRepository preferencesRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MutableLiveData<List<Forecast>> weakly = new MutableLiveData<>();
    private final MutableLiveData<Temperature.Unit> temperatureUnit = new MutableLiveData<>();
    private final MutableLiveData<WindSpeed.Unit> windSpeedUnit = new MutableLiveData<>();
    private final MutableLiveData<Pressure.Unit> pressureUnit = new MutableLiveData<>();

    @Inject
    public WeaklyForecastViewModel(
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
        disposable.clear();
        super.onCleared();
    }

    public LiveData<List<Forecast>> getWeakly() {
        return weakly;
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

    public void fetchDaily(@NonNull Coordinates coordinates) {
        // disposable.add(
        //         forecastRepository
        //                 .getForecast(coordinates)
        //                 .map(Forecast::mapFromForecastEntity)
        //                 .subscribeOn(Schedulers.io())
        //                 .observeOn(AndroidSchedulers.mainThread())
        //                 .subscribe(weakly::setValue, e -> Timber.e("onError: %s",
        // e.getMessage())));
    }
}
