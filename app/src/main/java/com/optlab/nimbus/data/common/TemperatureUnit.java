package com.optlab.nimbus.data.common;

import lombok.Getter;

@Getter
public enum TemperatureUnit {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
    KELVIN("K");
    private final String name;

    TemperatureUnit(String name) {
        this.name = name;
    }
}
