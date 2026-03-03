package com.example.ticket_reservation;

import java.util.Calendar;
import java.util.Locale;

final class DateUtils {

    private DateUtils() {
    }

    static String toIsoDate(Calendar cal) {
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.US, "%04d-%02d-%02d", y, m, d);
    }
}
