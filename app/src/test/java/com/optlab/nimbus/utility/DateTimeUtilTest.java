package com.optlab.nimbus.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.TimeZone;

@RunWith(MockitoJUnitRunner.class)
public class DateTimeUtilTest {
    private static final String ISO_UTC_DATE_STRING = "2025-05-04T11:11:00Z";
    private static final String ISO_PAST_DATE_STRING =
            "2025-04-04T11:11:00+07:00[Asia/Ho_Chi_Minh]";
    private static final String ISO_FUTURE_DATE_STRING =
            "2025-12-04T11:11:00+07:00[Asia/Ho_Chi_Minh]";
    private static final String INVALID_DATE_STRING =
            "2025-05-04T11:11:00+07:00[Asia/Invalid_Zone]";

    /**
     * Test the getDayOfWeek method with a valid ISO date string to ensure it returns the correct
     * day of the week.
     */
    @Test
    public void getDayOfWeek_with_SHORT_style() {
        String result = DateTimeUtil.getDayOfWeek(ISO_UTC_DATE_STRING, TextStyle.SHORT);
        assertEquals("Sun", result); // Expected short form of Sunday
    }

    /**
     * Test the getDayOfWeek method with a valid ISO date string to ensure it returns the correct
     * day of the week.
     */
    @Test
    public void getDayOfWeek_with_NARROW_style() {
        String result = DateTimeUtil.getDayOfWeek(ISO_UTC_DATE_STRING, TextStyle.NARROW);
        assertEquals("S", result);
    }

    @Test(expected = DateTimeParseException.class)
    public void getDayOfWeek_invalid_date_format() {
        DateTimeUtil.getDayOfWeek(INVALID_DATE_STRING, TextStyle.SHORT);
    }

    @Test(expected = NullPointerException.class)
    public void getDayOfWeek_null_date() {
        DateTimeUtil.getDayOfWeek(null, TextStyle.SHORT);
    }

    @Test(expected = DateTimeParseException.class)
    public void getDayOfWeek_empty_date() {
        DateTimeUtil.getDayOfWeek("", TextStyle.SHORT);
    }

    @Test
    public void getDayOfWeek_past_date() {
        String result = DateTimeUtil.getDayOfWeek(ISO_PAST_DATE_STRING, TextStyle.SHORT);
        assertEquals("Fri", result);
    }

    @Test
    public void getDayOfWeek_future_date() {
        String result = DateTimeUtil.getDayOfWeek(ISO_FUTURE_DATE_STRING, TextStyle.SHORT);
        assertEquals("Thu", result);
    }

    @Test
    public void getDayTime_valid_date() {
        // The format of output is "EEE MMMM dd | hh:mm a"
        String result = DateTimeUtil.getDayTime(ISO_UTC_DATE_STRING);
        assertEquals("Sun May 04 | 11:11 AM", result);
    }

    @Test(expected = DateTimeParseException.class)
    public void getDayTime_invalid_date_format() {
        DateTimeUtil.getDayTime(INVALID_DATE_STRING);
    }

    @Test(expected = NullPointerException.class)
    public void getDayTime_null_date() {
        DateTimeUtil.getDayTime(null);
    }

    @Test(expected = DateTimeParseException.class)
    public void getDayTime_empty_date() {
        DateTimeUtil.getDayTime("");
    }

    @Test
    public void getTimeZoneId() {
        String result = DateTimeUtil.getTimeZoneId();
        // Replace with the expected time zone ID for your test environment
        assertEquals("Asia/Ho_Chi_Minh", result);
    }

    @Test
    public void getAnHourLater() {
        try (MockedStatic<DateTimeUtil> util = mockStatic(DateTimeUtil.class)) {
            // Mock the static method getAnHourLater to return a specific date and time.
            util.when(DateTimeUtil::getAnHourLater).thenReturn("2025-05-04T12:11:00Z");

            String result = DateTimeUtil.getAnHourLater();

            // Verify the result and assert that it matches the expected date and time.
            assertEquals("2025-05-04T12:11:00Z", result);

            // Verify the result does not match an incorrect date and time.
            assertNotEquals("2025-05-05T11:11:00Z", result);
        }
    }

