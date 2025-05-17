package com.optlab.nimbus.data.model;

public class Wind {
    private final WindSpeed windSpeed;
    private final Direction direction;

    public Wind(WindSpeed windSpeed, Direction direction) {
        this.windSpeed = windSpeed;
        this.direction = direction;
    }

    public double getSpeedValue(WindSpeed.Unit unit) {
        return switch (unit) {
            case METERS_PER_SECOND -> windSpeed.getMeterPerSecond();
            case KILOMETERS_PER_HOUR -> windSpeed.getKilometersPerHour();
            case MILES_PER_HOUR -> windSpeed.getMilesPerHour();
            case KNOTS -> windSpeed.getKnots();
        };
    }

    public double getDirectionValue(Direction.Unit unit) {
        return switch (unit) {
            case DEGREE -> direction.getDegree();
            case RADIAN -> direction.getRadian();
        };
    }
}
