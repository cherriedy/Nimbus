package com.optlab.nimbus.utility.convertor;

import com.optlab.nimbus.data.common.TemperatureUnit;

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
                            case KELVIN -> {
                                if (temperature < -273.15) {
                                    throw new IllegalArgumentException(
                                            "Temperature cannot be below absolute zero.");
                                }
                                yield temperature - 273.15;
                            }
                            case FAHRENHEIT -> {
                                if (temperature < -459.67) {
                                    throw new IllegalArgumentException(
                                            "Temperature cannot be below absolute zero.");
                                }
                                yield (temperature - 32) * 5 / 9;
                            }
                        });
    }
}
