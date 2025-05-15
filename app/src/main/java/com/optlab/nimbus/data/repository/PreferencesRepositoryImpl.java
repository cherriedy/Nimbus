package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.common.PressureUnit;
import com.optlab.nimbus.data.common.TemperatureUnit;
import com.optlab.nimbus.data.common.WindSpeedUnit;
import com.optlab.nimbus.data.preferences.ForecastApiPreferences;
import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.data.preferences.SettingPreferencesImpl;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import timber.log.Timber;

import java.util.Objects;

import javax.inject.Inject;

public class PreferencesRepositoryImpl implements PreferencesRepository {
    private final SettingPreferences settingPreferences;
    private final ForecastApiPreferences forecastApiPreferences;
    private final BehaviorSubject<TemperatureUnit> tempUnitSubject = BehaviorSubject.create();
    private final BehaviorSubject<WindSpeedUnit> windSpeedUnitSubject = BehaviorSubject.create();
    private final BehaviorSubject<PressureUnit> pressureUnitSubject = BehaviorSubject.create();
    private final BehaviorSubject<Coordinates> lastLocationSubject = BehaviorSubject.create();
    private final BehaviorSubject<ForecastProvider> providerSubject = BehaviorSubject.create();
    private final BehaviorSubject<String> apiKeySubject = BehaviorSubject.create();
    private final Gson gson = new Gson();

    @Inject
    public PreferencesRepositoryImpl(
            @NonNull SettingPreferences settingPreferences,
            @NonNull ForecastApiPreferences forecastApiPreferences) {
        this.settingPreferences = settingPreferences;
        this.forecastApiPreferences = forecastApiPreferences;

        tempUnitSubject.onNext(settingPreferences.getTemperatureUnit());
        windSpeedUnitSubject.onNext(settingPreferences.getWindSpeedUnit());
        pressureUnitSubject.onNext(settingPreferences.getPressureUnit());
        providerSubject.onNext(settingPreferences.getWeatherProvider());
        apiKeySubject.onNext(forecastApiPreferences.getApiKey(ForecastProvider.TOMORROW_IO.name()));

        Timber.d("Loaded temperature unit: %s", tempUnitSubject.getValue());
        Timber.d("Loaded wind speed unit: %s", windSpeedUnitSubject.getValue());
        Timber.d("Loaded pressure unit: %s", pressureUnitSubject.getValue());
        Timber.d("Loaded weather provider: %s", providerSubject.getValue());
        Timber.d("Loaded API key: %s", apiKeySubject.getValue());

        observePreferences();
    }

    private void observePreferences() {
        observeSettingPreference();
        observeWeatherApiPreference();
    }

    private void observeWeatherApiPreference() {
        forecastApiPreferences.registerOnChangeListener(
                (sharedPreferences, key) -> {
                    if (Objects.equals(key, ForecastProvider.TOMORROW_IO.name())) {
                        apiKeySubject.onNext(
                                forecastApiPreferences.getApiKey(
                                        ForecastProvider.TOMORROW_IO.name()));
                    }
                    if (Objects.equals(key, ForecastProvider.OPEN_WEATHER.name())) {
                        apiKeySubject.onNext(
                                forecastApiPreferences.getApiKey(
                                        ForecastProvider.OPEN_WEATHER.name()));
                    }
                });
    }

    private void observeSettingPreference() {
        settingPreferences.registerOnChangeListener(
                (sharedPreferences, key) -> {
                    switch (Objects.requireNonNull(key)) {
                        case SettingPreferencesImpl.TEMPERATURE_UNIT ->
                                tempUnitSubject.onNext(settingPreferences.getTemperatureUnit());
                        case SettingPreferencesImpl.WIND_SPEED_UNIT ->
                                windSpeedUnitSubject.onNext(settingPreferences.getWindSpeedUnit());
                        case SettingPreferencesImpl.PRESSURE_UNIT ->
                                pressureUnitSubject.onNext(settingPreferences.getPressureUnit());
                    }
                });
    }

    @Override
    public Observable<TemperatureUnit> getTemperatureUnit() {
        return tempUnitSubject.hide();
    }

    @Override
    public Observable<WindSpeedUnit> getWindSpeedUnit() {
        return windSpeedUnitSubject.hide();
    }

    @Override
    public Observable<PressureUnit> getPressureUnit() {
        return pressureUnitSubject.hide();
    }

    @Override
    public Observable<String> getApiKey(ForecastProvider provider) {
        return apiKeySubject.hide();
    }

    @Override
    public Observable<ForecastProvider> getWeatherProvider() {
        return providerSubject.hide();
    }

    @Override
    public void setTemperatureUnit(TemperatureUnit unit) {
        settingPreferences.setUnit(SettingPreferencesImpl.TEMPERATURE_UNIT, unit);
    }

    @Override
    public void setWindSpeedUnit(WindSpeedUnit unit) {
        settingPreferences.setUnit(SettingPreferencesImpl.WIND_SPEED_UNIT, unit);
    }

    @Override
    public void setPressureUnit(PressureUnit unit) {
        settingPreferences.setUnit(SettingPreferencesImpl.PRESSURE_UNIT, unit);
    }

    @Override
    public void setApiKey(String apiKey, ForecastProvider provider) {
        forecastApiPreferences.setApiKey(provider.name(), apiKey);
    }

    @Override
    public void removeApiKey(ForecastProvider provider) {
        forecastApiPreferences.removeApiKey(provider.name());
    }
}
