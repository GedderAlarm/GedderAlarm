package com.gedder.gedderalarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.gedder.gedderalarm.util.Log;


/**
 * USER: jameskluz
 * DATE: 2/24/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmReceiver", "onReceive() called");
        Log.v("AlarmReceiver", "onReceive() called");

        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

        Log.v("AlarmReceiver", "onReceive() ending");
    }
}
