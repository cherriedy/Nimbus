package com.optlab.nimbus.data.preferences;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public interface ForecastApiPreferences {
    void setApiKey(String provider, String key);

    String getApiKey(String provider);

    void removeApiKey(String provider);

    void registerOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);

    void unregisterOnChangeListener(
            @NonNull SharedPreferences.OnSharedPreferenceChangeListener listener);
}
