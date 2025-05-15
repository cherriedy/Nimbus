package com.optlab.nimbus.data;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.common.PressureUnit;
import com.optlab.nimbus.data.common.TemperatureUnit;
import com.optlab.nimbus.data.common.WindSpeedUnit;
import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.data.preferences.SettingPreferencesImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fake implementation of UserPreferences for testing purposes. This class simulates the behavior of
 * user preferences without relying on actual storage. It provides methods to set and get locations
 * and units.
 */
public class FakeSettingPreferenceManager implements SettingPreferences {
    private final List<String> locations = new ArrayList<>();
    private final Map<String, Enum<?>> units = new HashMap<>();

    public FakeSettingPreferenceManager() {
        // Initialize with some default values
        locations.add(new Gson().toJson(new Coordinates("10.8231", "106.6297"))); // HCM City
        units.put(SettingPreferencesImpl.TEMPERATURE_UNIT, TemperatureUnit.CELSIUS);
        units.put(SettingPreferencesImpl.WIND_SPEED_UNIT, WindSpeedUnit.METERS_PER_SECOND);
        units.put(SettingPreferencesImpl.PRESSURE_UNIT, PressureUnit.HECTOPASCAL);
    }

    @Override
    public void setLocation(@NonNull Coordinates coordinates) {
        String json = new Gson().toJson(coordinates);
        if (!locations.contains(json)) {
            locations.add(json);
        }
    }

    @Override
    public Coordinates getLocation(int position) {
        return new Gson()
                .fromJson(locations.get(position), new TypeToken<Coordinates>() {}.getType());
    }

    @Override
    public List<String> getLocations() {
        return new ArrayList<>(locations);
    }

    @Override
    public void setUnit(@NonNull String key, @NonNull Enum<?> unit) {
        units.put(key, unit);
    }

    @Override
    public void setWeatherProvider(@NonNull ForecastProvider provider) {}

    @Override
    public Enum<?> getUnit(@NonNull String key) {
        return units.get(key);
    }

    @Override
    public TemperatureUnit getTemperatureUnit() {
        return (TemperatureUnit) getUnit(SettingPreferencesImpl.TEMPERATURE_UNIT);
    }

    @Override
    public WindSpeedUnit getWindSpeedUnit() {
        return (WindSpeedUnit) getUnit(SettingPreferencesImpl.WIND_SPEED_UNIT);
    }

    @Override
    public PressureUnit getPressureUnit() {
        return (PressureUnit) getUnit(SettingPreferencesImpl.PRESSURE_UNIT);
    }

    @Override
    public ForecastProvider getWeatherProvider() {
        return null;
    }

    @Override
    public void registerOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {}

    @Override
    public void unregisterOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {}
}
