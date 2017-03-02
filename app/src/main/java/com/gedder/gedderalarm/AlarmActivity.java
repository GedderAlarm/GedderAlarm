package com.gedder.gedderalarm;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.gedder.gedderalarm.util.Log;

/**
 * Created by jameskluz on 3/1/17.
 */

public class AlarmActivity extends AppCompatActivity {
    //I think we may need to use something other than Ringtone but this
    //is just for testing
    private Ringtone ringtone;
    //this is used to get the ringtone
    private Uri alert;
    //this links us to the "stop alarm" button
    private Button stop_alarm_btn;
    private static long scheduled_alarm_time_in_ms;
    private static boolean alarm_set;
    private long ms_until_alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        initializeVariables();
    }

    private void initializeVariables() {
        Log.v("Initialize Variables AlarmActivity", "initializeVariables() called");
        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        ringtone = RingtoneManager.getRingtone(this, alert);
        ringtone.play();
        stop_alarm_btn = (Button) findViewById(R.id.stop_alarm);
        stop_alarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });
        scheduled_alarm_time_in_ms = -1L;
        alarm_set = false;
        ms_until_alarm = 0L;
        updateSavedVariable();
        Log.v("Initialize Variables AlarmActivity", "initializeVariables() ending");
    }

    private void stopAlarm() {
        if(ringtone.isPlaying()){
            ringtone.stop();
            Intent MainActivityIntent = new Intent(this.getApplicationContext(), MainActivity.class);
            MainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(MainActivityIntent);
        }
    }

    private void updateSavedVariable() {
        Log.v("UpdateSavedVariable", "updateSavedVariable() called");

        SharedPreferences saved_values = getSharedPreferences(MainActivity.GEDDER_ALARM_SAVED_VARIABLES, 0);
        SharedPreferences.Editor editor = saved_values.edit();
        editor.putBoolean(MainActivity.GEDDER_ALARM_WAS_ALARM_SET, alarm_set);
        editor.putLong(MainActivity.GEDDER_ALARM_MILL_UNTIL_ALARM, ms_until_alarm);
        editor.putLong(MainActivity.GEDDER_ALARM_ALARM_TIME_IN_MILL, scheduled_alarm_time_in_ms);
        editor.apply();

        Log.v("UpdateSavedVariable", "updateSavedVariable() ending");
    }
}