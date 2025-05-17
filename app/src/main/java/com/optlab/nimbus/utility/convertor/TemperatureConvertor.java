package com.optlab.nimbus.utility.convertor;

import com.optlab.nimbus.data.model.TemperatureUnit;

public final class TemperatureConvertor {
    private TemperatureConvertor() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }

    /**
     * Convert temperature to Fahrenheit or Kelvin based on the Celsius value.
     */
    public static int fromCelsius(double temperature, TemperatureUnit unit) {
        return (int) switch (unit) {
            case FAHRENHEIT -> (temperature - 32) * 5 / 9;
            case KELVIN -> temperature - 273.15;
            case CELSIUS -> temperature;
        };
    }
}
