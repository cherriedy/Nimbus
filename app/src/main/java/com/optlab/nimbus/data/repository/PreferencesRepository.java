package com.optlab.nimbus.data.repository;

import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.common.PressureUnit;
import com.optlab.nimbus.data.common.TemperatureUnit;
import com.optlab.nimbus.data.common.WindSpeedUnit;

import io.reactivex.rxjava3.core.Observable;

public interface PreferencesRepository {
    Observable<TemperatureUnit> getTemperatureUnit();

    Observable<WindSpeedUnit> getWindSpeedUnit();

    Observable<PressureUnit> getPressureUnit();

    Observable<String> getApiKey(ForecastProvider provider);

    Observable<ForecastProvider> getWeatherProvider();

    void setTemperatureUnit(TemperatureUnit unit);

    void setWindSpeedUnit(WindSpeedUnit unit);

    void setPressureUnit(PressureUnit unit);

    void setApiKey(String apiKey, ForecastProvider provider);

    void removeApiKey(ForecastProvider provider);
}
