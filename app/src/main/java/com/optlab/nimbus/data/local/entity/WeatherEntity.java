package com.optlab.nimbus.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.optlab.nimbus.constant.ResponseConstant;
import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.model.common.Coordinates;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(tableName = "weather_database")
@TypeConverters({Converters.class})
public class WeatherEntity {
    public enum Type {
        WEAKLY,
        HOURLY,
        CURRENT
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * Type of weather data. This is used to determine which API to use and how to parse the
     * response.
     */
    private Type type;

    /**
     * Weather provider from which the data is fetched. This is used to determine which API to use.
     */
    private WeatherProvider provider;

    /**
     * Weather data in JSON format. This is the raw data fetched from the API. It is stored as a
     * string in the database.
     */
    private String data;

    /**
     * Coordinates of the location for which the weather data is fetched. This is used to determine
     * the location for which the weather data is fetched.
     */
    private Coordinates coordinates;

    /**
     * Timestamp of when the weather data was fetched. This is used to determine if the data is
     * expired or not.
     */
    private long timestamp;

    public boolean isExpired() {
        return switch (type) {
            case WEAKLY, HOURLY ->
                    System.currentTimeMillis() - timestamp > ResponseConstant.DAILY_EXPIRY_TIME;
            case CURRENT ->
                    System.currentTimeMillis() - timestamp > ResponseConstant.CURRENT_EXPIRY_TIME;
        };
    }
}
