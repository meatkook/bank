package org.clever_bank.services;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * This class provides utility methods for working with dates and creating date objects.
 */
public class DateConstructor {
    /**
     * Creates an Instant date object based on the provided year, month, and day.
     *
     * @param year  the year of the date
     * @param month the month of the date
     * @param day   the day of the date
     * @return the Instant date object
     */
    public static Instant createInstantDate(int year, int month, int day) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        if (year < 1970) {
            ZonedDateTime zonedDateTime = LocalDate.of(1970, 1, 1).atStartOfDay(ZoneOffset.UTC);
            return zonedDateTime.toInstant();
        } else if (year > currentYear) {
            year = currentYear;
        }

        if (month > 12) {
            month = 12;
        } else if (month < 1) {
            month = 1;
        }

        LocalDate userDate = LocalDate.of(year, month, 1);
        Month userMonth = userDate.getMonth();
        int daysInMonth = userMonth.length(userDate.isLeapYear());
        if (day < 1) {
            day = 1;
        } else if (day > daysInMonth) {
            day = daysInMonth;
        }

        userDate = LocalDate.of(year, month, day);
        if (userDate.isAfter(currentDate)) {
            userDate = currentDate;
        }

        ZonedDateTime zonedDateTime = userDate.atStartOfDay(ZoneOffset.UTC);

        return zonedDateTime.toInstant();
    }

    /**
     * Creates an Instant date object based on the provided Date object.
     *
     * @param userDate the Date object representing the date
     * @return the Instant date object
     */
    public static Instant createInstantDate(Date userDate) {
        String[] date = userDate.toString().split("-");

        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);

        return createInstantDate(year, month, day);
    }

    /**
     * Returns an Instant object representing the date one month ago from the current date.
     *
     * @return the Instant object representing the date one month ago
     */
    public static Instant getPreviousMonthInstant() {
        Instant currentDate = Instant.now();
        ZonedDateTime zonedDateTime = currentDate.atZone(ZoneId.systemDefault());
        ZonedDateTime oneMonthAgo = zonedDateTime.minus(1, ChronoUnit.MONTHS);
        return oneMonthAgo.toInstant();
    }

    /**
     * Returns an Instant object representing the date one year ago from the current date.
     *
     * @return the Instant object representing the date one year ago
     */
    public static Instant getPreviousYearInstant() {
        Instant currentDate = Instant.now();
        ZonedDateTime zonedDateTime = currentDate.atZone(ZoneId.systemDefault());
        ZonedDateTime oneMonthAgo = zonedDateTime.minus(1, ChronoUnit.YEARS);
        return oneMonthAgo.toInstant();
    }

    /**
     * Creates a java.sql.Date object based on the provided Date object.
     *
     * @param userDate the Date object representing the date
     * @return the java.sql.Date object
     */
    public static java.sql.Date createSqlDate(Date userDate) {
        String[] date = userDate.toString().split("-");

        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        return new java.sql.Date(calendar.getTimeInMillis());
    }
}
