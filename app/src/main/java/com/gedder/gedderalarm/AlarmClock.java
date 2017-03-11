/*
 * USER: mslm
 * DATE: 3/10/2017
 */

package com.gedder.gedderalarm;

import com.gedder.gedderalarm.util.Log;


/**
 * An alarm clock class encapsulating the data stored in a typical alarm clock and the logic
 * required to start alarm clocks using raw pending intents.
 */
public class AlarmClock {
    private static final String TAG = AlarmClock.class.getSimpleName();

    private long msUntilAlarm;
    private long scheduledAlarmTimeInMs;
    private boolean alarmSet;

    /**
     * A fresh alarm clock without any alarm set.
     */
    public AlarmClock() {
        msUntilAlarm = -1L;
        scheduledAlarmTimeInMs = -1L;
        alarmSet = false;
    }

    /**
     * A fresh alarm clock with alarm set for msUntilAlarm milliseconds into the future.
     * @param msUntilAlarm The time until the alarm, in milliseconds.
     */
    public AlarmClock(long msUntilAlarm) {
        setAlarmTime(msUntilAlarm);
    }

    /**
     * Sets a new alarm clock through intents to the Android OS.
     * com.gedder.gedderalarm.AlarmReceiver will receive this intent.
     * @param msUntilAlarm The time until the alarm, in milliseconds.
     */
    public void setAlarmTime(long msUntilAlarm) {
        Log.v(TAG, "setAlarmTime(" + String.valueOf(msUntilAlarm) + ")");

        this.msUntilAlarm = msUntilAlarm;

        // scheduledAlarmTimeInMs = time_right_now + this.msUntilAlarm;

        // bunch of code to activate intents etc.

        alarmSet = true;
    }

    /**
     * Cancels any alarm associated with this alarm clock instance.
     */
    public void cancelAlarm() {
        Log.v(TAG, "cancelAlarm()");

        msUntilAlarm = -1L;
        scheduledAlarmTimeInMs = -1L;

        // bunch of code to deactivate intents etc.

        alarmSet = false;
    }

    /**
     * Gets the current intended alarm clock time in milliseconds since the "epoch".
     * @return The current time set for the alarm.
     */
    public long getAlarmTime() {
        return scheduledAlarmTimeInMs;
    }

    /**
     * Tells you whether the alarm is currently set or not. It will return false if the alarm has
     * already gone off or been explicitly canceled.
     * @return Whether the alarm is set or not.
     */
    public boolean isSet() {
        return alarmSet;
    }

    private void updateMsUntilAlarm() {
        /*
         * 1. Get current time in milliseconds.
         * 2. Subtract that from scheduledAlarmTimeInMs.
         * 3. Equate msUntilAlarm to the result.
         */
    }
}
