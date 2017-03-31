/*
 * USER: mslm
 * DATE: 3/10/2017
 */

package com.gedder.gedderalarm.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.gedder.gedderalarm.AlarmReceiver;
import com.gedder.gedderalarm.GedderAlarmApplication;
import com.gedder.gedderalarm.GedderAlarmManager;
import com.gedder.gedderalarm.util.DaysOfWeek;
import com.gedder.gedderalarm.util.TimeUtilities;

import java.util.Calendar;
import java.util.UUID;

/**
 * An alarm clock class encapsulating all data for both a typical alarm clock and the specialized
 * Gedder alarm clock.
 */
public class AlarmClock implements Parcelable {
    private static final String TAG = AlarmClock.class.getSimpleName();

    // Default values for certain private variables.
    private static final String DEFAULT_ORIGIN = "";
    private static final String DEFAULT_DESTINATION = "";
    private static final int DEFAULT_ALARM_HOUR = 6;
    private static final int DEFAULT_ALARM_MINUTE = 0;
    private static final int DEFAULT_ARRIVAL_HOUR = 7;
    private static final int DEFAULT_ARRIVAL_MINUTE = 0;
    private static final int DEFAULT_PREP_HOUR = 0;
    private static final int DEFAULT_PREP_MINUTE = 0;
    private static final int DEFAULT_UPPER_BOUND_HOUR = 6;
    private static final int DEFAULT_UPPER_BOUND_MINUTE = 0;
    private static final boolean DEFAULT_ALARM_SET = false;
    private static final boolean DEFAULT_GEDDER_SET = false;
    private static final int INTENT_ID = 31582;

    // Required for universal uniqueness of each alarm.
    private UUID    mUuid;

    // Required for smart alarm.
    private String  mOrigin;
    private String  mDestination;

    // The days this alarm will repeat in its current form.
    private DaysOfWeek mRepeatDays;

    // The real-time values for when this alarm is set, if it is set.
    private int     mAlarmDay;          // 1-7 (Sunday, Monday, ..., Saturday)
    private int     mAlarmHour;         // 0-23 (24 hour clock)
    private int     mAlarmMinute;       // 0-59 (60 minutes)
    private long    mAlarmTime;         // Milliseconds since the epoch.

    // The user-planned arrival time to mDestination from mOrigin.
    // Required for smart alarm.
    private int     mArrivalDay;        // 1-7 (Sunday, Monday, ..., Saturday)
    private int     mArrivalHour;       // 0-23 (24 hour clock)
    private int     mArrivalMinute;     // 0-59 (60 minutes)
    private long    mArrivalTime;       // Milliseconds since the epoch.

    // The user-inputted or smart-adjusted time it takes to get prepared in the morning.
    // Required for smart alarm.
    private int     mPrepHour;          // 0-23 (24 hour clock)
    private int     mPrepMinute;        // 0-59 (60 minutes)
    private long    mPrepTime;          // Milliseconds since the epoch.

    // The user-inputted "wish" time to wake up: do not set an alarm past this time, only before.
    // Required for smart alarm.
    private int     mUpperBoundDay;     // 1-7 (Sunday, Monday, ..., Saturday)
    private int     mUpperBoundHour;    // 0-23 (24 hour clock)
    private int     mUpperBoundMinute;  // 0-59 (60 minutes)
    private long    mUpperBoundTime;    // Milliseconds since the epoch.

    // The different types of alarms available.
    private boolean mAlarmSet;
    private boolean mGedderSet;

