package com.optlab.nimbus.constant;

public final class ResponseConstant {
    public static final long DAILY_EXPIRY_TIME =
            1000 * 60 * 60 * (long) 24; // 1 day in milliseconds (24 hours)
    public static final long CURRENT_EXPIRY_TIME =
            1000 * 60 * (long) 60; // 1 hour in milliseconds (60 minutes)

    private ResponseConstant() {
        // Prevent instantiation of this class as it is a utility class.
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }
}