    @Test
    public void getAnHourLaterTomorrow() {
        try (MockedStatic<DateTimeUtil> util = mockStatic(DateTimeUtil.class)) {
            util.when(DateTimeUtil::getAnHourLaterTomorrow).thenReturn("2025-05-05T12:11:00Z");

            String result = DateTimeUtil.getAnHourLaterTomorrow();
            assertEquals("2025-05-05T12:11:00Z", result);
            assertNotEquals("2025-05-05T11:11:00Z", result);
        }
    }

    @Test
    public void getHours_valid_date() {
        try (MockedStatic<DateTimeUtil> util = mockStatic(DateTimeUtil.class)) {
            util.when(() -> DateTimeUtil.getHours(ISO_UTC_DATE_STRING)).thenReturn("11 AM");
            String result = DateTimeUtil.getHours(ISO_UTC_DATE_STRING);
            assertEquals("11 AM", result);
        }
    }

    @Test(expected = DateTimeParseException.class)
    public void getHours_invalid_date_format() {
        DateTimeUtil.getHours(INVALID_DATE_STRING);
    }

    @Test(expected = NullPointerException.class)
    public void getHours_null_date() {
        DateTimeUtil.getHours(null);
    }

    @Test(expected = DateTimeParseException.class)
    public void getHours_empty_date() {
        DateTimeUtil.getHours("");
    }

    @Test
    public void getDayOfWeek_timezone_independence_check() {
        String plus7ZoneDayOfWeek =
                DateTimeUtil.getDayOfWeek("2025-05-04T11:11:00+07:00", TextStyle.SHORT);
        assertEquals("Sun", plus7ZoneDayOfWeek);

        String minus7ZoneDayOfWeek =
                DateTimeUtil.getDayOfWeek("2025-05-04T09:11:00-07:00", TextStyle.SHORT);
        assertEquals("Sun", minus7ZoneDayOfWeek);

        String plus9ZoneDayOfWeek =
                DateTimeUtil.getDayOfWeek("2025-05-04T16:11:00+09:00", TextStyle.SHORT);
        assertEquals("Sun", plus9ZoneDayOfWeek);
    }

    @Test
    public void getDayTime_timezone_independence_check() {
        String utcDayTime = DateTimeUtil.getDayTime("2025-05-04T11:11:00Z");
        assertEquals("Sun May 04 | 11:11 AM", utcDayTime);

        String plus7ZoneDayTime = DateTimeUtil.getDayTime("2025-05-04T11:11:00+07:00");
        assertEquals("Sun May 04 | 11:11 AM", plus7ZoneDayTime);

        String plus5ZoneDayTime = DateTimeUtil.getDayTime("2025-05-04T11:11:00+05:00");
        assertEquals("Sun May 04 | 11:11 AM", plus5ZoneDayTime);
    }

