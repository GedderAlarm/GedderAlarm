/*
 * USER: mslm
 * DATE: 3/28/17
 */

package com.gedder.gedderalarm.util;

/**
 * Contains general time-related utilities.
 */
public final class TimeUtilities {
    private static final String TAG = TimeUtilities.class.getSimpleName();

    private TimeUtilities() {}

    /**
     * Gets the time since midnight up to hour:minute.
     * @param hour      The hour to count up to since midnight.
     * @param minute    The minute to count up to since midnight.
     * @return The time since midnight up to hour:minute in milliseconds.
     */
    private long getTimeSinceMidnight(int hour, int minute) {
        return (hour*60 + minute)*60*1000;
    }
}
