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

import com.gedder.gedderalarm.util.Log;

/**
 *
 */

public class AlarmRestartReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmRestartReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                resetAlarms(context);
            }
        }
    }

    private void resetAlarms(Context context) {
        // TODO: Get a cursor of all alarms and loop through them. Reactivate alarm if on before.

        if (sAlarmSet && sScheduledAlarmTimeInMs > System.currentTimeMillis()) {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, intentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            GedderAlarmManager.setOptimal(
                    AlarmManager.RTC_WAKEUP, sScheduledAlarmTimeInMs, pendingIntent);
        } else {
            // TODO: Turn alarm variables off for this.
            // We missed the alarm while the phone was off; appropriate alarm variables.
        }
    }
}
