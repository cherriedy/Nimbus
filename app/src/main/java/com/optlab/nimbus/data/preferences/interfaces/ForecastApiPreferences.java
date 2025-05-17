package com.optlab.nimbus.data.preferences.interfaces;

import android.content.SharedPreferences;

public interface ForecastApiPreferences {
    void setApiKey(String provider, String key);

    String getApiKey(String provider);

    void removeApiKey(String provider);

    void registerOnChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);

    void unregisterOnChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);
}
