package com.optlab.nimbus.data.model.tomorrowio;

import com.google.gson.annotations.SerializedName;

public record TomorrowIoResponse(@SerializedName("data") Data data) {}
