/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * FROM: https://android.googlesource.com/platform/packages/apps/AlarmClock
 * FILE: src/com/android/alarmclock/Alarm.java
 * MODIFIED BY: mslm
 * DATE OF MODIFICATION: 3/27/17 and onward.
 */

package com.gedder.gedderalarm.util;

import android.util.SparseArray;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.EnumSet;

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

    /**
     *
     */
    public enum DAY {
        MONDAY(Calendar.MONDAY),
        TUESDAY(Calendar.TUESDAY),
        WEDNESDAY(Calendar.WEDNESDAY),
        THURSDAY(Calendar.THURSDAY),
        FRIDAY(Calendar.FRIDAY),
        SATURDAY(Calendar.SATURDAY),
        SUNDAY(Calendar.SUNDAY);

        private final int day;
        private static final SparseArray<DAY> lookup = new SparseArray<>();

        static {
            for (DAY d : EnumSet.allOf(DAY.class))
                lookup.put(d.getInt(), d);
        }

        DAY(int day) {
            this.day = day;
        }

        public int getInt() {
            return this.day;
        }

        public static DAY getDay(int day) {
            return lookup.get(day);
        }
    }

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