    /**
     * Initializes an unset alarm clock with default parameters.
     */
    public AlarmClock() {
        Calendar calendar = Calendar.getInstance();

        mUuid = UUID.randomUUID();

        mOrigin      = DEFAULT_ORIGIN; // If we find device location, that should be the default.
        mDestination = DEFAULT_DESTINATION; // If we have history, set it as previously chosen one.

        // Not repeating by default.
        mRepeatDays = new DaysOfWeek();

        // Tomorrow 6:00am
        mAlarmDay    = (calendar.get(Calendar.DAY_OF_WEEK) + 1) % 7;
        mAlarmHour   = DEFAULT_ALARM_HOUR;
        mAlarmMinute = DEFAULT_ALARM_MINUTE;
        setAlarmTime(mAlarmDay, mAlarmHour, mAlarmMinute);

        // Tomorrow 7:00am
        mArrivalDay    = (calendar.get(Calendar.DAY_OF_WEEK) + 1) % 7;
        mArrivalHour   = DEFAULT_ARRIVAL_HOUR;
        mArrivalMinute = DEFAULT_ARRIVAL_MINUTE;
        setArrivalTime(mArrivalDay, mArrivalHour, mArrivalMinute);

        // No prep by default.
        mPrepHour   = DEFAULT_PREP_HOUR;
        mPrepMinute = DEFAULT_PREP_MINUTE;
        setPrepTime(mPrepHour, mPrepMinute);

        // Tomorrow 6:00am
        mUpperBoundDay    = (calendar.get(Calendar.DAY_OF_WEEK) + 1) % 7;
        mUpperBoundHour   = DEFAULT_UPPER_BOUND_HOUR;
        mUpperBoundMinute = DEFAULT_UPPER_BOUND_MINUTE;
        setUpperBoundTime(mUpperBoundDay, mUpperBoundHour, mUpperBoundMinute);

        mAlarmSet  = DEFAULT_ALARM_SET;
        mGedderSet = DEFAULT_GEDDER_SET;
    }

    /**
     * Copy constructor.
     * @param alarmClock The alarm clock instance to copy.
     */
    public AlarmClock(AlarmClock alarmClock) {
        mUuid = alarmClock.mUuid;

        mOrigin      = alarmClock.mOrigin;
        mDestination = alarmClock.mDestination;

        mRepeatDays = alarmClock.mRepeatDays;

        mAlarmDay    = alarmClock.mAlarmDay;
        mAlarmHour   = alarmClock.mAlarmHour;
        mAlarmMinute = alarmClock.mAlarmMinute;
        mAlarmTime   = alarmClock.mAlarmTime;

        mArrivalDay    = alarmClock.mArrivalDay;
        mArrivalHour   = alarmClock.mArrivalHour;
        mArrivalMinute = alarmClock.mArrivalMinute;
        mArrivalTime   = alarmClock.mArrivalTime;

        mPrepHour   = alarmClock.mPrepHour;
        mPrepMinute = alarmClock.mPrepMinute;
        mPrepTime   = alarmClock.mPrepTime;

        mUpperBoundDay    = alarmClock.mUpperBoundDay;
        mUpperBoundHour   = alarmClock.mUpperBoundHour;
        mUpperBoundMinute = alarmClock.mUpperBoundMinute;
        mUpperBoundTime   = alarmClock.mUpperBoundTime;

        mAlarmSet  = alarmClock.mAlarmSet;
        mGedderSet = alarmClock.mGedderSet;
    }

    public AlarmClock(String origin, String destination,
                      DaysOfWeek repeatDays,
                      Calendar alarmTime,
                      Calendar arrivalTime,
                      int prepHour, int prepMinute,
                      Calendar upperBoundTime) {
        mOrigin = origin;
        mDestination = destination;
        mRepeatDays = repeatDays;
        setAlarmTime(alarmTime);
        setArrivalTime(arrivalTime);
        setPrepTime(prepHour, prepMinute);
        setUpperBoundTime(upperBoundTime);
        mAlarmSet = DEFAULT_ALARM_SET;
        mGedderSet = DEFAULT_GEDDER_SET;
    }

