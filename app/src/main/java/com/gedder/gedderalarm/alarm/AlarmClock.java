/*
 * USER: mslm
 * DATE: 3/10/2017
 */

package com.gedder.gedderalarm.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.gedder.gedderalarm.AlarmReceiver;
import com.gedder.gedderalarm.util.Log;

import java.io.Serializable;
import java.util.UUID;

import static android.content.Context.ALARM_SERVICE;


/**
 * An alarm clock class encapsulating the data stored in a typical alarm clock and the logic
 * required to start alarm clocks using raw pending intents.
 */
public class AlarmClock implements Serializable {
    private static final String TAG = AlarmClock.class.getSimpleName();

    private final int intentId = 31582;
    private UUID mUuid;

    private Context mContext;
    private AlarmManager mAlarmManager;
    private long mMsUntilAlarm;
    private long mScheduledAlarmTimeInMs;
    private boolean mAlarmSet;

    /**
     * Copy constructor.
     *
     * @param alarmClock The alarm clock instance to copy.
     */
    public AlarmClock(AlarmClock alarmClock) {
        this.mContext = alarmClock.mContext;
        this.mAlarmManager = alarmClock.mAlarmManager;
        this.mUuid = alarmClock.mUuid;
        this.mMsUntilAlarm = alarmClock.mMsUntilAlarm;
        this.mScheduledAlarmTimeInMs = alarmClock.mScheduledAlarmTimeInMs;
        this.mAlarmSet = alarmClock.mAlarmSet;
    }

    /**
     * Initializes an unset alarm clock with default parameters.
     */
    public AlarmClock(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        mUuid = UUID.randomUUID();
        mMsUntilAlarm = 0L;
        mScheduledAlarmTimeInMs = 0L;
        mAlarmSet = false;
    }

    /**
     * Initializes an unset alarm clock.
     *
     * @param mMsUntilAlarm The time until the alarm, in milliseconds.
     */
    public AlarmClock(Context context, long mMsUntilAlarm) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        mUuid = UUID.randomUUID();
        setAlarmTime(mMsUntilAlarm);
    }

    /**
     * Initializes an unset alarm clock based off of explicit parameters.
     *
     * @param context                The context to use in the new alarm.
     * @param alarmManager           The alarm manager to use in the new alarm.
     * @param scheduledAlarmTimeInMs The scheduled alarm time in milliseconds to use in new alarm.
     * @param alarmSet               Whether the alarm is set already or not.
     */
    public AlarmClock(Context context, AlarmManager alarmManager, UUID uuid,
                      long scheduledAlarmTimeInMs, boolean alarmSet) {
        this.mContext = context;
        this.mAlarmManager = alarmManager;
        this.mUuid = uuid;
        this.mScheduledAlarmTimeInMs = scheduledAlarmTimeInMs;
        this.mAlarmSet = alarmSet;
        updateMsUntilAlarm();
    }

    /**
     * Sets the time for the alarm.
     * NOTE: Does NOT set the pending intent for the alarm; only sets data.
     *
     * @param msUntilAlarm The time until the alarm, in milliseconds.
     */
    public void setAlarmTime(long msUntilAlarm) {
        Log.v(TAG, "setAlarmTime(" + String.valueOf(msUntilAlarm) + ")");

        this.mMsUntilAlarm = msUntilAlarm;
        mScheduledAlarmTimeInMs = System.currentTimeMillis() + this.mMsUntilAlarm;
    }

    /**
     * Clears any current settings. The alarm may still be on and set to run at the previously
     * appointed time.
     */
    public void clearAlarmSettings() {
        Log.v(TAG, "resetAlarm()");

        mMsUntilAlarm = 0L;
        mScheduledAlarmTimeInMs = 0L;
    }

    /**
     * Sets a new alarm clock through intents to the Android OS.
     * com.gedder.gedderalarm.AlarmReceiver will receive this intent.
     */
    public void setAlarm() {
        Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext, intentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 23) {
            Log.v(TAG, "Build.VERSION.SDK_INT >= 23");
            mAlarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, mScheduledAlarmTimeInMs, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            Log.v(TAG, "19 <= Build.VERSION.SDK_INT < 23");
            mAlarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, mScheduledAlarmTimeInMs, pendingIntent);
        } else {
            Log.v(TAG, "Build.VERSION.SDK_INT < 19");
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, mScheduledAlarmTimeInMs, pendingIntent);
        }

        mAlarmSet = true;
    }

    /**
     * Cancels any alarm associated with this alarm clock instance.
     * NOTE: Does NOT reset alarm data; only cancels the alarm intent.
     */
    public void cancelAlarm() {
        Log.v(TAG, "cancelAlarm()");

        Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext, intentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);

        mAlarmSet = false;
    }

    /**
     * Gets the current intended alarm clock time in milliseconds since the "epoch".
     *
     * @return The current time set for the alarm.
     */
    public long getAlarmTime() {
        return mScheduledAlarmTimeInMs;
    }

    /**
     * Gets the time until the alarm clock in milliseconds.
     *
     * @return The time until the alarm in milliseconds.
     */
    public long timeUntilAlarm() {
        updateMsUntilAlarm();
        return mMsUntilAlarm;
    }

    /**
     * Tells you whether the alarm is currently set or not. It will return false if the alarm has
     * already gone off or been explicitly canceled.
     *
     * @return Whether the alarm is set or not.
     */
    public boolean isSet() {
        return mAlarmSet;
    }

    public UUID getUUID() {
        return mUuid;
    }

    private void updateMsUntilAlarm() {
        long current = System.currentTimeMillis();
        mMsUntilAlarm = mScheduledAlarmTimeInMs - current;
    }
}
