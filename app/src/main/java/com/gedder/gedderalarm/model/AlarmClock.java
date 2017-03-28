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
 * An alarm clock class encapsulating all data for both a typical alarm clock and the specialized
 * Gedder alarm clock.
 */
public class AlarmClock implements Parcelable {
    private static final String TAG = AlarmClock.class.getSimpleName();

    private static final int INTENT_ID = 31582;

    // Required for universal uniqueness of each alarm.
    private UUID    mUuid;

    // Required for smart alarm.
    private String  mOrigin;
    private String  mDestination;

    // The real-time values for when this alarm is set, if it is set.
    private int     mAlarmHour;         // 0-23 (24 hour clock)
    private int     mAlarmMinute;       // 0-59 (60 minutes)
    private long    mAlarmTime;         // Since the epoch.

    // The user-planned arrival time to mDestination from mOrigin.
    // Required for smart alarm.
    private int     mArrivalHour;       // 0-23 (24 hour clock)
    private int     mArrivalMinute;     // 0-59 (60 minutes)
    private long    mArrivalTime;       // Since the epoch.

    // The user-inputted or smart-adjusted time it takes to get prepared in the morning.
    // Required for smart alarm.
    private int     mPrepHour;          // 0-23 (24 hour clock)
    private int     mPrepMinute;        // 0-59 (60 minutes)
    private long    mPrepTime;          // Since the epoch.

    // The user-inputted "wish" time to wake up: do not set an alarm past this time, only before.
    // Required for smart alarm.
    private int     mUpperBoundHour;    // 0-23 (24 hour clock)
    private int     mUpperBoundMinute;  // 0-59 (60 minutes)
    private long    mUpperBoundTime;    // Since the epoch.

    // The different types of alarms available.
    private boolean mAlarmSet;
    private boolean mGedderSet;

    /**
     * Initializes an unset alarm clock with default parameters.
     */
    public AlarmClock() {
        mUuid = UUID.randomUUID();

        mOrigin = "";           // If we find device location, that should be the default.
        mDestination = "";

        mAlarmHour = 0;
        mAlarmMinute = 0;
        mAlarmTime = 0L;

        // 8:00am
        mArrivalHour = 8;
        mArrivalMinute = 0;
        mArrivalTime = 0L;      // Not adjusted to 8:00am yet.

        // No prep by default.
        mPrepHour = 0;
        mPrepMinute = 0;
        mPrepTime = 0L;

        // 6:00am
        mUpperBoundHour = 6;
        mUpperBoundMinute = 0;
        mUpperBoundTime = 0L;   // Not adjusted to 6:00am yet.

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

        mAlarmHour = alarmClock.mAlarmHour;
        mAlarmMinute = alarmClock.mAlarmMinute;
        mAlarmTime = alarmClock.mAlarmTime;

        mArrivalHour = alarmClock.mArrivalHour;
        mArrivalMinute = alarmClock.mArrivalMinute;
        mArrivalTime = alarmClock.mArrivalTime;

        mPrepHour = alarmClock.mPrepHour;
        mPrepMinute = alarmClock.mPrepMinute;
        mPrepTime = alarmClock.mPrepTime;

        mUpperBoundHour = alarmClock.mUpperBoundHour;
        mUpperBoundMinute = alarmClock.mUpperBoundMinute;
        mUpperBoundTime = alarmClock.mUpperBoundTime;

        mAlarmSet = alarmClock.mAlarmSet;
        mGedderSet = alarmClock.mGedderSet;
    }

    /**
     * Initializes an unset alarm clock based off of explicit parameters.
     * @param origin
     * @param destination
     * @param scheduledAlarmTime The scheduled alarm time in milliseconds to use in new alarm.
     * @param arrivalTime
     * @param prepTime
     * @param upperBoundTime
     */
    public AlarmClock(String origin, String destination,
                      long scheduledAlarmTime, long arrivalTime,
                      long prepTime, long upperBoundTime) {
        mOrigin = origin;
        mDestination = destination;
        mAlarmTime = scheduledAlarmTime;
        mArrivalTime = arrivalTime;
        mPrepTime = prepTime;
        mUpperBoundTime = upperBoundTime;
        mAlarmSet = false;
        mGedderSet = false;
    }

    /**
     * Defaults any current settings and turns off any running alarms.
     */
    public void defaultAlarmSettings() {
        mOrigin = "";           // If we find device location, that should be the default.
        mDestination = "";

        mAlarmHour = 0;
        mAlarmMinute = 0;
        mAlarmTime = 0L;

        // 8:00am
        mArrivalHour = 8;
        mArrivalMinute = 0;
        mArrivalTime = 0L;      // Not adjusted to 8:00am yet.

        // No prep by default.
        mPrepHour = 0;
        mPrepMinute = 0;
        mPrepTime = 0L;

        // 6:00am
        mUpperBoundHour = 6;
        mUpperBoundMinute = 0;
        mUpperBoundTime = 0L;   // Not adjusted to 6:00am yet.

        if (isOn())
            turnOff();
    }