    @Test
    public void getAnHourLater_timezone_independence_check() {
        ZoneId[] zoneIds = {
            ZoneId.of("Asia/Ho_Chi_Minh"),
            ZoneId.of("Asia/Tokyo"),
            ZoneId.of("America/New_York"),
            ZoneId.of("Europe/London"),
            ZoneId.of("Australia/Sydney"),
            ZoneId.of("America/Los_Angeles"),
            ZoneId.of("America/Chicago"),
            ZoneId.of("Brazil/Acre")
        };

        // For each time zone, set up the mock behavior for TimeZone and ZonedDateTime.
        for (ZoneId zoneId : zoneIds) {
            // Get TimeZone object for the current zoneId to mock TimeZone.getDefault()
            TimeZone timeZone = TimeZone.getTimeZone(zoneId);

            // Assume the current date is 2025-05-04T11:11:00 in the current time zone.
            ZonedDateTime now = ZonedDateTime.of(2025, 5, 4, 11, 11, 0, 0, zoneId);

            // Assume the one hours later date is 2025-05-04T12:11:00 in the current time zone. This
            // is used to mock the behavior of now.plusHours(1).
            ZonedDateTime nowPlusOne = now.plusHours(1);

            // Assume the expected date is 2025-05-04T12:11:00Z in UTC. This is used to check the
            // result of DateTimeUtil.getAnHourLater() method.
            ZonedDateTime expected = now.plusHours(1).withZoneSameInstant(ZoneId.of("UTC"));

            // We need to mock the TimeZone.getDefault() method to return the current time zone.
            // We also need to mock the ZonedDateTime.now() method to return the current date and
            // time in the current time zone. This is used to check the result of
            // DateTimeUtil.getAnHourLater() method.
            try (MockedStatic<TimeZone> tzMock = mockStatic(TimeZone.class);
                    MockedStatic<ZonedDateTime> zdtMock = mockStatic(ZonedDateTime.class)) {
                tzMock.when(TimeZone::getDefault).thenReturn(timeZone);

                // This will cause the NPE problem since every time we call ZonedDateTime.now(),
                // it will return a new instance of ZonedDateTime. The new problem will not match
                // the mock instance we created above. That is why we need to use any(ZoneId.class).
                // zdtMock.when(() -> ZonedDateTime.now(zoneId)).thenReturn(now);

                // We use any(ZoneId.class) in the mock for ZonedDateTime.now() because
                // DateTimeUtil.getTimeZone() returns a new ZoneId instance each time, and Mockito
                // matches mocks by object identity, not value. Using any(ZoneId.class) ensures the
                // mock is used for all ZoneId instances.
                zdtMock.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(now);

                // Since we mocked the ZonedDateTime.now() method to return the current date and
                // time in the current time zone, we also need to mock the plusHours(1) method to
                // return the expected date and time in the current time zone. This is used to check
                // the result of DateTimeUtil.getAnHourLater() method.
                Mockito.when(now.plusHours(1)).thenReturn(nowPlusOne);

                String result = DateTimeUtil.getAnHourLater();
                assertEquals(expected.toInstant().toString(), result);
            }
        }
    }

