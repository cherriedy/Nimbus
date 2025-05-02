package com.optlab.nimbus.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtilTest {

    @Test
    public void getDayOfWeek_valid_date() {
        String date = ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        String result = DateTimeUtil.getDayOfWeek(date);
        assertEquals("FRIDAY", result);
    }

    @Test
    public void getDayOfWeek_invalid_date_format() {}

    @Test
    public void getDayOfWeek_null_date() {}

    @Test
    public void getDayOfWeek_empty_date() {}

    @Test
    public void getDayOfWeek_leap_year_date() {}

    @Test
    public void getDayOfWeek_invalid_date() {}

    @Test
    public void getDayTime_valid_date_time() {}

    @Test
    public void getDayTime_invalid_date_time_format() {}

    @Test
    public void getDayTime_null_date_time() {}

    @Test
    public void getDayTime_empty_date_time() {}

    @Test
    public void getDayTime_invalid_values() {}

    @Test
    public void getStartOfDay_current_time_check() {
        String result = DateTimeUtil.getStartOfDay();
        String assumption =
                LocalDate.now()
                        .atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .format(DateTimeFormatter.ISO_DATE_TIME);
        System.out.println(assumption);
        assertEquals(assumption, result);
    }

    @Test
    public void getStartOfDay_Timezone_check() {}

    @Test
    public void getStartOfDay_empty_date() {}

    @Test
    public void getDayOfWeek_with_spaces() {}

    @Test
    public void getDayTime_with_spaces() {}
}
