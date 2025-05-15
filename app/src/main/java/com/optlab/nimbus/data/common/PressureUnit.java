package com.optlab.nimbus.data.common;

import lombok.Getter;

@Getter
public enum PressureUnit {
    HECTOPASCAL("hPa"),
    ATMOSPHERE("atm"),
    BAR("bar"),
    PASCAL("Pa"),
    TORR("torr");

    private final String name;

    PressureUnit(String name) {
        this.name = name;
    }
}
