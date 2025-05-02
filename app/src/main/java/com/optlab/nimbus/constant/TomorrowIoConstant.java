package com.optlab.nimbus.constant;

/** TomorrowIoConstant is a utility class that contains constants used in the Tomorrow.io API. */
public final class TomorrowIoConstant {
    public static final String BASE_URL = "https://api.tomorrow.io/";

    public static final String CURRENT_WEATHER_FIELDS =
            "temperature,temperatureMax,temperatureMin,weatherCode,windSpeed,humidity,pressureSurfaceLevel";
    public static final String HOURLY_WEATHER_FIELDS = "temperature,weatherCode";
    public static final String DAILY_WEATHER_FIELDS =
            "temperature,temperatureMax,temperatureMin,weatherCode,sunriseTime,sunsetTime,windSpeed,humidity,pressureSurfaceLevel";
    public static final String TIMESTEPS_ONE_DAY = "1d"; // 1 day forecast
    public static final String TIMESTEPS_ONE_HOUR = "1h"; // 1 hour forecast
    public static final String TIMESTEPS_CURRENT = "current"; // current forecast
    public static final String PLUS_1_DAYS_FROM_TODAY = "nowPlus1d"; // plus 1 days for forecast
    public static final String PLUS_5_DAYS_FROM_TODAY = "nowPlus5d"; // plus 5 days for forecast
    public static final String METRIC = "metric"; // temperature unit

    /** Private constructor to prevent instantiation of this utility class. */
    private TomorrowIoConstant() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }
}
