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

    // Required for universal uniqueness of each alarm.
    private UUID    mUuid;

    // Unique number to identify this alarm in PendingIntents.
    private int     mRequestCode;

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

    /**
     * A copy constructor with explicit variables.
     * @param origin            The place the user is leaving from.
     * @param destination       The place the user wants to get to.
     * @param repeatDays        The days this alarm clock will repeat.
     * @param alarmTime         The time for which this alarm is set (or going to be set for).
     * @param arrivalTime       The time the user needs to get to their destination.
     * @param prepHour          The hour portion of the time it takes the user to get prepared for
     *                          travel after the alarm goes off.
     * @param prepMinute        The minute portion of the time it takes the user to get prepared for
     *                          travel after the alarm goes off.
     * @param upperBoundTime    The user selected alarm time. If Gedder is activated, this may
     *                          differ from the actual alarm time.
     */
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

    /**
     * A copy constructor with <em>very</em> explicit values.
     * @param origin            The place the user is leaving from.
     * @param destination       The place the user wants to get to.
     * @param repeatDays        The days this alarm clock will repeat.
     * @param alarmDay          The day portion of the time for which this alarm is to be set.
     * @param alarmHour         The hour portion of the time for which this alarm is to be set.
     * @param alarmMinute       The minute portion of the time for which this alarm is to be set.
     * @param arrivalDay        The day portion of the time the user needs to get to their
     *                          destination.
     * @param arrivalHour       The hour portion of the time the user needs to get to their
     *                          destination.
     * @param arrivalMinute     The minute portion of the time the user needs to get to their
     *                          destination.
     * @param prepHour          The hour portion of the time it takes the user to get prepared for
     *                          travel after the alarm goes off.
     * @param prepMinute        The minute portion of the time it takes the user to get prepared for
     *                          travel after the alarm goes off.
     * @param upperBoundDay     The day portion of the user selected alarm time.
     * @param upperBoundHour    The hour portion of the user selected alarm time.
     * @param upperBoundMinute  The minute portion of the user selected alarm time.
     */
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

    /** Defaults any current settings and turns off any running alarms. */
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
     * Sets origin for the purpose of the Gedder alarm.
     * @param origin The origin to set to. Must be non-null.
     * @throws IllegalArgumentException If origin is null.
     */
    public void setOrigin(String origin) {
        if (origin == null)
            throw new IllegalArgumentException();
        mOrigin = origin;
    }

    /**
     * Sets destination for the purpose of the Gedder alarm.
     * @param destination The destination to set to. Must be non-null.
     * @throws IllegalArgumentException If destination is null.
     */
    public void setDestination(String destination) {
        if (destination == null)
            throw new IllegalArgumentException();
        mDestination = destination;
    }

    /**
     * Sets the internal alarm time according to the Calendar's {@link Calendar#DAY_OF_WEEK},
     * {@link Calendar#HOUR}, and {@link Calendar#MINUTE} keys.
     * @param future A calendar that must have valid values for the keys
     *               {@link Calendar#DAY_OF_WEEK}, {@link Calendar#HOUR}, and
     *               {@link Calendar#MINUTE}.
     */
    public void setAlarmTime(Calendar future) {
        mAlarmTime = TimeUtilities.getMillisUntil(future);
        mAlarmDay = future.get(Calendar.DAY_OF_WEEK);
        mAlarmHour = future.get(Calendar.HOUR);
        mAlarmMinute = future.get(Calendar.MINUTE);
    }

    /**
     * Works the same as its public counterpart, {@link #setAlarmTime(Calendar)}, but uses explicit
     * inputs.
     * @param day       The day to set the internal alarm to.
     * @param hour      The hour to set the internal alarm to.
     * @param minute    The minute to set the internal alarm to.
     * @see #setAlarmTime(Calendar)
     */
    public void setAlarmTime(DaysOfWeek.DAY day, int hour, int minute) {
        setAlarmTime(day.getInt(), hour, minute);
    }

    /**
     * Works the same as its public counterpart, {@link #setAlarmTime(Calendar)}, but uses explicit
     * inputs.
     * @param day       The day to set the internal alarm to.
     * @param hour      The hour to set the internal alarm to.
     * @param minute    The minute to set the internal alarm to.
     * @see #setAlarmTime(Calendar)
     */
    private void setAlarmTime(int day, int hour, int minute) {
        mAlarmTime = TimeUtilities.getMillisUntil(day, hour, minute);
        mAlarmDay = day;
        mAlarmHour = hour;
        mAlarmMinute = minute;
    }

    /**
     * Sets the arrival time according to the Calendar's {@link Calendar#DAY_OF_WEEK},
     * {@link Calendar#HOUR}, and {@link Calendar#MINUTE} keys.
     * @param future A calendar that must have valid values for the keys
     *               {@link Calendar#DAY_OF_WEEK}, {@link Calendar#HOUR}, and
     *               {@link Calendar#MINUTE}.
     */
    public void setArrivalTime(Calendar future) {
        mArrivalTime = TimeUtilities.getMillisUntil(future);
        mArrivalDay = future.get(Calendar.DAY_OF_WEEK);
        mArrivalHour = future.get(Calendar.HOUR);
        mArrivalMinute = future.get(Calendar.MINUTE);
    }

    /**
     * Works the same as its public counterpart, {@link #setArrivalTime(Calendar)}, but uses explicit
     * inputs.
     * @param day       The day to set the arrival time to.
     * @param hour      The hour to set the arrival time to.
     * @param minute    The minute to set the arrival time to.
     * @see #setArrivalTime(Calendar)
     */
    public void setArrivalTime(DaysOfWeek.DAY day, int hour, int minute) {
        setArrivalTime(day.getInt(), hour, minute);
    }

    /**
     * Works the same as its public counterpart, {@link #setArrivalTime(Calendar)}, but uses explicit
     * inputs.
     * @param day       The day to set the arrival time to.
     * @param hour      The hour to set the arrival time to.
     * @param minute    The minute to set the arrival time to.
     * @see #setArrivalTime(Calendar)
     */
    private void setArrivalTime(int day, int hour, int minute) {
        mArrivalTime = TimeUtilities.getMillisUntil(day, hour, minute);
        mArrivalDay = day;
        mArrivalHour = hour;
        mArrivalMinute = minute;
    }

    /**
     * Sets the time it takes to get prepared for departure after the alarm triggers.
     * @param hour      The hours it takes to get prepared.
     * @param minute    The minutes it takes to get prepared.
     */
    public void setPrepTime(int hour, int minute) {
        mPrepTime = TimeUtilities.getMillis(hour, minute);
        mPrepHour = hour;
        mPrepMinute = minute;
    }

    /**
     * Sets the user's selected time according to the Calendar's {@link Calendar#DAY_OF_WEEK},
     * {@link Calendar#HOUR}, and {@link Calendar#MINUTE} keys.
     * @param future A calendar that must have valid values for the keys
     *               {@link Calendar#DAY_OF_WEEK}, {@link Calendar#HOUR}, and
     *               {@link Calendar#MINUTE}.
     */
    public void setUpperBoundTime(Calendar future) {
        mUpperBoundTime = TimeUtilities.getMillisUntil(future);
        mUpperBoundDay = future.get(Calendar.DAY_OF_WEEK);
        mUpperBoundHour = future.get(Calendar.HOUR);
        mUpperBoundMinute = future.get(Calendar.MINUTE);
    }

    /**
     * Works the same as its public counterpart, {@link #setUpperBoundTime(Calendar)}, but uses
     * explicit inputs.
     * @param day       The day the user selects for the alarm.
     * @param hour      The hour the user selects for the alarm.
     * @param minute    The minute the user selects for the alarm.
     * @see #setUpperBoundTime(Calendar)
     */
    public void setUpperBoundTime(DaysOfWeek.DAY day, int hour, int minute) {
        setUpperBoundTime(day.getInt(), hour, minute);
    }

    /**
     * Works the same as its public counterpart, {@link #setUpperBoundTime(Calendar)}, but uses
     * explicit inputs.
     * @param day       The day the user selects for the alarm.
     * @param hour      The hour the user selects for the alarm.
     * @param minute    The minute the user selects for the alarm.
     * @see #setUpperBoundTime(Calendar)
     */
    private void setUpperBoundTime(int day, int hour, int minute) {
        mUpperBoundTime = TimeUtilities.getMillisUntil(day, hour, minute);
        mUpperBoundDay = day;
        mUpperBoundHour = hour;
        mUpperBoundMinute = minute;
    }

    /**
     * Sets a new alarm clock through intents to the Android OS. {@link AlarmReceiver} will receive
     * this intent.
     *
     * If the Gedder alarm is also required, tells the {@link GedderAlarmManager} to set up the
     * Gedder services for this particular alarm.
     */
    public void turnOn() {
        Intent alarmIntent =
                new Intent(GedderAlarmApplication.getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(GedderAlarmApplication.getAppContext(),
                        mRequestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        GedderAlarmManager.setOptimal(
                AlarmManager.RTC_WAKEUP, mAlarmTime, pendingIntent);

        if (isGedderOn()) {
            // We've set the normal alarm, and now we want to turn on the Gedder Engine.
            // We're the passenger, and the GedderAlarmManager is the driver. Tell the driver to
            // start the engine.
            Bundle bundle = new Bundle();
            bundle.putParcelable(GedderAlarmManager.PARAM_ALARM_CLOCK, this);
            bundle.putInt(GedderAlarmManager.PARAM_UNIQUE_ID, mRequestCode);
            GedderAlarmManager.setGedder(bundle);
            mGedderSet = true;
        }

        mAlarmSet = true;
    }

    /**
     * Cancels any alarm associated with this alarm clock instance. Also terminates any associations
     * with any Gedder services.
     *
     * <em><strong>NOTE:</strong></em> Does <em>not</em> reset alarm data; only cancels the alarm.
     */
    public void turnOff() {
        Intent alarmIntent =
                new Intent(GedderAlarmApplication.getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(GedderAlarmApplication.getAppContext(),
                mRequestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
     * Gets the universally unique identification associated with this particular alarm clock.
     * @return The universally unique identifcation for this alarm clock.
     */
    public UUID getUUID() {
        return mUuid;
    }

    /**
     * Gets the place the user is leaving from.
     * @return The place the user is leaving from.
     */
    public String getOrigin() {
        return mOrigin;
    }

    /**
     * Gets the place the user is going to.
     * @return The place the user is going to.
     */
    public String getDestination() {
        return mDestination;
    }

    /**
     * Gets the days that this alarm is set to repeat on.
     * @return The days this alarm is set to repeat on.
     */
    public DaysOfWeek getRepeatDays() {
        return mRepeatDays;
    }

    /**
     * Gets the internal alarm time for this alarm clock.
     * @return The internal alarm time.
     */
    public Calendar getAlarmTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, mAlarmDay);
        calendar.set(Calendar.HOUR, mAlarmHour);
        calendar.set(Calendar.MINUTE, mAlarmMinute);
        return calendar;
    }

    /**
     * Gets the internal alarm time for this alarm clock in milliseconds since the "epoch".
     * @return The internal alarm time in milliseconds since the "epoch".
     */
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
