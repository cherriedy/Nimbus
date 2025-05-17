package com.optlab.nimbus.utility.convertor;

import com.optlab.nimbus.data.model.PressureUnit;

/** Utility class for converting pressure values between different units. */
public final class PressureConvertor {
    private PressureConvertor() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }

    /**
     * Convert pressure to hectopascal based on the given unit.
     *
     * @param pressure the pressure value to convert
     * @param unit the unit of the pressure value
     * @return the converted pressure value in hectopascal
     */
    public static double fromHectopascal(double pressure, PressureUnit unit) {
        return switch (unit) {
            case ATMOSPHERE -> pressure / 1013.25;
            case BAR -> pressure / 1000;
            case PASCAL -> pressure * 100;
            case TORR -> pressure * 0.750062;
            default -> pressure;
        };
    }
}
