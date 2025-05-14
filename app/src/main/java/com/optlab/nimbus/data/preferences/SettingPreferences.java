package com.optlab.nimbus.data.preferences;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.PressureUnit;
import com.optlab.nimbus.data.model.common.TemperatureUnit;
import com.optlab.nimbus.data.model.common.WindSpeedUnit;

import java.util.List;

public interface SettingPreferences {
    void setLocation(@NonNull Coordinates coordinates);

    Coordinates getLocation(int position);

    List<String> getLocations();

    void setUnit(@NonNull String key, @NonNull Enum<?> unit);

    void setWeatherProvider(@NonNull WeatherProvider provider);

    Enum<?> getUnit(@NonNull String key);

    TemperatureUnit getTemperatureUnit();

    WindSpeedUnit getWindSpeedUnit();

    PressureUnit getPressureUnit();

    WeatherProvider getWeatherProvider();

    void registerOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);

    void unregisterOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);
}
