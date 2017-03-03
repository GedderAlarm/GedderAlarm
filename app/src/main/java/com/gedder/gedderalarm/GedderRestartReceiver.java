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

/**
 * Created by jameskluz on 3/3/17.
 */

public class GedderRestartReceiver extends BroadcastReceiver {
    private static long scheduled_alarm_time_in_ms;
    private static boolean alarm_set;
    private static boolean app_shut_down_correctly;
    private AlarmManager alarmManager;
    private final int intent_id = 31582;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("GedderRestartReceiver", "onReceive() called");
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_BOOT_COMPLETED:
                    Log.e("GedderRestartReceiver", "Reset alarms after reboot");
                    resetAlarms(context);
                    Log.e("GedderRestartReceiver", "Finished resetting alarms after reboot");
                        break;
                    default:
                        break;
            }
        }
        Log.v("GedderRestartReceiver", "onReceive() ended");
    }

    private void resetAlarms(Context context) {
        Log.v("GedderRestartReceiver", "resetAlarms() called");
        SharedPreferences saved_values = context.getSharedPreferences(MainActivity.GEDDER_ALARM_SAVED_VARIABLES, 0);
        alarm_set = saved_values.getBoolean(MainActivity.GEDDER_ALARM_WAS_ALARM_SET, false);
        scheduled_alarm_time_in_ms = saved_values.getLong(MainActivity.GEDDER_ALARM_ALARM_TIME_IN_MILL, -1L);
        if(alarm_set && scheduled_alarm_time_in_ms > System.currentTimeMillis()){
            alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, intent_id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            if (Build.VERSION.SDK_INT >= 23) {
                com.gedder.gedderalarm.util.Log.v("Start Alarm", "Build.VERSION.SDK_INT >= 23");
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, scheduled_alarm_time_in_ms, pendingIntent
                );
            } else if (Build.VERSION.SDK_INT >= 19) {
                com.gedder.gedderalarm.util.Log.v("Start Alarm", "19 <= Build.VERSION.SDK_INT < 23");
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP, scheduled_alarm_time_in_ms, pendingIntent
                );
            } else {
                com.gedder.gedderalarm.util.Log.v("Start Alarm", "Build.VERSION.SDK_INT < 19");
                alarmManager.set(AlarmManager.RTC_WAKEUP, scheduled_alarm_time_in_ms, pendingIntent);
            }
        }
        Log.v("GedderRestartReceiver", "resetAlarms() called");
    }
}
