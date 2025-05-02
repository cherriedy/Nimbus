package com.optlab.nimbus.utility.convertor;

import com.optlab.nimbus.data.model.common.TemperatureUnit;

public final class TemperatureConvertor {
    private TemperatureConvertor() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }

    /** Convert temperature to Fahrenheit or Kelvin based on the Celsius value. */
    public static int fromCelsius(double temperature, TemperatureUnit unit) {
        return (int)
                Math.round(
                        switch (unit) {
                            case CELSIUS -> temperature;
                            case KELVIN -> temperature - 273.15;
                            case FAHRENHEIT -> (temperature - 32) * 5 / 9;
                        });
    }

    /** Convert temperature to Celsius based on the Fahrenheit or Kelvin value. */
    public static double toCelsius(double temperature, TemperatureUnit unit) {
        return switch (unit) {
            case CELSIUS -> temperature;
            case KELVIN -> temperature + 273.15;
            case FAHRENHEIT -> (temperature * 9 / 5) + 32;
        };
    }
}
