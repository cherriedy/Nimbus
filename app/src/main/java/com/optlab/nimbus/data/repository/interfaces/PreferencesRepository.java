package com.optlab.nimbus.data.repository.interfaces;

import com.optlab.nimbus.data.model.ForecastProvider;
import com.optlab.nimbus.data.model.Pressure;
import com.optlab.nimbus.data.model.Temperature;
import com.optlab.nimbus.data.model.WindSpeed;

import io.reactivex.rxjava3.core.Observable;

public interface PreferencesRepository {
    Observable<Temperature.Unit> getTemperatureUnit();

    Observable<WindSpeed.Unit> getWindSpeedUnit();

    Observable<Pressure.Unit> getPressureUnit();

    Observable<String> getApiKey(ForecastProvider provider);

    Observable<ForecastProvider> getWeatherProvider();

    void setTemperatureUnit(Temperature.Unit unit);

    void setWindSpeedUnit(WindSpeed.Unit unit);

    void setPressureUnit(Pressure.Unit unit);

    void setApiKey(String apiKey, ForecastProvider provider);

    void removeApiKey(ForecastProvider provider);
}