    public AlarmClock(String origin, String destination,
                      DaysOfWeek repeatDays,
                      DaysOfWeek.DAY alarmDay, int alarmHour, int alarmMinute,
                      DaysOfWeek.DAY arrivalDay, int arrivalHour, int arrivalMinute,
                      int prepHour, int prepMinute,
                      DaysOfWeek.DAY upperBoundDay, int upperBoundHour, int upperBoundMinute) {
        mOrigin             = origin;
        mDestination        = destination;
        mRepeatDays         = repeatDays;
        mAlarmDay           = alarmDay.getInt();
        mAlarmHour          = alarmHour;
        mAlarmMinute        = alarmMinute;
        setAlarmTime(mAlarmDay, mAlarmHour, mAlarmMinute);
        mArrivalDay         = arrivalDay.getInt();
        mArrivalHour        = arrivalHour;
        mAlarmMinute        = arrivalMinute;
        setArrivalTime(mArrivalDay, mArrivalHour, mArrivalMinute);
        mPrepHour           = prepHour;
        mPrepMinute         = prepMinute;
        setPrepTime(mPrepHour, mPrepMinute);
        mUpperBoundDay      = upperBoundDay.getInt();
        mUpperBoundHour     = upperBoundHour;
        mUpperBoundMinute   = upperBoundMinute;
        setUpperBoundTime(mUpperBoundDay, mUpperBoundHour, mUpperBoundMinute);
        mAlarmSet           = DEFAULT_ALARM_SET;
        mGedderSet          = DEFAULT_GEDDER_SET;
    }

    /**
     * Defaults any current settings and turns off any running alarms.
     */
    public void defaultAlarmSettings() {
        Calendar calendar = Calendar.getInstance();

        mOrigin      = DEFAULT_ORIGIN; // If we find device location, that should be the default.
        mDestination = DEFAULT_DESTINATION; // If we have history, set it as previously chosen one.

        // Not repeating by default.
        mRepeatDays = new DaysOfWeek();

        // Tomorrow 6:00am
        mAlarmDay    = (calendar.get(Calendar.DAY_OF_WEEK) + 1) % 7;
        mAlarmHour   = DEFAULT_ALARM_HOUR;
        mAlarmMinute = DEFAULT_ALARM_MINUTE;
        setAlarmTime(mAlarmDay, mAlarmHour, mAlarmMinute);

        // Tomorrow 7:00am
        mArrivalDay    = (calendar.get(Calendar.DAY_OF_WEEK) + 1) % 7;
        mArrivalHour   = DEFAULT_ARRIVAL_HOUR;
        mArrivalMinute = DEFAULT_ARRIVAL_MINUTE;
        setArrivalTime(mArrivalDay, mArrivalHour, mArrivalMinute);

        // No prep by default.
        mPrepHour   = DEFAULT_PREP_HOUR;
        mPrepMinute = DEFAULT_PREP_MINUTE;
        setPrepTime(mPrepHour, mPrepMinute);

        // Tomorrow 6:00am
        mUpperBoundDay    = (calendar.get(Calendar.DAY_OF_WEEK) + 1) % 7;
        mUpperBoundHour   = DEFAULT_UPPER_BOUND_HOUR;
        mUpperBoundMinute = DEFAULT_UPPER_BOUND_MINUTE;
        setUpperBoundTime(mUpperBoundDay, mUpperBoundHour, mUpperBoundMinute);

        if (isOn())
            turnOff();
    }

    /**
     *
     * @param origin
     */
    public void setOrigin(String origin) {
        if (origin == null)
            throw new IllegalArgumentException();
        mOrigin = origin;
    }

    /**
     *
     * @param destination
     */
    public void setDestination(String destination) {
        if (destination == null)
            throw new IllegalArgumentException();
        mDestination = destination;
    }

    /**
     *
     * @param future
     */
    public void setAlarmTime(Calendar future) {
        mAlarmTime = TimeUtilities.getMillisUntil(future);
        mAlarmDay = future.get(Calendar.DAY_OF_WEEK);
        mAlarmHour = future.get(Calendar.HOUR);
        mAlarmMinute = future.get(Calendar.MINUTE);
    }

    /**
     *
     * @param day
     * @param hour
     * @param minute
     */
    public void setAlarmTime(DaysOfWeek.DAY day, int hour, int minute) {
        setAlarmTime(day.getInt(), hour, minute);
    }

    private void setAlarmTime(int day, int hour, int minute) {
        mAlarmTime = TimeUtilities.getMillisUntil(day, hour, minute);
        mAlarmDay = day;
        mAlarmHour = hour;
        mAlarmMinute = minute;
    }

