package com.optlab.nimbus.data.model.openweather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record Daily(
        @SerializedName("sunrise") long sunrise,
        @SerializedName("sunset") long sunset,
        @SerializedName("weather") List<Weather> weather,
        @SerializedName("wind_speed") double windSpeed,
        @SerializedName("humidity") int humidity,
        @SerializedName("pressure") String pressure,
        @SerializedName("temp") Temp temp) {

    public record Weather(@SerializedName("id") int id) {}

    public record Temp(@SerializedName("min") double min, @SerializedName("max") double max) {}
}
