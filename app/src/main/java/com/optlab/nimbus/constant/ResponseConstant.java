package com.optlab.nimbus.constant;

public final class ResponseConstant {
    /**
     * The expiry time for the hourly weather data. This is set to 24 hours (1 day) in milliseconds.
     */
    public static final long DAILY_EXPIRY_TIME = 1000 * 60 * 60 * (long) 24;

    /**
     * The current expiry time for the weather data. This is set to 1 hour (60 minutes) in
     * milliseconds.
     */
    public static final long CURRENT_EXPIRY_TIME = 1000 * 60 * (long) 60;

    private ResponseConstant() {
        // Prevent instantiation of this class as it is a utility class.
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }
}
