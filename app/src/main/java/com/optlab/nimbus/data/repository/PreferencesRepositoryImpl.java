package com.optlab.nimbus.data.repository;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.PressureUnit;
import com.optlab.nimbus.data.model.common.TemperatureUnit;
import com.optlab.nimbus.data.model.common.WindSpeedUnit;
import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.data.preferences.SettingPreferencesImpl;
import com.optlab.nimbus.data.preferences.WeatherApiPreferences;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import timber.log.Timber;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class PreferencesRepositoryImpl implements PreferencesRepository {
    private final SettingPreferences settingPreferences;
    private final WeatherApiPreferences weatherApiPreferences;
    private final BehaviorSubject<TemperatureUnit> tempUnitSubject = BehaviorSubject.create();
    private final BehaviorSubject<WindSpeedUnit> windSpeedUnitSubject = BehaviorSubject.create();
    private final BehaviorSubject<PressureUnit> pressureUnitSubject = BehaviorSubject.create();
    private final BehaviorSubject<Coordinates> lastLocationSubject = BehaviorSubject.create();
    private final BehaviorSubject<List<Coordinates>> locationsSubject = BehaviorSubject.create();
    private final BehaviorSubject<WeatherProvider> providerSubject = BehaviorSubject.create();
    private final BehaviorSubject<String> apiKeySubject = BehaviorSubject.create();
    private final Gson gson = new Gson();

    @Inject
    public PreferencesRepositoryImpl(
            @NonNull SettingPreferences settingPreferences,
            @NonNull WeatherApiPreferences weatherApiPreferences) {
        this.settingPreferences = settingPreferences;
        this.weatherApiPreferences = weatherApiPreferences;

        tempUnitSubject.onNext(settingPreferences.getTemperatureUnit());
        Timber.d("Loaded temperature unit: %s", tempUnitSubject.getValue());
        windSpeedUnitSubject.onNext(settingPreferences.getWindSpeedUnit());
        Timber.d("Loaded wind speed unit: %s", windSpeedUnitSubject.getValue());
        pressureUnitSubject.onNext(settingPreferences.getPressureUnit());
        Timber.d("Loaded pressure unit: %s", pressureUnitSubject.getValue());
        // lastLocationSubject.onNext(settingPreferences.getLocation(0));
        providerSubject.onNext(settingPreferences.getWeatherProvider());
        Timber.d("Loaded weather provider: %s", providerSubject.getValue());
        apiKeySubject.onNext(weatherApiPreferences.getApiKey(WeatherProvider.TOMORROW_IO.name()));
        Timber.d("Loaded API key: %s", apiKeySubject.getValue());

        observePreferences();
    }

    private void observePreferences() {
        observeSettingPreference();
        observeWeatherApiPreference();
    }

    private void observeWeatherApiPreference() {
        weatherApiPreferences.registerOnChangeListener(
                (sharedPreferences, key) -> {
                    if (Objects.equals(key, WeatherProvider.TOMORROW_IO.name())) {
                        apiKeySubject.onNext(
                                weatherApiPreferences.getApiKey(
                                        WeatherProvider.TOMORROW_IO.name()));
                    }
                    if (Objects.equals(key, WeatherProvider.OPEN_WEATHER.name())) {
                        apiKeySubject.onNext(
                                weatherApiPreferences.getApiKey(
                                        WeatherProvider.OPEN_WEATHER.name()));
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
    public Observable<Coordinates> getLastLocation() {
        return lastLocationSubject.hide();
    }

    @Override
    public Observable<List<Coordinates>> getLocations() {
        return locationsSubject.hide();
    }

    @Override
    public Observable<String> getApiKey(WeatherProvider provider) {
        return apiKeySubject.hide();
    }

    @Override
    public Observable<WeatherProvider> getWeatherProvider() {
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
    public void setLastLocation(Coordinates coordinates) {
        String json = gson.toJson(coordinates);
        if (!settingPreferences.getLocations().contains(json)) {
            settingPreferences.setLocation(coordinates);
        }
    }

    @Override
    public void setApiKey(String apiKey, WeatherProvider provider) {
        weatherApiPreferences.setApiKey(provider.name(), apiKey);
    }

    @Override
    public void setWeatherProvider(WeatherProvider provider) {
        settingPreferences.setWeatherProvider(provider);
    }

    @Override
    public void removeLocation(Coordinates coordinates) {
        List<String> locations = settingPreferences.getLocations();
        String json = gson.toJson(coordinates);
        if (locations.contains(json)) {
            locations.remove(json);
            settingPreferences.setLocation(coordinates);
        }
    }

    @Override
    public void removeApiKey(WeatherProvider provider) {
        weatherApiPreferences.removeApiKey(provider.name());
    }
}
