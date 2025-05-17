package com.optlab.nimbus.data.model;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import lombok.Getter;

@Getter
public class Temperature implements Comparable<Temperature> {
    private final double celsius;
    private final double fahrenheit;
    private final double kelvin;
    private final Unit unit;

    public Temperature(double value, Unit unit) {
        this.unit = unit;
        this.celsius = toCelsius(value, unit);
        this.fahrenheit = unit == Unit.FAHRENHEIT ? value : fromCelsius(Unit.FAHRENHEIT);
        this.kelvin = unit == Unit.KELVIN ? value : fromCelsius(Unit.KELVIN);
    }

    public double getValue(Unit unit) {
        return switch (unit) {
            case CELSIUS -> celsius;
            case KELVIN -> kelvin;
            case FAHRENHEIT -> fahrenheit;
        };
    }

    private double toCelsius(double value, Unit unit) {
        return Math.round(
                switch (unit) {
                    case CELSIUS -> value;
                    case KELVIN -> {
                        if (value < 0) {
                            throw new IllegalArgumentException(
                                    "Kelvin temperature cannot be negative.");
                        }
                        yield value + 273.15;
                    }
                    case FAHRENHEIT -> {
                        if (value < -459.67) {
                            throw new IllegalArgumentException(
                                    "Fahrenheit temperature cannot be below absolute zero.");
                        }
                        yield (value * 9 / 5) + 32;
                    }
                });
    }

    private double fromCelsius(Unit unit) {
        if (celsius < -273.15 || celsius > 1000.0) {
            throw new IllegalArgumentException(
                    "Celsius must be above absolute zero, was " + celsius);
        }
        return Math.round(
                switch (unit) {
                    case CELSIUS -> celsius;
                    case KELVIN -> celsius - 273.15;
                    case FAHRENHEIT -> (celsius - 32) * 5 / 9;
                });
    }

    @Override
    public int compareTo(Temperature that) {
        return Double.compare(this.celsius, that.celsius);
    }

    @NonNull
    @Override
    @SuppressLint("DefaultLocale")
    public String toString() {
        return String.format("%.2f °C / %.2f °F / %.2f K", celsius, fahrenheit, kelvin);
    }

    @Getter
    public enum Unit {
        CELSIUS("°C"),
        FAHRENHEIT("°F"),
        KELVIN("K");

        private final String symbol;

        Unit(String symbol) {
            this.symbol = symbol;
        }
    }
}
