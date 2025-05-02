package com.optlab.nimbus.utility.convertor;

import com.optlab.nimbus.data.model.common.WindSpeedUnit;

public final class WindSpeedConvertor {
    private WindSpeedConvertor() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }

    public static double fromMeterPerSecond(double speed, WindSpeedUnit unit) {
        return switch (unit) {
            case KILOMETERS_PER_HOUR -> speed * 3.6;
            case MILES_PER_HOUR -> speed * 2.23694;
            case KNOTS -> speed * 1.94384;
            default -> speed;
        };
    }
}
