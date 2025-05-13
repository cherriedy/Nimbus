package com.optlab.nimbus.data.model.common;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The general weather response model that is used to unify the data from different weather APIs.
 *
 * <p>
 *
 * <ul>
 *   <li>date string representing the date of the weather forecast
 *   <li>temperatureMax maximum temperature for the day (according to the API, should be in Celsius)
 *   <li>temperatureMin minimum temperature for the day (according to the API, should be in Celsius)
 *   <li>pressure air pressure (according on the API, should be in hPa)
 *   <li>windSpeed wind speed (according to the API, should be in m/s)
 *   <li>humidity humidity percentage (should be in %)
 *   <li>weatherCode weather code
 *   <li>weatherIcon weather icon
 *   <li>weatherDescription weather description
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String date;
    private double temperature;
    private double temperatureMax;
    private double temperatureMin;
    private double pressure;
    private double windSpeed;
    private double humidity;
    private int weatherCode;
    @DrawableRes private int weatherIcon;
    @StringRes private int weatherDescription;
}
