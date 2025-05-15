package com.optlab.nimbus.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.common.PressureUnit;
import com.optlab.nimbus.data.common.TemperatureUnit;
import com.optlab.nimbus.data.common.WindSpeedUnit;

import java.util.Arrays;

/**
 * UserPrefsManager is a class that manages the user preferences for the application. It uses
 * SharedPreferences to store the preferences and Gson to serialize and deserialize the data.
 *
 * @noinspection rawtypes
 */
public class SettingPreferencesImpl implements SettingPreferences {
    public static final String TEMPERATURE_UNIT = "temperature_unit";
    public static final String WIND_SPEED_UNIT = "wind_speed_unit";
    public static final String PRESSURE_UNIT = "pressure_unit";
    public static final String WEATHER_PROVIDER = "weather_provider";

    private static final String PREF_NAME = "setting_prefs";
    private static final String[] UNITS =
            new String[] {TEMPERATURE_UNIT, WIND_SPEED_UNIT, PRESSURE_UNIT};

    private final SharedPreferences settingsPrefs;

    public SettingPreferencesImpl(@NonNull Context context) {
        this.settingsPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        initUnits();
    }

    @Override
    public void setWeatherProvider(@NonNull ForecastProvider provider) {
        if (provider == null) {
            provider = ForecastProvider.TOMORROW_IO;
        }
        settingsPrefs.edit().putString(WEATHER_PROVIDER, provider.name()).apply();
    }

    /**
     * Initialize the units in the shared preferences. If the units do not exist, they will be
     * initialized to the default values.
     */
    private void initUnits() {
        if (!settingsPrefs.contains(TEMPERATURE_UNIT)) {
            setUnit(TEMPERATURE_UNIT, TemperatureUnit.CELSIUS);
        }
        if (!settingsPrefs.contains(WIND_SPEED_UNIT)) {
            setUnit(WIND_SPEED_UNIT, WindSpeedUnit.METERS_PER_SECOND);
        }
        if (!settingsPrefs.contains(PRESSURE_UNIT)) {
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
        settingsPrefs.edit().putString(key, unit.name()).apply();
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
                            settingsPrefs.getString(
                                    TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name()));
            case WIND_SPEED_UNIT ->
                    Enum.valueOf(
                            WindSpeedUnit.class,
                            settingsPrefs.getString(
                                    WIND_SPEED_UNIT, WindSpeedUnit.METERS_PER_SECOND.name()));
            case PRESSURE_UNIT ->
                    Enum.valueOf(
                            PressureUnit.class,
                            settingsPrefs.getString(
                                    PRESSURE_UNIT, PressureUnit.HECTOPASCAL.name()));
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

    @Override
    public ForecastProvider getWeatherProvider() {
        String providerName =
                settingsPrefs.getString(WEATHER_PROVIDER, ForecastProvider.TOMORROW_IO.name());
        return ForecastProvider.valueOf(providerName);
    }

    @Override
    public void registerOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        settingsPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener) {
        settingsPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
