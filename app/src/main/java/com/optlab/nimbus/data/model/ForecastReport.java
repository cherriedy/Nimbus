package com.optlab.nimbus.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ForecastReport {
    private String startTime;
    private String endTime;
    private Temperature temperature;
    private Temperature temperatureMin;
    private Temperature temperatureMax;
    private Wind wind;
    private String sunsetTime;
    private String sunriseTime;
    private Pressure pressure;
    private double humidity;
    private int uvIndex;
    private int weatherCode;
    private int weatherIcon;
    private int weatherDescription;

    // private final Temperature feelsLike = calculateFeelsLike(temperature, humidity);
    private final Temperature feelsLike = new Temperature(100, Temperature.Unit.FAHRENHEIT);

    private Temperature calculateFeelsLike(Temperature temperature, double humidity) {
        double temperatureF = temperature.getFahrenheit();
        double simpleHI =
                0.5 * (temperatureF + 61.0 + ((temperatureF - 68.0) * 1.2) + (humidity * 0.094));
        double averageHI = (simpleHI + temperatureF) / 2.0;

        if (averageHI < 80.0) {
            return new Temperature(averageHI, Temperature.Unit.FAHRENHEIT);
        }

        double heatIndex =
                -42.379
                        + 2.04901523 * temperatureF
                        + 10.14333127 * humidity
                        - 0.22475541 * temperatureF * humidity
                        - 0.00683783 * temperatureF * temperatureF
                        - 0.05481717 * humidity * humidity
                        + 0.00122874 * temperatureF * temperatureF * humidity
                        + 0.00085282 * temperatureF * humidity * humidity
                        - 0.00000199 * temperatureF * temperatureF * humidity * humidity;

        if (humidity < 13 && temperatureF >= 80 && temperatureF <= 112) {
            double adjustment =
                    ((13 - humidity) / 4.0)
                            * Math.sqrt((17 - Math.abs(temperatureF - 95.0)) / 17.0);
            heatIndex -= adjustment;
        } else if (humidity > 85 && temperatureF >= 80 && temperatureF <= 87) {
            double adjustment = ((humidity - 85) / 10.0) * ((87 - temperatureF) / 5.0);
            heatIndex += adjustment;
        }

        return new Temperature(heatIndex, Temperature.Unit.FAHRENHEIT);
    }
}
