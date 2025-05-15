package com.optlab.nimbus.data.model.forecast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.local.entity.ForecastEntity;
import com.optlab.nimbus.data.model.Coordinates;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import timber.log.Timber;

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
public class ForecastResponse {
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

    public static ForecastEntity mapToForecastEntity(
            @NonNull List<ForecastResponse> response,
            @NonNull Coordinates coordinates,
            @NonNull ForecastEntity.Type type) {
        ForecastEntity entity = new ForecastEntity();
        entity.setCoordinates(coordinates);
        entity.setType(type);
        entity.setData(new Gson().toJson(response));
        entity.setProvider(ForecastProvider.TOMORROW_IO);
        entity.setTimestamp(System.currentTimeMillis());
        return entity;
    }

    public static List<ForecastResponse> mapFromForecastEntity(ForecastEntity entity) {
        if (entity == null || entity.getData() == null) {
            Timber.e("WeatherEntity or its data is null");
            return Collections.emptyList();
        }
        try {
            Type listType = new TypeToken<List<ForecastResponse>>() {}.getType();
            return new Gson().fromJson(entity.getData(), listType);
        } catch (Exception e) {
            Timber.e("Failed to parse WeatherEntity data: %s", e.getMessage());
            return Collections.emptyList();
        }
    }
}
