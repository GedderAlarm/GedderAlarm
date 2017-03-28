/*
 * USER: mslm
 * DATE: 3/28/17
 */

package com.gedder.gedderalarm.util;

import java.util.Calendar;

/**
 * Contains general time-related utilities.
 */
public final class TimeUtilities {
    private static final String TAG = TimeUtilities.class.getSimpleName();

    private static final int HOUR_PER_DAY = 24;

    private static final int MINUTE_PER_HOUR = 60;
    private static final int MINUTE_PER_DAY = HOUR_PER_DAY*MINUTE_PER_HOUR;

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE*SECONDS_PER_MINUTE;
    private static final int SECONDS_PER_DAY = MINUTE_PER_DAY*SECONDS_PER_MINUTE;

    private static final long MILLIS_PER_SECOND = 1000;
    private static final long MILLIS_PER_MINUTE = SECONDS_PER_MINUTE*MILLIS_PER_SECOND;
    private static final long MILLIS_PER_HOUR = SECONDS_PER_HOUR*MILLIS_PER_SECOND;
    private static final long MILLIS_PER_DAY = SECONDS_PER_DAY*MILLIS_PER_SECOND;

    private TimeUtilities() {}

    /**
     * Gets the time until some specified day, hour, and minute combination, from today.
     * @param day       A day number where 1 = Sunday, 2 = Monday, ..., 7 = Saturday.
     * @param hour      An hour between 0 and 23 for the 24-hour clock.
     * @param minute    A minute between 0 and 59.
     * @return The milliseconds from now until the specified input.
     */
    public static long getMillisUntil(int day, int hour, int minute) {
        if (day < 1 || day > 7 || hour < 0 || hour > 23 || minute < 0 || minute > 59)
            throw new IllegalArgumentException();

        Calendar calendar = Calendar.getInstance();
        long now = calendar.get(Calendar.DAY_OF_WEEK)*MILLIS_PER_DAY
                 + calendar.get(Calendar.HOUR)*MILLIS_PER_HOUR
                 + calendar.get(Calendar.MINUTE)*MILLIS_PER_MINUTE;
        long then = day*MILLIS_PER_DAY
                  + hour*MILLIS_PER_HOUR
                  + minute*MILLIS_PER_MINUTE;
        return then - now;
    }

    public static long getMillisUntil(Calendar future) {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.get(Calendar.DAY_OF_WEEK)*MILLIS_PER_DAY
                 + calendar.get(Calendar.HOUR)*MILLIS_PER_HOUR
                 + calendar.get(Calendar.MINUTE)*MILLIS_PER_MINUTE;
        long then = future.get(Calendar.DAY_OF_WEEK)*MILLIS_PER_DAY
                  + future.get(Calendar.HOUR)*MILLIS_PER_HOUR
                  + future.get(Calendar.MINUTE)*MILLIS_PER_MINUTE;
        return then - now;
    }

    /**
     * Gets the number of milliseconds one can count in the hour and minute inputs. Equivalent to
     * counting up to that time since midnight.
     * @param hour
     * @param minute
     * @return The amount of milliseconds in the hour and minute input.
     */
    public static long getMillis(int hour, int minute) {
        return getMillisSinceMidnight(hour, minute);
    }

    /**
     * Gets the time since midnight up to hour:minute.
     * @param hour      The hour to count up to since midnight.
     * @param minute    The minute to count up to since midnight.
     * @return The time since midnight up to hour:minute in milliseconds.
     */
    public static long getMillisSinceMidnight(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
            throw new IllegalArgumentException();
        return (hour*60 + minute)*60*1000;
    }
}
