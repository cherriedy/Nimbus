package com.optlab.nimbus.utility.convertor;

import com.optlab.nimbus.data.model.Temperature;

public final class TemperatureConvertor {
    private TemperatureConvertor() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }

    /** Convert temperature to Fahrenheit or Kelvin based on the Celsius value. */
    public static double fromCelsius(double temperature, Temperature.Unit unit) {
        return Math.round(
                switch (unit) {
                    case CELSIUS -> temperature;
                    case KELVIN -> {
                        double kelvin = temperature - 273.15;
                        if (kelvin < -273.15) {
                            throw new IllegalArgumentException(
                                    "Temperature cannot be below absolute zero.");
                        }
                        yield kelvin;
                    }
                    case FAHRENHEIT -> {
                        double fahrenheit = (temperature - 32) * 5 / 9;
                        if (fahrenheit < -273.15) {
                            throw new IllegalArgumentException(
                                    "Temperature cannot be below absolute zero.");
                        }
                        yield fahrenheit;
                    }
                });
    }

    /** Convert temperature to Celsius based on the given unit. */
    public static double toCelsius(double temperature, Temperature.Unit unit) {
        return Math.round(
                switch (unit) {
                    case CELSIUS -> temperature;
                    case KELVIN -> {
                        double celsius = temperature + 273.15;
                        if (celsius < -273.15) {
                            throw new IllegalArgumentException(
                                    "Temperature cannot be below absolute zero.");
                        }
                        yield celsius;
                    }
                    case FAHRENHEIT -> {
                        double celsius = (temperature * 9 / 5) + 32;
                        if (celsius < -273.15) {
                            throw new IllegalArgumentException(
                                    "Temperature cannot be below absolute zero.");
                        }
                        yield celsius;
                    }
                });
    }
}
