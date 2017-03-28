/*
 * USER: mslm
 * DATE: 3/27/17
 */

package com.gedder.gedderalarm;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Handler;

import static android.content.Context.ALARM_SERVICE;


/**
 * A reimplementation of AlarmManager (because we can't extend it), to manage Gedder Alarms.
 */
public final class GedderAlarmManager {
    private static final String TAG = GedderAlarmManager.class.getSimpleName();

    private static AlarmManager sAlarmManager =
            (AlarmManager) GedderAlarmApplication.getAppContext().getSystemService(ALARM_SERVICE);

    private GedderAlarmManager() {}

    public void cancel(PendingIntent operation) {
        sAlarmManager.cancel(operation);
    }

    @TargetApi(24)
    public void cancel(AlarmManager.OnAlarmListener listener) {
        if (Build.VERSION.SDK_INT >= 24)
            sAlarmManager.cancel(listener);
    }

    @TargetApi(21)
    public AlarmManager.AlarmClockInfo getNextAlarmClock() {
        if (Build.VERSION.SDK_INT >= 21)
            return sAlarmManager.getNextAlarmClock();
        return null;
    }

    public void set(int type, long triggerAtMillis, PendingIntent operation) {
        sAlarmManager.set(type, triggerAtMillis, operation);
    }

    @TargetApi(24)
    public void set(int type, long triggerAtMillis, String tag,
                    AlarmManager.OnAlarmListener listener, Handler targetHandler) {
        if (Build.VERSION.SDK_INT >= 24)
            sAlarmManager.set(type, triggerAtMillis, tag, listener, targetHandler);
    }

    @TargetApi(21)
    public void setAlarmClock(AlarmManager.AlarmClockInfo info, PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= 21)
            sAlarmManager.setAlarmClock(info, operation);
    }

    @TargetApi(23)
    public void setAndAllowWhileIdle(int type, long triggerAtMillis, PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= 23)
            sAlarmManager.setAndAllowWhileIdle(type, triggerAtMillis, operation);
    }

    public void setExact(int type, long triggerAtMillis, PendingIntent operation) {
        sAlarmManager.setExact(type, triggerAtMillis, operation);
    }

    @TargetApi(24)
    public void setExact(int type, long triggerAtMillis, String tag,
                         AlarmManager.OnAlarmListener listener, Handler targetHandler) {
        if (Build.VERSION.SDK_INT >= 24)
            sAlarmManager.setExact(type, triggerAtMillis, tag, listener, targetHandler);
    }

    @TargetApi(23)
    public void setExactAndAllowWhileIdle(int type, long triggerAtMillis, PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= 23)
            sAlarmManager.setAndAllowWhileIdle(type, triggerAtMillis, operation);
    }

    public void setInexactRepeating(int type, long triggerAtMillis, long intervalMillis,
                                    PendingIntent operation) {
        sAlarmManager.setInexactRepeating(type, triggerAtMillis, intervalMillis, operation);
    }

    public void setRepeating(int type, long triggerAtMillis, long intervalMillis,
                             PendingIntent operation) {
        sAlarmManager.setRepeating(type, triggerAtMillis, intervalMillis, operation);
    }

    public void setTime(long millis) {
        sAlarmManager.setTime(millis);
    }

    public void setTimeZone(String timeZone) {
        sAlarmManager.setTimeZone(timeZone);
    }

    public void setWindow(int type, long windowStartMillis, long windowLengthMillis,
                          PendingIntent operation) {
        sAlarmManager.setWindow(type, windowStartMillis, windowLengthMillis, operation);
    }

    @TargetApi(24)
    public void setWindow(int type, long windowStartMillis, long windowLengthMillis, String tag,
                          AlarmManager.OnAlarmListener listener, Handler targetHandler) {
        if (Build.VERSION.SDK_INT >= 24)
            sAlarmManager.setWindow(
                    type, windowStartMillis, windowLengthMillis, tag, listener, targetHandler);
    }
}
