package com.optlab.nimbus.data.preferences.interfaces;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.model.ForecastProvider;
import com.optlab.nimbus.data.model.Pressure;
import com.optlab.nimbus.data.model.Temperature;
import com.optlab.nimbus.data.model.WindSpeed;

public interface SettingPreferences {
    void setUnit(@NonNull String key, @NonNull Enum<?> unit);

    void setWeatherProvider(@NonNull ForecastProvider provider);

    Enum<?> getUnit(@NonNull String key);

    Temperature.Unit getTemperatureUnit();

    WindSpeed.Unit getWindSpeedUnit();

    Pressure.Unit getPressureUnit();

    ForecastProvider getWeatherProvider();

    void registerOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);

    void unregisterOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);
}
