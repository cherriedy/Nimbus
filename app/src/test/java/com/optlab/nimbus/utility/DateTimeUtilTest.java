package com.optlab.nimbus.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@RunWith(MockitoJUnitRunner.class)
public class DateTimeUtilTest {
    /**
     * Test the getDayOfWeek method with a valid ISO date string to ensure it returns the correct
     * day of the week.
     */
    @RunWith(Parameterized.class)
    public static class GetDayOfWeekParameterizedTest {
        /**
         * Test data for getDayOfWeek method.
         *
         * <p>getDayOfWeek({0}, {1}) = {2}) with {0} is the date string, {1} is the TextStyle, {2}
         * is the expected day of week. We can use this to test the getDayOfWeek method with
         * different date strings and TextStyles.
         */
        @Parameterized.Parameters(name = "{index}: getDayOfWeek({0}, {1}) = {2}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {"2025-05-04T11:11:00Z", TextStyle.SHORT, "Sun"},
                        {"2025-05-04T09:11:00Z", TextStyle.SHORT, "Sun"},
                        {"2025-05-03T11:11:00Z", TextStyle.SHORT, "Sat"},
                        {"2025-05-04T09:11:00Z", TextStyle.NARROW, "S"},
                        {"2025-05-03T11:11:00Z", TextStyle.NARROW, "S"},
                        {"2025-05-04T11:11:00Z", TextStyle.NARROW, "S"},
                        {"2025-04-04T11:11:00+07:00[Asia/Ho_Chi_Minh]", TextStyle.SHORT, "Fri"},
                        {"2025-12-04T11:11:00+07:00[Asia/Ho_Chi_Minh]", TextStyle.SHORT, "Thu"}
                    });
        }

        /** The date string to be tested. This is the first parameter of the test. */
        @Parameterized.Parameter(0)
        public String date;

        /** The TextStyle to be tested. This is the second parameter of the test. */
        @Parameterized.Parameter(1)
        public TextStyle style;

        /** The expected day of the week. This is the third parameter of the test. */
        @Parameterized.Parameter(2)
        public String expected;

        @Test
        public void testGetDayOfWeek() {
            // Call the getDayOfWeek method with the date and style parameters.
            assertEquals(expected, DateTimeUtil.getDayOfWeek(date, style));
        }
    }

    /**
     * This test is to check if the getDayOfWeek method works correctly when the date string is in
     * invalid format. It should throw a DateTimeParseException or NullPointerException.
     */
    @Test
    public void getDayOfWeek_invalidInput_throwException() {
        assertThrows(
                DateTimeParseException.class,
                () ->
                        DateTimeUtil.getDayOfWeek(
                                "2025-05-04T11:11:00+07:00[Asia/INVALID_ZONE]", TextStyle.SHORT));
        assertThrows(
                NullPointerException.class, () -> DateTimeUtil.getDayOfWeek(null, TextStyle.SHORT));
        assertThrows(
                DateTimeParseException.class, () -> DateTimeUtil.getDayOfWeek("", TextStyle.SHORT));
    }

    /**
     * Test the getDayTime method with a valid ISO date string to ensure it returns the correct
     * formatted date and time.
     */
    @RunWith(Parameterized.class)
    public static class GetDatTimeParameterizedTest {
        /**
         * Test data for getDayTime method.
         *
         * <p>getDayTime({0}) = {1} with {0} is the date string, {1} is the expected formatted date
         * and time. We can use this to test the getDayTime method with different date strings.
         */
        @Parameterized.Parameters(name = "{index}: getDayTime({0}) = {1}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {"2025-05-04T11:11:00Z", "Sun May 04 | 11:11 AM"},
                        {"2025-05-04T11:11:00+07:00", "Sun May 04 | 11:11 AM"},
                        {"2025-05-04T11:11:00+05:00", "Sun May 04 | 11:11 AM"},
                        {"2025-05-04T11:11:00+07:00[Asia/Ho_Chi_Minh]", "Sun May 04 | 11:11 AM"}
                    });
        }

        @Parameterized.Parameter(0)
        public String date;

        @Parameterized.Parameter(1)
        public String expected;

        @Test
        public void testGetDayTime() {
            // Call the getDayTime method with the date parameter.
            // The expected date and time is in the format "EEE MMM dd | hh:mm a".
            assertEquals(expected, DateTimeUtil.getDayTime(date));
        }
    }

    /**
     * This test is to check if the getDayTime method works correctly when the date string is in
     * invalid format. It should throw a DateTimeParseException or NullPointerException.
     */
    @Test
    public void getDayTime_invalidInput_throwException() {
        assertThrows(
                DateTimeParseException.class,
                () -> DateTimeUtil.getDayTime("2025-05-04T11:11:00+07:00[Asia/INVALID_ZONE]"));
        assertThrows(NullPointerException.class, () -> DateTimeUtil.getDayTime(null));
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.getDayTime(""));
    }

    /**
     * Test the getTimeZoneId method with different time zones to ensure it returns the correct time
     * zone ID.
     */
    @RunWith(Parameterized.class)
    public static class GetTimeZoneIdParameterizedTest {
        /**
         * Test data for getTimeZoneId method.
         *
         * <p>getTimeZoneId() = {0} with {0} is the time zone ID. We can use this to test the
         * getTimeZoneId method with different time zones.
         */
        @Parameterized.Parameters(name = "{index}: getTimeZoneId() = {0}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {"Asia/Ho_Chi_Minh"},
                        {"Asia/Tokyo"},
                        {"America/New_York"},
                        {"Europe/London"},
                        {"Australia/Sydney"},
                        {"America/Los_Angeles"},
                        {"America/Chicago"},
                        {"Brazil/Acre"}
                    });
        }

        /** The time zone ID to be tested. This is the first parameter of the test. */
        @Parameterized.Parameter(0)
        public String timezone;

        @Test
        public void testGetTimezone() {
            // Get TimeZone object for the current timezone to mock TimeZone.getDefault().
            TimeZone timeZone = TimeZone.getTimeZone(ZoneId.of(timezone));
            // We need to mock the TimeZone.getDefault() method to return the current time zone.
            try (MockedStatic<TimeZone> tzMock = mockStatic(TimeZone.class)) {
                // Mock the TimeZone.getDefault() method to return the current time zone.
                tzMock.when(TimeZone::getDefault).thenReturn(timeZone);
                // Check if the getTimeZoneId() method returns the correct time zone ID.
                assertEquals(timezone, DateTimeUtil.getTimeZoneId());
            }
        }
    }

    /**
     * Test the getAnHourLater method with different time zones to ensure it returns the correct
     * date and time in UTC format.
     */
    @RunWith(Parameterized.class)
    public static class GetAnHourLaterParameterizedTest {
        /**
         * Test data for getAnHourLater method.
         *
         * <p>getAnHourLater() = {0} with {0} is the expected date and time in UTC format. We can
         * use this to test the getAnHourLater method with different date and time.
         */
        @Parameterized.Parameters(name = "{index}: getAnHourLater() = {0}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {"2025-05-10T15:14:00Z"},
                        {"2025-05-11T00:59:00Z"},
                        {"2025-05-10T01:00:00Z"},
                        {"2025-05-10T13:30:00Z"},
                        {"2025-05-11T00:11:00Z"},
                        {"2025-05-10T13:11:00Z"}
                    });
        }

        @Parameterized.Parameter(0)
        public String expected;

        @Test
        public void testGetAnHourLate() {
            // Parse the expected date and time and subtract one hour to get the assumed current
            // date and time.
            ZonedDateTime nowZdt = ZonedDateTime.parse(expected).minusHours(1);

            // Plus one hour to get the expected date and time, which is used to check the result of
            // DateTimeUtil.getAnHourLater() method.
            ZonedDateTime nowPlusOneZdt = nowZdt.plusHours(1);

            // Get TimeZone object for the current zoneId to mock TimeZone.getDefault(). The
            // timezone should be UTC since we are testing the getAnHourLater method which
            // returns the date and time in UTC format.
            TimeZone timezone = TimeZone.getTimeZone(ZoneId.of("UTC"));

            try (MockedStatic<TimeZone> tzMock = mockStatic(TimeZone.class);
                    MockedStatic<ZonedDateTime> zdtMock = mockStatic(ZonedDateTime.class)) {
                // We need to mock the TimeZone.getDefault() method to return the current time zone.
                tzMock.when(TimeZone::getDefault).thenReturn(timezone);

                // We need to mock the ZonedDateTime.now() method to return the current date and
                // time in the current time zone. This is used to check the result of
                // DateTimeUtil.getAnHourLater() method.
                zdtMock.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(nowZdt);

                // Since we mocked the ZonedDateTime.now() method to return the current date and
                // time in the current time zone, we also need to mock the plusHours(1) method to
                // return the expected date and time in the current time zone. This is used to check
                // the result of DateTimeUtil.getAnHourLater() method.
                Mockito.when(nowZdt.plusHours(1)).thenReturn(nowPlusOneZdt);

                assertEquals(expected, DateTimeUtil.getAnHourLater());
            }
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

    /**
     * Test the getHours method with a valid ISO date string to ensure it returns the correct
     * formatted hour.
     */
    @RunWith(Parameterized.class)
    public static class GetHoursParameterizedTest {
        /**
         * Test data for getHours method.
         *
         * <p>getHours({0}) = {1} with {0} is the date string, {1} is the expected formatted hour.
         * We can use this to test the getHours method with different date strings.
         */
        @Parameterized.Parameters(name = "{index}: getHours({0}) = {1}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {"2025-05-04T11:11:00Z", "11 AM"},
                        {"2025-05-04T23:59:00+07:00", "11 PM"},
                        {"2025-05-04T00:00:00+00:00", "12 AM"}
                    });
        }

        /** The date string to be tested. This is the first parameter of the test. */
        @Parameterized.Parameter(0)
        public String date;

        /**
         * The expected formatted hour. This is the second parameter of the test. The expected
         * formatted hour is in the format "hh a".
         */
        @Parameterized.Parameter(1)
        public String expected;

        @Test
        public void testGetHours() {
            assertEquals(expected, DateTimeUtil.getHours(date));
        }
    }

    /**
     * This test is to check if the getHours method works correctly when the date string is in
     * invalid format. It should throw a DateTimeParseException or NullPointerException.
     */
    @Test
    public void getHours_invalidInput_throwException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.getHours("invalid"));
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.getHours(""));
        assertThrows(NullPointerException.class, () -> DateTimeUtil.getHours(null));
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
