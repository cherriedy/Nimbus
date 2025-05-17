package com.optlab.nimbus.data.model;

import lombok.Data;
import lombok.Getter;

@Getter
public class WindSpeed {
    private final double meterPerSecond;
    private final double kilometersPerHour;
    private final double milesPerHour;
    private final double knots;
    private final Unit unit;

    public WindSpeed(double value, Unit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }

        this.unit = unit;
        this.meterPerSecond = toMeterPerSecond(value, unit);
        this.kilometersPerHour =
                unit == Unit.KILOMETERS_PER_HOUR
                        ? value
                        : fromMeterPerSecond(Unit.KILOMETERS_PER_HOUR);
        this.milesPerHour =
                unit == Unit.MILES_PER_HOUR ? value : fromMeterPerSecond(Unit.MILES_PER_HOUR);
        this.knots = unit == Unit.KNOTS ? value : fromMeterPerSecond(Unit.KNOTS);
    }

    private double toMeterPerSecond(double value, Unit unit) {
        if (value < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }
        return Math.round(
                switch (unit) {
                    case METERS_PER_SECOND -> value;
                    case KILOMETERS_PER_HOUR -> value / 3.6;
                    case MILES_PER_HOUR -> value / 2.23694;
                    case KNOTS -> value / 1.94384;
                });
    }

    public double fromMeterPerSecond(WindSpeed.Unit unit) {
        return switch (unit) {
            case METERS_PER_SECOND -> meterPerSecond;
            case KILOMETERS_PER_HOUR -> meterPerSecond * 3.6;
            case MILES_PER_HOUR -> meterPerSecond * 2.23694;
            case KNOTS -> meterPerSecond * 1.94384;
        };
    }

    @Getter
    public enum Unit {
        METERS_PER_SECOND("m/s"),
        KILOMETERS_PER_HOUR("km/h"),
        MILES_PER_HOUR("mph"),
        KNOTS("kn");

        private final String name;

        Unit(String name) {
            this.name = name;
        }
    }
}
