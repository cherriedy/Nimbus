package com.optlab.nimbus.data.repository;

import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.PressureUnit;
import com.optlab.nimbus.data.model.common.TemperatureUnit;
import com.optlab.nimbus.data.model.common.WindSpeedUnit;

import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public interface PreferencesRepository {
    Observable<TemperatureUnit> getTemperatureUnit();

    Observable<WindSpeedUnit> getWindSpeedUnit();

    Observable<PressureUnit> getPressureUnit();

    Observable<Coordinates> getLastLocation();

    Observable<List<Coordinates>> getLocations();

    Observable<String> getApiKey(WeatherProvider provider);

    Observable<WeatherProvider> getWeatherProvider();

    void setTemperatureUnit(TemperatureUnit unit);

    void setWindSpeedUnit(WindSpeedUnit unit);

    void setPressureUnit(PressureUnit unit);

    void setLastLocation(Coordinates coordinates);

    void setApiKey(String apiKey, WeatherProvider provider);

    void setWeatherProvider(WeatherProvider provider);

    void removeLocation(Coordinates coordinates);

    void removeApiKey(WeatherProvider provider);
}
