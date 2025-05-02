package com.optlab.nimbus.data.model.common;

import lombok.Getter;

@Getter
public enum WindSpeedUnit {
    METERS_PER_SECOND("m/s"),
    KILOMETERS_PER_HOUR("km/h"),
    MILES_PER_HOUR("mph"),
    KNOTS("kn");

    private final String name;

    WindSpeedUnit(String name) {
        this.name = name;
    }

}
