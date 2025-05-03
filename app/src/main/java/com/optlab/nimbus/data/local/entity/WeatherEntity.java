package com.optlab.nimbus.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
