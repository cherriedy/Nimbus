package com.optlab.nimbus.data.model;

import lombok.Getter;

@Getter
public class Direction {
    private final double degree;
    private final double radian;
    private final Unit unit;

    public Direction(double value, Unit unit) {
        this.unit = unit;
        degree = unit == Unit.DEGREE ? value : Math.toDegrees(value);
        radian = unit == Unit.RADIAN ? value : Math.toRadians(value);
    }

    public enum Unit {
        DEGREE,
        RADIAN
    }
}