    /**
     *
     * @param origin
     */
    public void setOrigin(String origin) {
        if (origin != null)
            mOrigin = origin;
    }

    /**
     *
     * @param destination
     */
    public void setDestination(String destination) {
        if (destination != null)
            mDestination = destination;
    }

    /**
     *
     * @param hour
     * @param minute
     */
    public void setAlarmTime(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
            throw new IllegalArgumentException();


    }

    /**
     *
     * @param hour
     * @param minute
     */
    public void setArrivalTime(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
            throw new IllegalArgumentException();


    }

    /**
     *
     * @param hour
     * @param minute
     */
    public void setPrepTime(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
            throw new IllegalArgumentException();


    }

    /**
     *
     * @param hour
     * @param minute
     */
    public void setUpperBoundTime(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
            throw new IllegalArgumentException();


    }

    /**
     * Sets a new alarm clock through intents to the Android OS.
     * com.gedder.gedderalarm.AlarmReceiver will receive this intent.
     */
    public void turnOn() {
        // TODO: Adjust for GedderEngine.
        Intent alarmIntent =
                new Intent(GedderAlarmApplication.getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(GedderAlarmApplication.getAppContext(),
                        AlarmClock.INTENT_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        GedderAlarmManager.setOptimal(
                AlarmManager.RTC_WAKEUP, mAlarmTime, pendingIntent);

        mAlarmSet = true;
    }

    /**
     * Cancels any alarm associated with this alarm clock instance.
     * NOTE: Does NOT reset alarm data; only cancels the alarm intent.
     */
    public void turnOff() {
        // TODO: Adjust for GedderEngine.
        Intent alarmIntent =
                new Intent(GedderAlarmApplication.getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(GedderAlarmApplication.getAppContext(),
                AlarmClock.INTENT_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        GedderAlarmManager.cancel(pendingIntent);
        mAlarmSet = false;
    }

    /**
     *
     * @return
     */
    public UUID getUUID() {
        return mUuid;
    }

    /**
     *
     * @return
     */
    public String getOrigin() {
        return mOrigin;
    }

    /**
     *
     * @return
     */
    public String getDestination() {
        return mDestination;
    }

    /**
     *
     * @return
     */
    public int getAlarmHour() {
        return mAlarmHour;
    }

    /**
     *
     * @return
     */
    public int getAlarmMinute() {
        return mAlarmMinute;
    }

    /**
     * Gets the current intended alarm clock time in milliseconds since the "epoch".
     * @return The current time set for the alarm in milliseconds since the epoch.
     */
    public long getAlarmTime() {
        return mAlarmTime;
    }

    /**
     *
     * @return
     */
    public int getArrivalHour() {
        return mArrivalHour;
    }

    /**
     *
     * @return
     */
    public int getArrivalMinute() {
        return mArrivalMinute;
    }

    /**
     *
     * @return
     */
    public long getArrivalTime() {
        return mArrivalTime;
    }

    /**
     *
     * @return
     */
    public int getPrepHour() {
        return mPrepHour;
    }

    /**
     *
     * @return
     */
    public int getPrepMinute() {
        return mPrepMinute;
    }

    /**
     *
     * @return
     */
    public long getPrepTime() {
        return mPrepTime;
    }

    /**
     *
     * @return
     */
    public int getUpperBoundHour() {
        return mUpperBoundHour;
    }

    /**
     *
     * @return
     */
    public int getUpperBoundMinute() {
        return mUpperBoundMinute;
    }

    /**
     *
     * @return
     */
    public long getUpperBoundTime() {
        return mUpperBoundTime;
    }

    /**
     * Tells you whether the alarm is currently set or not. It will return false if the alarm has
     * already gone off or has been explicitly canceled.
     * @return Whether the alarm is set or not.
     */
    public boolean isOn() {
        return mAlarmSet;
    }

    /**
     *
     * @return
     */
    public boolean isGedderOn() {
        return mAlarmSet && mGedderSet;
    }

    /**
     *
     * @return
     */
    private long getTimeUntilAlarm() {
        return mAlarmTime - System.currentTimeMillis();
    }

    /** {@inheritDoc} */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mUuid);
        dest.writeLong(this.mAlarmTime);
        dest.writeByte(this.mAlarmSet ? (byte) 1 : (byte) 0);
    }

    /** {@inheritDoc} */
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
        this.mAlarmTime = in.readLong();
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
