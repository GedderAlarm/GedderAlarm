/*
 * USER: jameskluz
 * DATE: 3/3/17
 */

package com.gedder.gedderalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.gedder.gedderalarm.util.Log;

import static android.content.Context.ALARM_SERVICE;


public class GedderRestartReceiver extends BroadcastReceiver {
    private static final String TAG = GedderRestartReceiver.class.getSimpleName();

    private static long sScheduledAlarmTimeInMs;
    private static boolean sAlarmSet;
    private static boolean sAppShutDownCorrectly;
    private AlarmManager mAlarmManager;
    private final int intentId = 31582;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive()");

        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_BOOT_COMPLETED:
                    Log.e(TAG, "Reset alarms after reboot");
                    resetAlarms(context);
                    Log.e(TAG, "Finished resetting alarms after reboot");
                        break;
                    default:
                        break;
            }
        }
    }

    private void resetAlarms(Context context) {
        Log.v(TAG, "resetAlarms()");

        SharedPreferences saved_values =
                context.getSharedPreferences(MainActivity.GEDDER_ALARM_SAVED_VARIABLES, 0);
        sAlarmSet = saved_values.getBoolean(MainActivity.GEDDER_ALARM_WAS_ALARM_SET, false);
        sScheduledAlarmTimeInMs =
                saved_values.getLong(MainActivity.GEDDER_ALARM_ALARM_TIME_IN_MILL, -1L);

        if (sAlarmSet && sScheduledAlarmTimeInMs > System.currentTimeMillis()) {
            mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, intentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= 23) {
                Log.v("Start Alarm", "Build.VERSION.SDK_INT >= 23");
                mAlarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, sScheduledAlarmTimeInMs, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                Log.v("Start Alarm", "19 <= Build.VERSION.SDK_INT < 23");
                mAlarmManager.setExact(
                        AlarmManager.RTC_WAKEUP, sScheduledAlarmTimeInMs, pendingIntent);
            } else {
                Log.v("Start Alarm", "Build.VERSION.SDK_INT < 19");
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, sScheduledAlarmTimeInMs, pendingIntent);
            }
        } else {
            // We missed the alarm while the phone was off; reset saved variables.
            SharedPreferences.Editor editor = saved_values.edit();
            editor.putBoolean(MainActivity.GEDDER_ALARM_WAS_ALARM_SET, false);
            editor.putLong(MainActivity.GEDDER_ALARM_MILL_UNTIL_ALARM, 0L);
            editor.putLong(MainActivity.GEDDER_ALARM_ALARM_TIME_IN_MILL, -1L);
            editor.apply();
        }
    }
}
