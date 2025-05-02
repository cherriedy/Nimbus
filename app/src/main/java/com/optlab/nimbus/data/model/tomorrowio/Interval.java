package com.optlab.nimbus.data.model.tomorrowio;

import com.google.gson.annotations.SerializedName;

public record Interval(
        @SerializedName("startTime") String startTime, @SerializedName("values") Values values) {}
