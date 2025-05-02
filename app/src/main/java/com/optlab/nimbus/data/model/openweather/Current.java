package com.optlab.nimbus.data.model.openweather;

import com.google.gson.annotations.SerializedName;

public record Current(@SerializedName("dt") long dt) {}
