package com.optlab.nimbus.data.model.tomorrowio;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record Timeline(@SerializedName("intervals") List<Interval> intervals) {}
