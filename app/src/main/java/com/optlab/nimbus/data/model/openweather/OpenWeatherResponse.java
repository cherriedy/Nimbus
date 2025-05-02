package com.optlab.nimbus.data.model.openweather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record OpenWeatherResponse(
        @SerializedName("lat") double lat,
        @SerializedName("lon") double lon,
        @SerializedName("day") String day,
        @SerializedName("date_time") String dateTime,
        @SerializedName("timezone") String timeZone,
        @SerializedName("timezone_offset") int offSet,
        @SerializedName("current") Current current,
        @SerializedName("daily") List<Daily> daily) {}