    @Test
    public void getAnHourLaterTomorrow_timezone_independence_check() {
        ZoneId[] zoneIds = {
            ZoneId.of("Asia/Ho_Chi_Minh"),
            ZoneId.of("Asia/Tokyo"),
            ZoneId.of("America/New_York"),
            ZoneId.of("Europe/London"),
            ZoneId.of("Australia/Sydney"),
            ZoneId.of("America/Los_Angeles"),
            ZoneId.of("America/Chicago"),
            ZoneId.of("Brazil/Acre")
        };

        for (ZoneId zoneId : zoneIds) {
            // Assume the current date is 2025-05-10T11:11:00 in the current time zone.
            ZonedDateTime now = ZonedDateTime.of(2025, 5, 10, 11, 11, 0, 0, zoneId);

            // Assume the one hours later date is 2025-05-10T12:11:00 in the current time zone. This
            // is used to mock the behavior of now.plusHours(1).
            ZonedDateTime nowPlusOneHours = now.plusHours(1);

            // Assume the one hours later tomorrow date is 2025-05-11T12:11:00 in the current time
            // zone. This is used to mock the behavior of now.plusHours(1).plusDays(1).
            ZonedDateTime nowPlusOneHoursOneDay = nowPlusOneHours.plusDays(1);

            // Assume the expected date is 2025-05-11T12:11:00Z in UTC. This is used to check the
            // result of DateTimeUtil.getAnHourLaterTomorrow() method.
            ZonedDateTime expected =
                    now.plusHours(1).plusDays(1).withZoneSameInstant(ZoneId.of("UTC"));

            // Get TimeZone object for the current zoneId to mock TimeZone.getDefault()
            TimeZone timeZone = TimeZone.getTimeZone(zoneId);

            try (MockedStatic<TimeZone> tzMock = mockStatic(TimeZone.class);
                    MockedStatic<ZonedDateTime> zdtMock = mockStatic(ZonedDateTime.class)) {
                // We need to mock the TimeZone.getDefault() method to return the current time zone.
                tzMock.when(TimeZone::getDefault).thenReturn(timeZone);

                // We use any(ZoneId.class) in the mock for ZonedDateTime.now() because
                // DateTimeUtil.getTimeZone() returns a new ZoneId instance each time, and Mockito
                // matches mocks by object identity, not value. Using any(ZoneId.class) ensures the
                // mock is used for all ZoneId instances.
                zdtMock.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(now);

                // Since we mocked the ZonedDateTime.now() method to return the current date and
                // time in the current time zone, we also need to mock the plusHours(1) method to
                // return the expected date and time in the current time zone. This is used to check
                // the result of DateTimeUtil.getAnHourLater() method.
                Mockito.when(now.plusHours(1)).thenReturn(nowPlusOneHours);

                // We also need to mock the plusDays(1) method to return the expected date and time
                // in the current time zone. This is used to check the result of
                // DateTimeUtil.getAnHourLaterTomorrow() method. This is because the plusDays(1)
                // method will return a new instance of ZonedDateTime each time it is called, and we
                // need to mock the behavior to return the expected date and time in the current
                // time zone.
                Mockito.when(now.plusHours(1).plusDays(1)).thenReturn(nowPlusOneHoursOneDay);

                String result = DateTimeUtil.getAnHourLaterTomorrow();
                String expectedResult = expected.toInstant().toString();
                assertEquals(expectedResult, result);
            }
        }
    }

