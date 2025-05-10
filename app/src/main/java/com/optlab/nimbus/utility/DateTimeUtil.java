package com.optlab.nimbus.utility;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class for date and time operations. This class provides methods to manipulate and format
 * date and time strings. It includes methods to get the current date and time, format dates, and
 * retrieve specific components of a date.
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }

    /**
     * Get the current ZoneId.
     *
     * @return The current ZoneId based on the system's default time zone.
     */
    public static ZoneId getTimeZone() {
        return TimeZone.getDefault().toZoneId();
    }

    /**
     * Get the day of the week from a date string.
     *
     * @param date The date string in ISO format (yyyy-MM-dd'T'HH:mm:ssZ).
     * @param style The style of the day of the week (e.g., FULL, SHORT).
     * @return The day of the week as a string (e.g., "MONDAY", "Mon").
     */
    public static String getDayOfWeek(@NonNull String date, TextStyle style) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        return localDate.getDayOfWeek().getDisplayName(style, Locale.getDefault());
    }

    /**
     * Get the day and time from a date string.
     *
     * @param date The date string in ISO format (yyyy-MM-dd'T'HH:mm:ssZ).
     * @return The formatted date and time as a string (e.g., "MONDAY, 12:00PM").
     */
    public static String getDayTime(@NonNull String date) {
        ZonedDateTime zonedDateTime =
                ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        return zonedDateTime.format(DateTimeFormatter.ofPattern("EEE MMMM dd | hh:mm a"));
    }

    /**
     * Get the current time zone ID.
     *
     * @return The current time zone ID (e.g., "America/New_York").
     */
    public static String getTimeZoneId() {
        return getTimeZone().getId();
    }

    /**
     * Get the start of the day in UTC format.
     *
     * @return The start of the day in UTC format (yyyy-MM-dd'T'HH:mm:ssZ).
     */
    @NonNull
    public static String getStartOfDay() {
        return LocalDate.now(
                        getTimeZone()) // Returns the date without timezone for specified timezone
                .atStartOfDay(
                        getTimeZone()) // Returns the start of the day in the specified timezone
                .toInstant() // Return the start of the day of the specified timezone to UTC
                .toString(); // Convert to string in ISO format (ISO_INSTANT)

        // Or you can use the following line to get the start of the day in UTC:
        // return LocalDate.now(ZoneId.of("UTC"))
        //         .atStartOfDay()
        //         .atZone(ZoneId.of("UTC"))
        //         .format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Get the current date and time in UTC format.
     *
     * @return The current date and time in UTC format (yyyy-MM-dd'T'HH:mm:ssZ).
     */
    @NonNull
    public static String getAnHourLater() {
        return ZonedDateTime.now(getTimeZone()) // Returns the date without timezone
                .plusHours(1) // Adds an hour to the current date and time
                .toInstant() // Converts the date and time to UTC
                .toString(); // Converts to string in ISO format (ISO_INSTANT)
    }

    /**
     * Get the date and time one hour later tomorrow in UTC format.
     *
     * @return The date and time one hour later tomorrow in UTC format (yyyy-MM-dd'T'HH:mm:ssZ).
     */
    @NonNull
    public static String getAnHourLaterTomorrow() {
        return ZonedDateTime.now(getTimeZone())
                .plusHours(1) // Adds an hour to the current date and time
                .plusDays(1) // Adds a day to the current date and time
                .toInstant() // Converts the date and time to UTC
                .toString(); // Converts to string in ISO format (ISO_INSTANT)
    }

    /**
     * Get the end of the day in UTC format.
     *
     * @return The end of the day in UTC format (yyyy-MM-dd'T'HH:mm:ssZ).
     */
    @NonNull
    public static String getEndOfDay() {
        return LocalDate.now(
                        getTimeZone()) // Returns the date without timezone for specified timezone
                .atTime(23, 59, 59) // Returns the end of the day in the specified timezone
                .atZone(getTimeZone()) // Returns the end of the day in the specified timezone
                .toInstant() // Return the end of the day of the specified timezone to UTC
                .toString(); // Convert to string in ISO format (ISO_INSTANT)
    }

    /**
     * Get the hours from a date string, formatted as "h a" (e.g., "12 PM").
     *
     * @param dateTime The date string in ISO format (yyyy-MM-dd'T'HH:mm:ssZ).
     * @return The formatted hours as a string (e.g., "12 PM").
     */
    public static String getHours(@NonNull String dateTime) {
        return ZonedDateTime.parse(dateTime).format(DateTimeFormatter.ofPattern("h a"));
    }
}
