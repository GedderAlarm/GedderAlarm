/*
 * USER: jameskluz
 * DATE: 2/24/17.
 */

package com.gedder.gedderalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 */

public class AlarmReceiver extends BroadcastReceiver {
    // TODO: Look to see if this needs adjustment for all of past additions.

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmActivity = new Intent(context.getApplicationContext(), AlarmActivity.class);
        alarmActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(alarmActivity);
    }
}
