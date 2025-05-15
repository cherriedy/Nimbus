package com.optlab.nimbus.data.preferences;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.common.PressureUnit;
import com.optlab.nimbus.data.common.TemperatureUnit;
import com.optlab.nimbus.data.common.WindSpeedUnit;

public interface SettingPreferences {
    void setUnit(@NonNull String key, @NonNull Enum<?> unit);

    void setWeatherProvider(@NonNull ForecastProvider provider);

    Enum<?> getUnit(@NonNull String key);

    TemperatureUnit getTemperatureUnit();

    WindSpeedUnit getWindSpeedUnit();

    PressureUnit getPressureUnit();

    ForecastProvider getWeatherProvider();

    void registerOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);

    void unregisterOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);
}
