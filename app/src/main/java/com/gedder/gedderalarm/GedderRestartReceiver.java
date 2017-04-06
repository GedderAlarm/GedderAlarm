/*
 * USER: mslm
 * DATE: 4/5/17
 */

package com.gedder.gedderalarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**  */

public class GedderRestartReceiver extends BroadcastReceiver {
    private static final String TAG = GedderRestartReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                resetAlarms(context);
            }
        }
    }

    private void resetAlarms(Context context) {
        // TODO: Get a cursor of all alarms and loop through them. Reactivate gedder if on before.

        if (sAlarmSet && sScheduledAlarmTimeInMs > System.currentTimeMillis()) {
            GedderAlarmManager.setGedder(new Bundle());
        } else {
            // TODO: Turn Gedder variables off for this alarm.
            // We missed the alarm while the phone was off; appropriate alarm variables.
        }
    }
}
