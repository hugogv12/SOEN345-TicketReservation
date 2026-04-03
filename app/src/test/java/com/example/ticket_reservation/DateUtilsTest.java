package com.example.ticket_reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("DateUtils")
class DateUtilsTest {

    @Test
    @DisplayName("toIsoDate pads month and day")
    void padsComponents() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2026, Calendar.MARCH, 7);
        assertEquals("2026-03-07", DateUtils.toIsoDate(cal));
    }

    @Test
    @DisplayName("toIsoDate handles year boundary")
    void yearMonthDay() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(1999, Calendar.DECEMBER, 31);
        assertEquals("1999-12-31", DateUtils.toIsoDate(cal));
    }
}
