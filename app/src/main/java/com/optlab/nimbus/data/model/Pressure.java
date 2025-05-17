package com.optlab.nimbus.data.model;

import lombok.Data;
import lombok.Getter;

@Data
public class Pressure {
    private final Unit unit;
    private final double hectopascal;
    private final double atmosphere;
    private final double bar;
    private final double pascal;
    private final double torr;

    public Pressure(double value, Unit unit) {
        if (value < 0) {
            throw new IllegalArgumentException("Pressure must be non-negative, was " + value);
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be null");
        }

        this.unit = unit;
        hectopascal = toHectopascal(value, unit);
        atmosphere = unit == Unit.ATMOSPHERE ? value : fromHectopascal();
        bar = unit == Unit.BAR ? value : fromHectopascal();
        pascal = unit == Unit.PASCAL ? value : fromHectopascal();
        torr = unit == Unit.TORR ? value : fromHectopascal();
    }

    public double getValue(Unit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be null");
        }
        return switch (unit) {
            case HECTOPASCAL -> hectopascal;
            case ATMOSPHERE -> atmosphere;
            case BAR -> bar;
            case PASCAL -> pascal;
            case TORR -> torr;
        };
    }

    private double toHectopascal(double value, Unit unit) {
        return switch (unit) {
            case HECTOPASCAL -> value;
            case ATMOSPHERE -> value * 1013.25;
            case BAR -> value * 1000;
            case PASCAL -> value / 100;
            case TORR -> value / 0.750062;
        };
    }

    private double fromHectopascal() {
        return switch (unit) {
            case HECTOPASCAL -> hectopascal;
            case ATMOSPHERE -> hectopascal / 1013.25;
            case BAR -> hectopascal / 1000;
            case PASCAL -> hectopascal * 100;
            case TORR -> hectopascal * 0.750062;
        };
    }

    @Getter
    public enum Unit {
        HECTOPASCAL("hPa"),
        ATMOSPHERE("atm"),
        BAR("bar"),
        PASCAL("Pa"),
        TORR("torr");

        private final String name;

        Unit(String name) {
            this.name = name;
        }
    }
}
