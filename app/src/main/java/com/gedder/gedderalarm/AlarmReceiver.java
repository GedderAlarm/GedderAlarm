package com.gedder.gedderalarm;

import android.app.Notification;
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
    //public static String NOTIFICATION_ID = "NOTIFICATION-ID";
    //public static String NOTIFICATION = "NOTIFICATION";
    //private Notification.Builder mBuilder;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmReceiver", "onReceive() called");
        Log.v("AlarmReceiver", "onReceive() called");

        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();
        //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Ringtone r = RingtoneManager.getRingtone(context, notification);
        //r.play();
        Intent alarmActivityIntent = new Intent(context.getApplicationContext(), AlarmActivity.class);
        alarmActivityIntent.addFlags(/*Intent.FLAG_ACTIVITY_CLEAR_TASK | */ Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(alarmActivityIntent);
        Log.v("AlarmReceiver", "onReceive() ending");
    }
}
