package com.optlab.nimbus.utility;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.TimeZone;

import lombok.Getter;

public final class DateTimeUtil {
    @Getter public static final ZoneId timeZone = TimeZone.getDefault().toZoneId();

    private DateTimeUtil() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
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
        return localDate.getDayOfWeek().getDisplayName(style, Locale.ENGLISH);
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

    public static String getTimeZoneId() {
        return timeZone.getId();
    }

    /**
     * Get the start of the day in UTC format.
     *
     * @return The start of the day in UTC format (yyyy-MM-dd'T'HH:mm:ssZ).
     */
    public static String getStartOfDay() {
        return LocalDate.now(timeZone) // Returns the date without timezone for specified timezone
                .atStartOfDay(timeZone) // Returns the start of the day in the specified timezone
                .toInstant() // Return the start of the day of the specified timezone to UTC
                .toString(); // Convert to string in ISO format (ISO_INSTANT)

        // Or
        // LocalDate.now(ZoneId.of("UTC"))
        //         .atStartOfDay()
        //         .atZone(ZoneId.of("UTC"))
        //         .format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Get the end of the day in UTC format.
     *
     * @return The end of the day in UTC format (yyyy-MM-dd'T'HH:mm:ssZ).
     */
    public static String getEndOfDay() {
        return LocalDate.now(timeZone) // Returns the date without timezone for specified timezone
                .atTime(23, 59, 59) // Returns the end of the day in the specified timezone
                .atZone(timeZone) // Returns the end of the day in the specified timezone
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
