package com.optlab.nimbus.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.UnifiedWeatherResponse;
import com.optlab.nimbus.data.repository.WeatherRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class DailyWeatherViewModel extends ViewModel {
    private final WeatherRepository repository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MutableLiveData<List<UnifiedWeatherResponse>> daily = new MutableLiveData<>();

    @Inject
    public DailyWeatherViewModel(@NonNull WeatherRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }

    public LiveData<List<UnifiedWeatherResponse>> getDaily() {
        return daily;
    }

    public void fetchDailyWeatherByLocation(@NonNull Coordinates coordinates) {
        disposable.add(
                repository
                        .getDailyWeatherByLocation(coordinates)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onDailyFetchedSuccessful, this::onDailyFetchedFail));
    }

    private void onDailyFetchedFail(Throwable throwable) {
        Timber.e("onError: %s", throwable.getMessage());
        daily.postValue(null);
    }

    private void onDailyFetchedSuccessful(List<UnifiedWeatherResponse> unifiedWeatherResponses) {
        if (unifiedWeatherResponses != null && !unifiedWeatherResponses.isEmpty()) {
            daily.postValue(unifiedWeatherResponses);
            Timber.d("onSuccess: %s", unifiedWeatherResponses.size());
        } else {
            Timber.d("onSuccess: response is null or empty");
        }
    }
}