    /**
     * Test the getHours method with a valid ISO date string to ensure it returns the correct
     * formatted hour.
     */
    @Test
    public void getHours_timezone_independence_check() {
        ZonedDateTime[] zonedDateTimes = {
            ZonedDateTime.of(2025, 5, 10, 23, 40, 0, 0, ZoneId.of("Asia/Ho_Chi_Minh")),
            ZonedDateTime.of(2024, 6, 24, 23, 18, 0, 0, ZoneId.of("Asia/Tokyo")),
            ZonedDateTime.of(2026, 10, 16, 23, 23, 0, 0, ZoneId.of("America/New_York")),
            ZonedDateTime.of(2023, 12, 31, 23, 28, 0, 0, ZoneId.of("Europe/London")),
            ZonedDateTime.of(2018, 1, 11, 22, 17, 0, 0, ZoneId.of("Australia/Sydney")),
            ZonedDateTime.of(2005, 2, 22, 23, 21, 0, 0, ZoneId.of("America/Los_Angeles")),
            ZonedDateTime.of(2016, 3, 30, 21, 11, 2, 0, ZoneId.of("America/Chicago")),
            // Additional edge cases
            ZonedDateTime.of(2025, 3, 9, 1, 30, 0, 0, ZoneId.of("America/New_York")), // DST spring
            ZonedDateTime.of(2025, 11, 2, 1, 30, 0, 0, ZoneId.of("America/New_York")), // DST fall
            ZonedDateTime.of(2024, 2, 29, 23, 45, 0, 0, ZoneId.of("UTC")), // Leap year
            ZonedDateTime.of(1970, 1, 1, 23, 59, 59, 0, ZoneId.of("UTC")), // Unix epoch
            ZonedDateTime.of(3000, 12, 31, 23, 59, 0, 0, ZoneId.of("Europe/Paris")), // Future
            ZonedDateTime.of(1800, 1, 1, 23, 0, 0, 0, ZoneId.of("Europe/Berlin")), // Historical
        };

        for (ZonedDateTime zdt : zonedDateTimes) {
            // Assume the expected date is 2025-05-10T14:11:00Z in UTC. This is used to check the
            // result of DateTimeUtil.getHours() method.
            ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of("UTC"));

            // Format the expected date to match the output format of getHours method.
            String expected = utc.format(DateTimeFormatter.ofPattern("h a"));
            // Get the result from the getHours method in DateTimeUtil class.
            String result = DateTimeUtil.getHours(utc.toString());
            assertEquals(expected, result);
        }
    }

    /**
     * This test is to check if the getAnHourLater method works correctly when the current time is
     * almost end of the day. It should return the correct date and time in UTC format.
     */
    @Test
    public void getAnHourLater_negative_check() {
        ZonedDateTime[] zonedDateTimes = {
            ZonedDateTime.of(2025, 5, 10, 23, 40, 0, 0, ZoneId.of("Asia/Ho_Chi_Minh")),
            ZonedDateTime.of(2024, 6, 24, 23, 18, 0, 0, ZoneId.of("Asia/Tokyo")),
            ZonedDateTime.of(2026, 10, 16, 23, 23, 0, 0, ZoneId.of("America/New_York")),
            ZonedDateTime.of(2023, 12, 31, 23, 28, 0, 0, ZoneId.of("Europe/London")),
            ZonedDateTime.of(2018, 1, 11, 22, 17, 0, 0, ZoneId.of("Australia/Sydney")),
            ZonedDateTime.of(2005, 2, 22, 23, 21, 0, 0, ZoneId.of("America/Los_Angeles")),
            ZonedDateTime.of(2016, 3, 30, 21, 11, 2, 0, ZoneId.of("America/Chicago")),
            // Additional edge cases
            ZonedDateTime.of(2025, 3, 9, 1, 30, 0, 0, ZoneId.of("America/New_York")), // DST spring
            ZonedDateTime.of(2025, 11, 2, 1, 30, 0, 0, ZoneId.of("America/New_York")), // DST fall
            ZonedDateTime.of(2024, 2, 29, 23, 45, 0, 0, ZoneId.of("UTC")), // Leap year
            ZonedDateTime.of(1970, 1, 1, 23, 59, 59, 0, ZoneId.of("UTC")), // Unix epoch
            ZonedDateTime.of(3000, 12, 31, 23, 59, 0, 0, ZoneId.of("Europe/Paris")), // Future
            ZonedDateTime.of(1800, 1, 1, 23, 0, 0, 0, ZoneId.of("Europe/Berlin")), // Historical
        };

        for (ZonedDateTime now : zonedDateTimes) {
            // Get TimeZone object for the current zoneId to mock TimeZone.getDefault().
            TimeZone timeZone = TimeZone.getTimeZone(now.getZone());

            // Assume the one hours later date is 2025-05-10T12:11:00 in the current time zone. This
            // is used to mock the behavior of now.plusHours(1).
            ZonedDateTime nowPlusOne = now.plusHours(1);

            // Assume the expected date is 2025-05-10T12:11:00Z in UTC. This is used to check the
            // result of DateTimeUtil.getAnHourLater() method.
            ZonedDateTime expected = now.plusHours(1).withZoneSameInstant(ZoneId.of("UTC"));

            try (MockedStatic<TimeZone> tzMock = mockStatic(TimeZone.class);
                    MockedStatic<ZonedDateTime> zdtMock = mockStatic(ZonedDateTime.class)) {
                // We need to mock the TimeZone.getDefault() method to return the current time zone.
                tzMock.when(TimeZone::getDefault).thenReturn(timeZone);

                // We use any(ZoneId.class) in the mock for ZonedDateTime.now() because
                // DateTimeUtil.getTimeZone() returns a new ZoneId instance each time, and Mockito
                // matches mocks by object identity, not value. Using any(ZoneId.class) ensures the
                // mock is used for all ZoneId instances.
                zdtMock.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(now);

                // Since we mocked the ZonedDateTime.now() method to return the current date and
                // time in the current time zone, we also need to mock the plusHours(1) method to
                // return the expected date and time in the current time zone. This is used to check
                // the result of DateTimeUtil.getAnHourLater() method.
                Mockito.when(now.plusHours(1)).thenReturn(nowPlusOne);

                String expectedResult = expected.toInstant().toString();
                String result = DateTimeUtil.getAnHourLater();
                assertEquals(expectedResult, result);
            }
        }
    }

    @Test
    public void getAnHourLaterTomorrow_negative_check() {
        ZonedDateTime[] zonedDateTimes = {
            ZonedDateTime.of(2025, 5, 10, 23, 40, 0, 0, ZoneId.of("Asia/Ho_Chi_Minh")),
            ZonedDateTime.of(2024, 6, 24, 23, 18, 0, 0, ZoneId.of("Asia/Tokyo")),
            ZonedDateTime.of(2026, 10, 16, 23, 23, 0, 0, ZoneId.of("America/New_York")),
            ZonedDateTime.of(2023, 12, 31, 23, 28, 0, 0, ZoneId.of("Europe/London")),
            ZonedDateTime.of(2018, 1, 11, 22, 17, 0, 0, ZoneId.of("Australia/Sydney")),
            ZonedDateTime.of(2005, 2, 22, 23, 21, 0, 0, ZoneId.of("America/Los_Angeles")),
            ZonedDateTime.of(2016, 3, 30, 21, 11, 2, 0, ZoneId.of("America/Chicago")),
            // Additional edge cases
            ZonedDateTime.of(2025, 3, 9, 1, 30, 0, 0, ZoneId.of("America/New_York")), // DST spring
            ZonedDateTime.of(2025, 11, 2, 1, 30, 0, 0, ZoneId.of("America/New_York")), // DST fall
            ZonedDateTime.of(2024, 2, 29, 23, 45, 0, 0, ZoneId.of("UTC")), // Leap year
            ZonedDateTime.of(1970, 1, 1, 23, 59, 59, 0, ZoneId.of("UTC")), // Unix epoch
            ZonedDateTime.of(3000, 12, 31, 23, 59, 0, 0, ZoneId.of("Europe/Paris")), // Future
            ZonedDateTime.of(1800, 1, 1, 23, 0, 0, 0, ZoneId.of("Europe/Berlin")), // Historical
        };

        for (ZonedDateTime now : zonedDateTimes) {
            // Get TimeZone object for the current zoneId to mock TimeZone.getDefault().
            TimeZone timeZone = TimeZone.getTimeZone(now.getZone());

            // Assume the one hours later date is 2025-05-10T12:11:00 in the current time zone. This
            // is used to mock the behavior of now.plusHours(1).
            ZonedDateTime nowPlusOne = now.plusHours(1);

            // Assume the one hours later tomorrow date is 2025-05-11T12:11:00 in the current time
            // zone. This is used to mock the behavior of now.plusHours(1).plusDays(1).
            ZonedDateTime nowPlusOneHoursOneDay = nowPlusOne.plusDays(1);

            // Assume the expected date is 2025-05-10T12:11:00Z in UTC. This is used to check the
            // result of DateTimeUtil.getAnHourLater() method.
            ZonedDateTime expected =
                    now.plusHours(1).plusDays(1).withZoneSameInstant(ZoneId.of("UTC"));

            try (MockedStatic<TimeZone> tzMock = mockStatic(TimeZone.class);
                    MockedStatic<ZonedDateTime> zdtMock = mockStatic(ZonedDateTime.class)) {
                // We need to mock the TimeZone.getDefault() method to return the current time zone.
                tzMock.when(TimeZone::getDefault).thenReturn(timeZone);

                // We use any(ZoneId.class) in the mock for ZonedDateTime.now() because
                // DateTimeUtil.getTimeZone() returns a new ZoneId instance each time, and Mockito
                // matches mocks by object identity, not value. Using any(ZoneId.class) ensures the
                // mock is used for all ZoneId instances.
                zdtMock.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(now);

                // Since we mocked the ZonedDateTime.now() method to return the current date and
                // time in the current time zone, we also need to mock the plusHours(1) method to
                // return the expected date and time in the current time zone. This is used to check
                // the result of DateTimeUtil.getAnHourLater() method.
                Mockito.when(now.plusHours(1)).thenReturn(nowPlusOne);

                // We also need to mock the plusDays(1) method to return the expected date and time
                // in the current time zone. This is used to check the result of
                // DateTimeUtil.getAnHourLaterTomorrow() method. This is because the plusDays(1)
                // method will return a new instance of ZonedDateTime each time it is called, and we
                // need to mock the behavior to return the expected date and time in the current
                // time zone.
                Mockito.when(now.plusHours(1).plusDays(1)).thenReturn(nowPlusOneHoursOneDay);

                String expectedResult = expected.toInstant().toString();
                String result = DateTimeUtil.getAnHourLaterTomorrow();
                assertEquals(expectedResult, result);
            }
        }
    }

    /**
     * Test the getHours method with a valid ISO date string to ensure it returns the correct when
     * the date time is almost end of the day.
     */
    @Test
    public void getHours_negative_check() {
        ZonedDateTime[] zonedDateTimes = {
            ZonedDateTime.of(2025, 5, 10, 23, 40, 0, 0, ZoneId.of("Asia/Ho_Chi_Minh")),
            ZonedDateTime.of(2024, 6, 24, 23, 18, 0, 0, ZoneId.of("Asia/Tokyo")),
            ZonedDateTime.of(2026, 10, 16, 23, 23, 0, 0, ZoneId.of("America/New_York")),
            ZonedDateTime.of(2023, 12, 31, 23, 28, 0, 0, ZoneId.of("Europe/London")),
            ZonedDateTime.of(2018, 1, 11, 22, 17, 0, 0, ZoneId.of("Australia/Sydney")),
            ZonedDateTime.of(2005, 2, 22, 23, 21, 0, 0, ZoneId.of("America/Los_Angeles")),
            ZonedDateTime.of(2016, 3, 30, 21, 11, 2, 0, ZoneId.of("America/Chicago")),
            // Additional edge cases
            ZonedDateTime.of(2025, 3, 9, 1, 30, 0, 0, ZoneId.of("America/New_York")), // DST spring
            ZonedDateTime.of(2025, 11, 2, 1, 30, 0, 0, ZoneId.of("America/New_York")), // DST fall
            ZonedDateTime.of(2024, 2, 29, 23, 45, 0, 0, ZoneId.of("UTC")), // Leap year
            ZonedDateTime.of(1970, 1, 1, 23, 59, 59, 0, ZoneId.of("UTC")),
            ZonedDateTime.of(3000, 12, 31, 23, 59, 0, 0, ZoneId.of("Europe/Paris")),
            ZonedDateTime.of(1800, 1, 1, 23, 0, 0, 0, ZoneId.of("Europe/Berlin")),
        };

        for (ZonedDateTime now : zonedDateTimes) {
            // Expected date in UTC format.
            String expected =
                    now.format(
                            DateTimeFormatter.ofPattern("h a")
                                    .withLocale(Locale.getDefault())
                                    .withZone(ZoneId.of("UTC")));

            // Convert the ZonedDateTime to UTC format.
            ZonedDateTime utc = now.withZoneSameInstant(ZoneId.of("UTC"));

            // Get the result from the getHours method in DateTimeUtil class.
            String result = DateTimeUtil.getHours(utc.toString());

            assertEquals(expected, result);
        }
    }
}
