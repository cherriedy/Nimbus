package com.optlab.nimbus.data.model.tomorrowio;

import com.google.gson.annotations.SerializedName;

public record Values(
        @SerializedName("temperature") float temperature,
        @SerializedName("temperatureMax") float temperatureMax,
        @SerializedName("temperatureMin") float temperatureMin,
        @SerializedName("weatherCode") int weatherCode,
        @SerializedName("sunriseTime") String sunriseTime,
        @SerializedName("sunsetTime") String sunsetTime,
        @SerializedName("windSpeed") float windSpeed,
        @SerializedName("humidity") float humidity,
        @SerializedName("pressureSurfaceLevel") float pressureSurfaceLevel) {}
