/*
 * USER: mslm
 * DATE: 3/27/17
 */

package com.gedder.gedderalarm;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.gedder.gedderalarm.model.AlarmClock;

import static android.content.Context.ALARM_SERVICE;

/**
 * A reimplementation of AlarmManager (because we can't extend it), to manage Gedder Alarms.
 * All documentation can be found at Android's official documentation repositories.
 * Search for 'AlarmManager'.
 *
 * All modified/added functionality is documented.
 */
public final class GedderAlarmManager {
    private static final String TAG = GedderAlarmManager.class.getSimpleName();

    public static final String PARAM_ALARM_CLOCK = "__PARAM_ALARM_CLOCK__";
    public static final String PARAM_UNIQUE_ID = "__PARAM_UNIQUE_ID__";

    private static AlarmManager sAlarmManager =
            (AlarmManager) GedderAlarmApplication.getAppContext().getSystemService(ALARM_SERVICE);

    private GedderAlarmManager() {}

    /**
     * Checks build version to decide which set functionality to use.
     * @param type              See AlarmManager documentation.
     * @param triggerAtMillis   See AlarmManager documentation.
     * @param operation         See AlarmManager documentation.
     */
    public static void setOptimal(int type, long triggerAtMillis, PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= 23)
            sAlarmManager.setExactAndAllowWhileIdle(type, triggerAtMillis, operation);
        else if (Build.VERSION.SDK_INT >= 19)
            sAlarmManager.setExact(type, triggerAtMillis, operation);
        else
            sAlarmManager.set(type, triggerAtMillis, operation);
    }

    /**
     *
     * @param gedderData
     */
    public static void setGedder(Bundle gedderData) {
        AlarmClock alarmClock = gedderData.getParcelable(PARAM_ALARM_CLOCK);
        int id = gedderData.getInt(PARAM_UNIQUE_ID, -1);

        if (alarmClock == null || id == -1)
            throw new IllegalArgumentException(
                    "alarmClock = " + alarmClock
                    + "id = " + id);

        Intent intent = new Intent(GedderAlarmApplication.getAppContext(), GedderReceiver.class);
        intent.putExtra(GedderReceiver.PARAM_ORIGIN, alarmClock.getOrigin());
        intent.putExtra(GedderReceiver.PARAM_DESTINATION, alarmClock.getDestination());
        intent.putExtra(GedderReceiver.PARAM_ARRIVAL_TIME, alarmClock.getArrivalTimeMillis());
        intent.putExtra(GedderReceiver.PARAM_PREP_TIME, alarmClock.getPrepTimeMillis());
        intent.putExtra(GedderReceiver.PARAM_UPPER_BOUND_TIME, alarmClock.getUpperBoundTimeMillis());
        intent.putExtra(GedderReceiver.PARAM_ID, id);
        LocalBroadcastManager.getInstance(GedderAlarmApplication.getAppContext())
                .sendBroadcast(intent);
    }

    /**
     *
     * @param gedderData
     */
    public static void cancelGedder(Bundle gedderData) {
        AlarmClock alarmClock = gedderData.getParcelable(PARAM_ALARM_CLOCK);
        int id = gedderData.getInt(PARAM_UNIQUE_ID, -1);
    }

    public static void cancel(PendingIntent operation) {
        sAlarmManager.cancel(operation);
    }

    @TargetApi(24)
    public static void cancel(AlarmManager.OnAlarmListener listener) {
        if (Build.VERSION.SDK_INT >= 24)
            sAlarmManager.cancel(listener);
    }

    @TargetApi(21)
    public static AlarmManager.AlarmClockInfo getNextAlarmClock() {
        if (Build.VERSION.SDK_INT >= 21)
            return sAlarmManager.getNextAlarmClock();
        return null;
    }

    public static void set(int type, long triggerAtMillis, PendingIntent operation) {
        sAlarmManager.set(type, triggerAtMillis, operation);
    }

    @TargetApi(24)
    public static void set(int type, long triggerAtMillis, String tag,
                    AlarmManager.OnAlarmListener listener, Handler targetHandler) {
        if (Build.VERSION.SDK_INT >= 24)
            sAlarmManager.set(type, triggerAtMillis, tag, listener, targetHandler);
    }

    @TargetApi(21)
    public static void setAlarmClock(AlarmManager.AlarmClockInfo info, PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= 21)
            sAlarmManager.setAlarmClock(info, operation);
    }

    @TargetApi(23)
    public static void setAndAllowWhileIdle(int type, long triggerAtMillis,
                                            PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= 23)
            sAlarmManager.setAndAllowWhileIdle(type, triggerAtMillis, operation);
    }

    public static void setExact(int type, long triggerAtMillis, PendingIntent operation) {
        sAlarmManager.setExact(type, triggerAtMillis, operation);
    }

    @TargetApi(24)
    public static void setExact(int type, long triggerAtMillis, String tag,
                         AlarmManager.OnAlarmListener listener, Handler targetHandler) {
        if (Build.VERSION.SDK_INT >= 24)
            sAlarmManager.setExact(type, triggerAtMillis, tag, listener, targetHandler);
    }

    @TargetApi(23)
    public static void setExactAndAllowWhileIdle(int type, long triggerAtMillis,
                                                 PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= 23)
            sAlarmManager.setAndAllowWhileIdle(type, triggerAtMillis, operation);
    }

    public static void setInexactRepeating(int type, long triggerAtMillis, long intervalMillis,
                                    PendingIntent operation) {
        sAlarmManager.setInexactRepeating(type, triggerAtMillis, intervalMillis, operation);
    }

    public static void setRepeating(int type, long triggerAtMillis, long intervalMillis,
                             PendingIntent operation) {
        sAlarmManager.setRepeating(type, triggerAtMillis, intervalMillis, operation);
    }

    public static void setTime(long millis) {
        sAlarmManager.setTime(millis);
    }

    public static void setTimeZone(String timeZone) {
        sAlarmManager.setTimeZone(timeZone);
    }

    public static void setWindow(int type, long windowStartMillis, long windowLengthMillis,
                          PendingIntent operation) {
        sAlarmManager.setWindow(type, windowStartMillis, windowLengthMillis, operation);
    }

    @TargetApi(24)
    public static void setWindow(int type, long windowStartMillis, long windowLengthMillis,
                                 String tag, AlarmManager.OnAlarmListener listener,
                                 Handler targetHandler) {
        if (Build.VERSION.SDK_INT >= 24)
            sAlarmManager.setWindow(
                    type, windowStartMillis, windowLengthMillis, tag, listener, targetHandler);
    }
}
