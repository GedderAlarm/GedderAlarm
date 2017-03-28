/*
 * FROM: https://android.googlesource.com/platform/packages/apps/AlarmClock
 * FILE: src/com/android/alarmclock/Alarm.java
 */

package com.gedder.gedderalarm.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;


/**
 * Pulled from Google source code. Their comments:
 *
 * Days of week code as a single int.
 * 0x00: no day
 * 0x01: Monday
 * 0x02: Tuesday
 * 0x04: Wednesday
 * 0x08: Thursday
 * 0x10: Friday
 * 0x20: Saturday
 * 0x40: Sunday
 */
public final class DaysOfWeek {

    private static int[] DAY_MAP = new int[] {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY,
    };

    // Bitmask of all repeating days
    private int mDays;

    public DaysOfWeek(int days) {
        mDays = days;
    }

    public String toString(boolean showNever) {
        StringBuilder ret = new StringBuilder();

        // no days
        if (mDays == 0) {
            return showNever ? "Never" : "";
        }

        // every day
        if (mDays == 0x7f) {
            return "every day";
        }

        // count selected days
        int dayCount = 0, days = mDays;
        while (days > 0) {
            if ((days & 1) == 1) dayCount++;
            days >>= 1;
        }

        // short or long form?
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] dayList = (dayCount > 1) ?
                dfs.getShortWeekdays() :
                dfs.getWeekdays();

        // selected days
        for (int i = 0; i < 7; i++) {
            if ((mDays & (1 << i)) != 0) {
                ret.append(dayList[DAY_MAP[i]]);
                dayCount -= 1;
                if (dayCount > 0) ret.append(", ");
            }
        }
        return ret.toString();
    }

    private boolean isSet(int day) {
        return ((mDays & (1 << day)) > 0);
    }

    public void set(int day, boolean set) {
        if (set) {
            mDays |= (1 << day);
        } else {
            mDays &= ~(1 << day);
        }
    }

    public void set(DaysOfWeek dow) {
        mDays = dow.mDays;
    }

    public int getCoded() {
        return mDays;
    }

    // Returns days of week encoded in an array of booleans.
    public boolean[] getBooleanArray() {
        boolean[] ret = new boolean[7];
        for (int i = 0; i < 7; i++) {
            ret[i] = isSet(i);
        }
        return ret;
    }

    public boolean isRepeatSet() {
        return mDays != 0;
    }
}