    /**
     *
     * @param future
     */
    public void setArrivalTime(Calendar future) {
        mArrivalTime = TimeUtilities.getMillisUntil(future);
        mArrivalDay = future.get(Calendar.DAY_OF_WEEK);
        mArrivalHour = future.get(Calendar.HOUR);
        mArrivalMinute = future.get(Calendar.MINUTE);
    }

    /**
     *
     * @param day
     * @param hour
     * @param minute
     */
    public void setArrivalTime(DaysOfWeek.DAY day, int hour, int minute) {
        setArrivalTime(day.getInt(), hour, minute);
    }

    private void setArrivalTime(int day, int hour, int minute) {
        mArrivalTime = TimeUtilities.getMillisUntil(day, hour, minute);
        mArrivalDay = day;
        mArrivalHour = hour;
        mArrivalMinute = minute;
    }

    /**
     *
     * @param hour
     * @param minute
     */
    public void setPrepTime(int hour, int minute) {
        mPrepTime = TimeUtilities.getMillis(hour, minute);
        mPrepHour = hour;
        mPrepMinute = minute;
    }

    /**
     *
     * @param future
     */
    public void setUpperBoundTime(Calendar future) {
        mUpperBoundTime = TimeUtilities.getMillisUntil(future);
        mUpperBoundDay = future.get(Calendar.DAY_OF_WEEK);
        mUpperBoundHour = future.get(Calendar.HOUR);
        mUpperBoundMinute = future.get(Calendar.MINUTE);
    }

    /**
     *
     * @param day
     * @param hour
     * @param minute
     */
    public void setUpperBoundTime(DaysOfWeek.DAY day, int hour, int minute) {
        setUpperBoundTime(day.getInt(), hour, minute);
    }

    private void setUpperBoundTime(int day, int hour, int minute) {
        mUpperBoundTime = TimeUtilities.getMillisUntil(day, hour, minute);
        mUpperBoundDay = day;
        mUpperBoundHour = hour;
        mUpperBoundMinute = minute;
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

        if (isGedderOn()) {
            // We've set the normal alarm, but now we want to activate the Gedder Engine as a
            // service to watch over our alarm. For that, we need to send it some required data.

            // TODO: Implement communication details.
            GedderAlarmManager.setGedder(new Bundle());
            mGedderSet = true;
        }

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

        if (isGedderOn()) {
            // We've canceled the normal alarm, but let's tell the manager to turn off the engine.

            // TODO: Implement communication details.
            GedderAlarmManager.cancelGedder(new Bundle());
            mGedderSet = false;
        }

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
    public DaysOfWeek getRepeatDays() {
        return mRepeatDays;
    }

    /**
     * Gets the current intended alarm clock time in milliseconds since the "epoch".
     * @return The current time set for the alarm in milliseconds since the epoch.
     */
    public Calendar getAlarmTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, mAlarmDay);
        calendar.set(Calendar.HOUR, mAlarmHour);
        calendar.set(Calendar.MINUTE, mAlarmMinute);
        return calendar;
    }

    public long getAlarmTimeMillis() {
        return mAlarmTime;
    }

    /**
     *
     * @return
     */
    public Calendar getArrivalTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, mArrivalDay);
        calendar.set(Calendar.HOUR, mArrivalHour);
        calendar.set(Calendar.MINUTE, mArrivalMinute);
        return calendar;
    }

    public long getArrivalTimeMillis() {
        return mArrivalTime;
    }

    /**
     *
     * @return
     */
    public Calendar getPrepTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, mPrepHour);
        calendar.set(Calendar.MINUTE, mPrepMinute);
        return calendar;
    }

    public long getPrepTimeMillis() {
        return mPrepTime;
    }

    /**
     *
     * @return
     */
    public Calendar getUpperBoundTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, mUpperBoundDay);
        calendar.set(Calendar.HOUR, mUpperBoundHour);
        calendar.set(Calendar.MINUTE, mUpperBoundMinute);
        return calendar;
    }

    public long getUpperBoundTimeMillis() {
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
