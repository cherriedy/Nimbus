package com.optlab.nimbus.data.network.openweather;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public record OpenWeatherResponse(
        @SerializedName("lat") double lat,
        @SerializedName("lon") double lon,
        @SerializedName("day") String day,
        @SerializedName("date_time") String dateTime,
        @SerializedName("timezone") String timeZone,
        @SerializedName("timezone_offset") int offSet,
        @SerializedName("current") Current current,
        @SerializedName("daily") List<Daily> daily) {

    public OpenWeatherResponse {
        Objects.requireNonNull(day, "day cannot be null");
        Objects.requireNonNull(dateTime, "dateTime cannot be null");
        Objects.requireNonNull(timeZone, "timeZone cannot be null");
        Objects.requireNonNull(current, "current cannot be null");
        Objects.requireNonNull(daily, "daily cannot be null");
        if (daily.isEmpty()) {
            throw new IllegalArgumentException("daily cannot be empty");
        }
    }

    public record Current(@SerializedName("dt") long dt) {}

    public record Daily(
            @SerializedName("sunrise") long sunrise,
            @SerializedName("sunset") long sunset,
            @SerializedName("weather") List<Weather> weather,
            @SerializedName("wind_speed") double windSpeed,
            @SerializedName("humidity") int humidity,
            @SerializedName("pressure") String pressure,
            @SerializedName("temp") Temp temp) {

        public Daily {
            Objects.requireNonNull(weather, "weather cannot be null");
            Objects.requireNonNull(temp, "temp cannot be null");
        }
    }

    public record Weather(@SerializedName("id") int id) {}

    public record Temp(@SerializedName("min") double min, @SerializedName("max") double max) {}
}
