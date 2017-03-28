/*
 * USER: mslm
 * DATE: 3/10/2017
 */

package com.gedder.gedderalarm.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.gedder.gedderalarm.AlarmReceiver;
import com.gedder.gedderalarm.GedderAlarmApplication;
import com.gedder.gedderalarm.GedderAlarmManager;

import java.util.UUID;


/**
 * An alarm clock class encapsulating the data stored in a typical alarm clock and the logic
 * required to start alarm clocks using raw pending intents.
 */
public class AlarmClock implements Parcelable {
    private static final String TAG = AlarmClock.class.getSimpleName();

    private static final int INTENT_ID = 31582;

    // Fundamental private variables.
    private UUID    mUuid;
    private String  mOrigin;
    private String  mDestination;
    private long    mScheduledAlarmTimeInMs;
    private long    mArrivalTimeInMs;
    private long    mPrepTimeInMs;
    private long    mUpperBoundInMs;
    private boolean mAlarmSet;
    private boolean mGedderSet;

    /**
     * Initializes an unset alarm clock with default parameters.
     */
    public AlarmClock() {
        mUuid = UUID.randomUUID();
        mOrigin = "";
        mDestination = "";
        mScheduledAlarmTimeInMs = 0L;
        mArrivalTimeInMs = 0L;
        mPrepTimeInMs = 0L;
        mUpperBoundInMs = 0L;
        mAlarmSet = false;
        mGedderSet = false;
    }

    /**
     * Copy constructor.
     * @param alarmClock The alarm clock instance to copy.
     */
    public AlarmClock(AlarmClock alarmClock) {
        mUuid = alarmClock.mUuid;
        mOrigin = alarmClock.mOrigin;
        mDestination = alarmClock.mDestination;
        mScheduledAlarmTimeInMs = alarmClock.mScheduledAlarmTimeInMs;
        mArrivalTimeInMs = alarmClock.mArrivalTimeInMs;
        mPrepTimeInMs = alarmClock.mPrepTimeInMs;
        mUpperBoundInMs = alarmClock.mUpperBoundInMs;
        mAlarmSet = alarmClock.mAlarmSet;
        mGedderSet = alarmClock.mGedderSet;
    }

    /**
     * Initializes an unset alarm clock based off of explicit parameters.
     * @param origin
     * @param destination
     * @param scheduledAlarmTimeInMs The scheduled alarm time in milliseconds to use in new alarm.
     * @param arrivalTimeInMs
     * @param prepTimeInMs
     * @param upperBoundInMs
     * @param alarmSet              Whether the alarm is set already or not.
     * @param gedderSet
     */
    public AlarmClock(String origin, String destination,
                      long scheduledAlarmTimeInMs, long arrivalTimeInMs,
                      long prepTimeInMs, long upperBoundInMs,
                      boolean alarmSet, boolean gedderSet) {
        mOrigin = origin;
        mDestination = destination;
        mScheduledAlarmTimeInMs = scheduledAlarmTimeInMs;
        mArrivalTimeInMs = arrivalTimeInMs;
        mPrepTimeInMs = prepTimeInMs;
        mUpperBoundInMs = upperBoundInMs;
        mAlarmSet = alarmSet;
        mGedderSet = gedderSet;
    }

    /**
     * Sets the time for the alarm.
     * NOTE: Does NOT set the pending intent for the alarm; only sets data.
     * @param scheduledAlarmTimeInMs The time the alarm is scheduled.
     */
    public void setAlarmTime(long scheduledAlarmTimeInMs) {
        mScheduledAlarmTimeInMs = scheduledAlarmTimeInMs;
    }

    public void setOrigin(String origin) {
        if (origin != null)
            mOrigin = origin;
    }

    public void setDestination(String destination) {
        if (destination != null)
            mDestination = destination;
    }

    public void setArrivalTime(long arrivalTimeInMs) {
        mArrivalTimeInMs = arrivalTimeInMs;
    }

    public void setPreparationTime(long prepTimeInMs) {
        mPrepTimeInMs = prepTimeInMs;
    }

    public void setUpperBound(long upperBoundTimeInMs) {
        mUpperBoundInMs = upperBoundTimeInMs;
    }

    /**
     * Clears any current settings. The alarm may still be on and set to run at the previously
     * appointed time.
     */
    public void clearAlarmSettings() {
        mScheduledAlarmTimeInMs = 0L;
    }

    /**
     * Sets a new alarm clock through intents to the Android OS.
     * com.gedder.gedderalarm.AlarmReceiver will receive this intent.
     */
    public void turnOn() {
        Intent alarmIntent =
                new Intent(GedderAlarmApplication.getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(GedderAlarmApplication.getAppContext(),
                        AlarmClock.INTENT_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        GedderAlarmManager.setOptimal(
                AlarmManager.RTC_WAKEUP, mScheduledAlarmTimeInMs, pendingIntent);

        mAlarmSet = true;
    }

    /**
     * Cancels any alarm associated with this alarm clock instance.
     * NOTE: Does NOT reset alarm data; only cancels the alarm intent.
     */
    public void turnOff() {
        Intent alarmIntent =
                new Intent(GedderAlarmApplication.getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(GedderAlarmApplication.getAppContext(),
                AlarmClock.INTENT_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        GedderAlarmManager.cancel(pendingIntent);
        mAlarmSet = false;
    }

    /**
     * Gets the current intended alarm clock time in milliseconds since the "epoch".
     * @return The current time set for the alarm in milliseconds since the epoch.
     */
    public long getAlarmTime() {
        return mScheduledAlarmTimeInMs;
    }

    /**
     * Tells you whether the alarm is currently set or not. It will return false if the alarm has
     * already gone off or has been explicitly canceled.
     * @return Whether the alarm is set or not.
     */
    public boolean isOn() {
        return mAlarmSet;
    }

    public UUID getUUID() {
        return mUuid;
    }

    private long getMsUntilAlarm() {
        return mScheduledAlarmTimeInMs - System.currentTimeMillis();
    }

    /** {@inheritDoc} */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mUuid);
        dest.writeLong(this.mScheduledAlarmTimeInMs);
        dest.writeByte(this.mAlarmSet ? (byte) 1 : (byte) 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcel constructor.
     * @param in The parcel to read.
     */
    protected AlarmClock(Parcel in) {
        this.mUuid = (UUID) in.readSerializable();
        this.mScheduledAlarmTimeInMs = in.readLong();
        this.mAlarmSet = in.readByte() != 0;
        this.mGedderSet = in.readByte() != 0;
    }

    public static final Creator<AlarmClock> CREATOR = new Creator<AlarmClock>() {
        @Override
        public AlarmClock createFromParcel(Parcel source) {
            return new AlarmClock(source);
        }

        @Override
        public AlarmClock[] newArray(int size) {
            return new AlarmClock[size];
        }
    };
}
