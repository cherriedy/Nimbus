package com.optlab.nimbus.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.optlab.nimbus.constant.ResponseConstant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(tableName = "weather_database")
@TypeConverters({Converters.class})
public class WeatherEntity {
    public enum Type {
        DAILY,
        HOURLY,
        CURRENT
    }

    @PrimaryKey(autoGenerate = true)
    private int id; // Unique ID for each weather data entry

    private Type type; // Type of weather data (DAILY, HOURLY, CURRENT)
    private String data; // Weather data in JSON format
    private long timestamp; // Timestamp of the weather data

    public boolean isExpired() {
        return switch (type) {
            case DAILY, HOURLY ->
                    System.currentTimeMillis() - timestamp > ResponseConstant.DAILY_EXPIRY_TIME;
            case CURRENT ->
                    System.currentTimeMillis() - timestamp > ResponseConstant.CURRENT_EXPIRY_TIME;
        };
    }
}
