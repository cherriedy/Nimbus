package com.optlab.nimbus.data.model.tomorrowio;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record Data(@SerializedName("timelines") List<Timeline> timelines) {}
