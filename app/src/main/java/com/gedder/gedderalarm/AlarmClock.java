/*
 * USER: mslm
 * DATE: 3/10/2017
 */

package com.gedder.gedderalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.gedder.gedderalarm.util.Log;

import java.util.UUID;

import static android.content.Context.ALARM_SERVICE;


/**
 * An alarm clock class encapsulating the data stored in a typical alarm clock and the logic
 * required to start alarm clocks using raw pending intents.
 */
public class AlarmClock {
    private static final String TAG = AlarmClock.class.getSimpleName();

    private final int intentId = 31582;
    private final UUID uuid = UUID.randomUUID();

    private Context sContext;
    private AlarmManager mAlarmManager;
    private long msUntilAlarm;
    private long scheduledAlarmTimeInMs;
    private boolean alarmSet;

    /**
     * A fresh alarm clock without any alarm set.
     */
    public AlarmClock(Context context) {
        sContext = context;
        mAlarmManager = (AlarmManager) sContext.getSystemService(ALARM_SERVICE);
        msUntilAlarm = 0L;
        scheduledAlarmTimeInMs = 0L;
        alarmSet = false;
    }

    /**
     * A fresh alarm clock with alarm set for msUntilAlarm milliseconds into the future.
     * @param msUntilAlarm The time until the alarm, in milliseconds.
     */
    public AlarmClock(Context context, long msUntilAlarm) {
        sContext = context;
        mAlarmManager = (AlarmManager) sContext.getSystemService(ALARM_SERVICE);
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
        scheduledAlarmTimeInMs = System.currentTimeMillis() + this.msUntilAlarm;

        // bunch of code to activate intents etc.
        Intent alarmIntent = new Intent(sContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                sContext, intentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 23) {
            Log.v(TAG, "Build.VERSION.SDK_INT >= 23");
            mAlarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, scheduledAlarmTimeInMs, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            Log.v(TAG, "19 <= Build.VERSION.SDK_INT < 23");
            mAlarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, scheduledAlarmTimeInMs, pendingIntent);
        } else {
            Log.v(TAG, "Build.VERSION.SDK_INT < 19");
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, scheduledAlarmTimeInMs, pendingIntent);
        }

        alarmSet = true;
    }

    /**
     * Cancels any alarm associated with this alarm clock instance.
     */
    public void cancelAlarm() {
        Log.v(TAG, "cancelAlarm()");

        msUntilAlarm = 0L;
        scheduledAlarmTimeInMs = 0L;

        Intent alarmIntent = new Intent(sContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                sContext, intentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);

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
     * Gets the time until the alarm clock in milliseconds.
     * @return The time until the alarm in milliseconds.
     */
    public long timeUntilAlarm() {
        updateMsUntilAlarm();
        return msUntilAlarm;
    }

    /**
     * Tells you whether the alarm is currently set or not. It will return false if the alarm has
     * already gone off or been explicitly canceled.
     * @return Whether the alarm is set or not.
     */
    public boolean isSet() {
        return alarmSet;
    }

    public UUID getUUID() {
        return uuid;
    }

    private void updateMsUntilAlarm() {
        long current = System.currentTimeMillis();
        msUntilAlarm = scheduledAlarmTimeInMs - current;
    }
}
