package com.optlab.nimbus.data;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.PressureUnit;
import com.optlab.nimbus.data.model.TemperatureUnit;
import com.optlab.nimbus.data.model.WindSpeedUnit;
import com.optlab.nimbus.data.preferences.UserPreferences;
import com.optlab.nimbus.data.preferences.UserPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fake implementation of UserPreferences for testing purposes. This class simulates the behavior of
 * user preferences without relying on actual storage. It provides methods to set and get locations
 * and units.
 */
public class FakeUserPreferenceManager implements UserPreferences {
    private final List<String> locations = new ArrayList<>();
    private final Map<String, Enum<?>> units = new HashMap<>();

    public FakeUserPreferenceManager() {
        // Initialize with some default values
        locations.add(new Gson().toJson(new Coordinates("10.8231", "106.6297"))); // HCM City
        units.put(UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.CELSIUS);
        units.put(UserPreferencesManager.WIND_SPEED_UNIT, WindSpeedUnit.METERS_PER_SECOND);
        units.put(UserPreferencesManager.PRESSURE_UNIT, PressureUnit.HECTOPASCAL);
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
    public Enum<?> getUnit(@NonNull String key) {
        return units.get(key);
    }

    @Override
    public TemperatureUnit getTemperatureUnit() {
        return (TemperatureUnit) getUnit(UserPreferencesManager.TEMPERATURE_UNIT);
    }

    @Override
    public WindSpeedUnit getWindSpeedUnit() {
        return (WindSpeedUnit) getUnit(UserPreferencesManager.WIND_SPEED_UNIT);
    }

    @Override
    public PressureUnit getPressureUnit() {
        return (PressureUnit) getUnit(UserPreferencesManager.PRESSURE_UNIT);
    }
}
