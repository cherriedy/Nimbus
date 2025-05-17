package com.optlab.nimbus.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.PressureUnit;
import com.optlab.nimbus.data.model.TemperatureUnit;
import com.optlab.nimbus.data.model.WindSpeedUnit;

import timber.log.Timber;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * UserPrefsManager is a class that manages the user preferences for the application. It uses
 * SharedPreferences to store the preferences and Gson to serialize and deserialize the data.
 *
 * @noinspection rawtypes
 */
public class UserPreferencesManager implements UserPreferences {
    public static final String TEMPERATURE_UNIT = "temperature_unit";
    public static final String WIND_SPEED_UNIT = "wind_speed_unit";
    public static final String PRESSURE_UNIT = "pressure_unit";

    private static final String PREF_NAME = "user_prefs";
    private static final String[] UNITS =
            new String[] {TEMPERATURE_UNIT, WIND_SPEED_UNIT, PRESSURE_UNIT};
    private static final String LOCATIONS = "locations";

    private final SharedPreferences userPrefs;
    private final Gson gson;

    public UserPreferencesManager(@NonNull Context context) {
        this.userPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        initUnits();
    }

    /**
     * Set the location in the list of locations. If the location already exists, it will not be
     * added again.
     *
     * @param coordinates the location to be added
     */
    @Override
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

    /**
     * Get the location at the given position in the list of locations.
     *
     * @param position the position of the location in the list
     * @return the location at the given position
     */
    @Override
    public Coordinates getLocation(int position) {
        List<String> locations = getLocations();
        if (locations.isEmpty()) {
            return null;
        }
        Type type = new TypeToken<Coordinates>() {}.getType();
        return gson.fromJson(locations.get(position), type);
    }

    /**
     * Get the list of locations.
     *
     * @return the list of locations
     */
    @Override
    public List<String> getLocations() {
        String json = userPrefs.getString(LOCATIONS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Initialize the units in the shared preferences. If the units do not exist, they will be
     * initialized to the default values.
     */
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

    /**
     * Set the unit for the given key. The key must be one of the following: TEMPERATURE_UNIT,
     * WIND_SPEED_UNIT, PRESSURE_UNIT.
     *
     * @param key the key for the unit
     * @param unit the unit to be set
     */
    @Override
    public void setUnit(@NonNull String key, @NonNull Enum unit) {
        assertValidKey(key);
        if (unit == null) {
            throw new NullPointerException("Unit cannot be null");
        }
        userPrefs.edit().putString(key, unit.name()).apply();
    }

    /**
     * Assert that the key is valid (i.e., one of the following: TEMPERATURE_UNIT, WIND_SPEED_UNIT,
     * PRESSURE_UNIT). It should not be null or empty.
     *
     * @param key the key to be validated
     */
    private static void assertValidKey(@NonNull String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new NullPointerException("Key cannot be null or empty");
        }
        if (!Arrays.asList(UNITS).contains(key)) {
            throw new IllegalArgumentException("Key is invalid");
        }
    }

    /**
     * Get the unit for the given key. The key must be one of the following: TEMPERATURE_UNIT,
     * WIND_SPEED_UNIT, PRESSURE_UNIT.
     *
     * @param key the key for the unit
     * @return the unit for the given key
     */
    @Override
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

    /**
     * Get the temperature unit.
     *
     * @return the temperature unit
     */
    @Override
    public TemperatureUnit getTemperatureUnit() {
        return (TemperatureUnit) getUnit(TEMPERATURE_UNIT);
    }

    /**
     * Get the wind speed unit.
     *
     * @return the wind speed unit
     */
    @Override
    public WindSpeedUnit getWindSpeedUnit() {
        return (WindSpeedUnit) getUnit(WIND_SPEED_UNIT);
    }

    /**
     * Get the pressure unit.
     *
     * @return the pressure unit
     */
    @Override
    public PressureUnit getPressureUnit() {
        return (PressureUnit) getUnit(PRESSURE_UNIT);
    }
}
