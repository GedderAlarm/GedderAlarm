package com.gedder.gedderalarm.util;

import java.util.Calendar;

/**
 * Created by jameskluz on 4/26/17.
 */

public class DayPicker {
    private int mAlarmDay;
    private int mArrivalDay;

    public DayPicker(int alarm_hour, int alarm_minute, int arrival_hour, int arrival_minute) {
        Calendar c = Calendar.getInstance();
        int now_hour = c.get(Calendar.HOUR_OF_DAY);
        int now_min = c.get(Calendar.MINUTE);
        mAlarmDay = c.get(Calendar.DAY_OF_WEEK);
        if (alarm_hour < now_hour || (alarm_hour == now_hour && alarm_minute <= now_min)) {
            mAlarmDay = (mAlarmDay % 7) + 1;
        }
        mArrivalDay = mAlarmDay;
        if (arrival_hour < alarm_hour || (arrival_hour < alarm_hour && arrival_minute < alarm_minute)) {
            mArrivalDay = (mAlarmDay % 7) + 1;
        }
    }

    public int getAlarmDay () {
        return mAlarmDay;
    }

    public int getArrivalDay() {
        return mArrivalDay;
    }
}
