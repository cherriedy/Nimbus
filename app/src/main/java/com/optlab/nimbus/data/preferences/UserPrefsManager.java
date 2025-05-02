package com.optlab.nimbus.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.PressureUnit;
import com.optlab.nimbus.data.model.common.TemperatureUnit;
import com.optlab.nimbus.data.model.common.WindSpeedUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * @noinspection rawtypes
 */
public class UserPrefsManager {
    public static final String TEMPERATURE_UNIT = "temperature_unit";
    public static final String WIND_SPEED_UNIT = "wind_speed_unit";
    public static final String PRESSURE_UNIT = "pressure_unit";

    private static final String PREF_NAME = "user_prefs";
    private static final String[] UNITS =
            new String[] {TEMPERATURE_UNIT, WIND_SPEED_UNIT, PRESSURE_UNIT};
    private static final String LOCATIONS = "locations";

    private final SharedPreferences userPrefs;
    private final Gson gson;

    public UserPrefsManager(@NonNull Context context) {
        this.userPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        initUnits();
    }

    public void setLocation(@NonNull Coordinates coordinates) {
        List<String> locations = getLocations();
        String json = gson.toJson(coordinates);
        if (!locations.contains(json)) {
            locations.add(json);
            userPrefs.edit().putString(LOCATIONS, gson.toJson(locations)).apply();
        } else {
            Timber.w("Location already exists");
        }
    }

    public Coordinates getLocation(int position) {
        List<String> locations = getLocations();
        if (locations.isEmpty()) {
            return null;
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(locations.get(position), type);
    }

    public List<String> getLocations() {
        String json = userPrefs.getString(LOCATIONS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void initUnits() {
        if (!userPrefs.contains(TEMPERATURE_UNIT)) {
            setUnit(TEMPERATURE_UNIT, TemperatureUnit.CELSIUS);
        }
        if (!userPrefs.contains(WIND_SPEED_UNIT)) {
            setUnit(WIND_SPEED_UNIT, WindSpeedUnit.METERS_PER_SECOND);
        }
        if (!userPrefs.contains(PRESSURE_UNIT)) {
            setUnit(PRESSURE_UNIT, PressureUnit.HECTOPASCAL);
        }
    }

    public void setUnit(@NonNull String key, @NonNull Enum unit) {
        assertValidKey(key);
        if (unit == null) {
            throw new NullPointerException("Unit cannot be null");
        }
        userPrefs.edit().putString(key, unit.name()).apply();
    }

    private static void assertValidKey(@NonNull String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new NullPointerException("Key cannot be null or empty");
        }
        if (!Arrays.asList(UNITS).contains(key)) {
            throw new IllegalArgumentException("Key is invalid");
        }
    }

    public Enum getUnit(@NonNull String key) {
        assertValidKey(key);
        return switch (key) {
            case TEMPERATURE_UNIT ->
                    Enum.valueOf(
                            TemperatureUnit.class,
                            userPrefs.getString(TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name()));
            case WIND_SPEED_UNIT ->
                    Enum.valueOf(
                            WindSpeedUnit.class,
                            userPrefs.getString(
                                    WIND_SPEED_UNIT, WindSpeedUnit.METERS_PER_SECOND.name()));
            case PRESSURE_UNIT ->
                    Enum.valueOf(
                            PressureUnit.class,
                            userPrefs.getString(PRESSURE_UNIT, PressureUnit.HECTOPASCAL.name()));
            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }

    public TemperatureUnit getTemperatureUnit() {
        return (TemperatureUnit) getUnit(TEMPERATURE_UNIT);
    }

    public WindSpeedUnit getWindSpeedUnit() {
        return (WindSpeedUnit) getUnit(WIND_SPEED_UNIT);
    }

    public PressureUnit getPressureUnit() {
        return (PressureUnit) getUnit(PRESSURE_UNIT);
    }
}
