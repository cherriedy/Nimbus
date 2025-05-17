package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.model.ForecastProvider;
import com.optlab.nimbus.data.model.Pressure;
import com.optlab.nimbus.data.model.Temperature;
import com.optlab.nimbus.data.model.WindSpeed;
import com.optlab.nimbus.data.preferences.SettingPreferencesImpl;
import com.optlab.nimbus.data.preferences.interfaces.ForecastApiPreferences;
import com.optlab.nimbus.data.preferences.interfaces.SettingPreferences;
import com.optlab.nimbus.data.repository.interfaces.PreferencesRepository;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.Objects;

import javax.inject.Inject;

public class PreferencesRepositoryImpl implements PreferencesRepository {
    private final SettingPreferences settingPreferences;
    private final ForecastApiPreferences forecastApiPreferences;
    private final BehaviorSubject<Temperature.Unit> tempUnitSubject = BehaviorSubject.create();
    private final BehaviorSubject<WindSpeed.Unit> windSpeedUnitSubject = BehaviorSubject.create();
    private final BehaviorSubject<Pressure.Unit> pressureUnitSubject = BehaviorSubject.create();
    private final BehaviorSubject<ForecastProvider> providerSubject = BehaviorSubject.create();
    private final BehaviorSubject<String> apiKeySubject = BehaviorSubject.create();

    @Inject
    public PreferencesRepositoryImpl(
            SettingPreferences settingPreferences, ForecastApiPreferences forecastApiPreferences) {
        this.settingPreferences = settingPreferences;
        this.forecastApiPreferences = forecastApiPreferences;

        tempUnitSubject.onNext(settingPreferences.getTemperatureUnit());
        windSpeedUnitSubject.onNext(settingPreferences.getWindSpeedUnit());
        pressureUnitSubject.onNext(settingPreferences.getPressureUnit());
        providerSubject.onNext(settingPreferences.getWeatherProvider());
        apiKeySubject.onNext(forecastApiPreferences.getApiKey(
                ForecastProvider.TOMORROW_IO.name()));

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
    public Observable<Temperature.Unit> getTemperatureUnit() {
        return tempUnitSubject.hide();
    }

    @Override
    public Observable<WindSpeed.Unit> getWindSpeedUnit() {
        return windSpeedUnitSubject.hide();
    }

    @Override
    public Observable<Pressure.Unit> getPressureUnit() {
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
    public void setTemperatureUnit(Temperature.Unit unit) {
        settingPreferences.setUnit(SettingPreferencesImpl.TEMPERATURE_UNIT, unit);
    }

    @Override
    public void setWindSpeedUnit(WindSpeed.Unit unit) {
        settingPreferences.setUnit(SettingPreferencesImpl.WIND_SPEED_UNIT, unit);
    }

    @Override
    public void setPressureUnit(Pressure.Unit unit) {
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
