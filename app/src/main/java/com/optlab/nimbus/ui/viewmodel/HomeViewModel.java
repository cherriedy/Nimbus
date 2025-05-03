package com.optlab.nimbus.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.nimbus.data.model.common.UnifiedWeatherResponse;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.repository.WeatherRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private final WeatherRepository repository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MutableLiveData<List<UnifiedWeatherResponse>> current = new MutableLiveData<>();
    private final MutableLiveData<List<UnifiedWeatherResponse>> hourly = new MutableLiveData<>();

    @Inject
    public HomeViewModel(@NonNull WeatherRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }

    public void fetchCurrentWeatherByLocation(@NonNull Coordinates coordinates) {
        disposable.add(
                repository
                        .getCurrentWeatherByLocation(coordinates)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onCurrentWeatherFetchSuccessful,
                                this::onCurrentWeatherFetchFail));
    }

    private void onCurrentWeatherFetchFail(Throwable throwable) {
        Timber.e("onError: %s", throwable.getMessage());
    }

    private void onCurrentWeatherFetchSuccessful(
            List<UnifiedWeatherResponse> unifiedWeatherResponses) {
        if (unifiedWeatherResponses != null && !unifiedWeatherResponses.isEmpty()) {
            current.postValue(unifiedWeatherResponses);
            Timber.d("onSuccess: %s", unifiedWeatherResponses.size());
            unifiedWeatherResponses.forEach(
                    weather -> Timber.d("onSuccess: %s", weather.toString()));
        } else {
            Timber.e("onSuccess: No data received");
        }
    }

    public void fetchHourlyWeathersByLocation(@NonNull Coordinates coordinates) {
        disposable.add(
                repository
                        .getHourlyWeatherByLocation(coordinates)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onHourlyWeatherFetchSuccessful,
                                this::onHourlyWeathersFetchFail));
    }

    private void onHourlyWeatherFetchSuccessful(
            List<UnifiedWeatherResponse> unifiedWeatherResponses) {
        if (unifiedWeatherResponses != null && !unifiedWeatherResponses.isEmpty()) {
            hourly.postValue(unifiedWeatherResponses);
            Timber.d("onSuccess: %s", unifiedWeatherResponses.size());
        } else {
            Timber.e("onSuccess: No data received");
        }
    }

    private void onHourlyWeathersFetchFail(Throwable throwable) {
        Timber.e("onError: %s", throwable.getMessage());
    }

    public LiveData<List<UnifiedWeatherResponse>> getHourly() {
        return hourly;
    }

    public LiveData<List<UnifiedWeatherResponse>> getCurrent() {
        return current;
    }
}
