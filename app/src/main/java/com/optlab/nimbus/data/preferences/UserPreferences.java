package com.optlab.nimbus.data.preferences;

import androidx.annotation.NonNull;

import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.PressureUnit;
import com.optlab.nimbus.data.model.TemperatureUnit;
import com.optlab.nimbus.data.model.WindSpeedUnit;

import java.util.List;

public interface UserPreferences {
    void setLocation(@NonNull Coordinates coordinates);

    Coordinates getLocation(int position);

    List<String> getLocations();

    void setUnit(@NonNull String key, @NonNull Enum<?> unit);

    Enum<?> getUnit(@NonNull String key);

    TemperatureUnit getTemperatureUnit();

    WindSpeedUnit getWindSpeedUnit();

    PressureUnit getPressureUnit();
}
