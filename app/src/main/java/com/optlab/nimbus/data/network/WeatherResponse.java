package com.optlab.nimbus.data.network;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
