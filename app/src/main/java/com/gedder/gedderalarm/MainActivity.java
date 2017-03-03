package com.gedder.gedderalarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gedder.gedderalarm.util.Log;


/**
 * USER: jameskluz
 * DATE: 2/24/17
 */

public class MainActivity extends AppCompatActivity {
    // This is connected to the display for seconds.
    private TextView seconds_text;

    // This is connected to the button to start or cancel the alarm.
    private Button start_cancel_btn;

    // This is (currently) connected to the "add 10 seconds" button.
    private Button set_time_btn;

    // This is how much time we have for alarm in milliseconds.
    // NOTE: Everything that has to do with time in android is done with milliseconds.
    private long ms_until_alarm;

    // Keys.
    public static final String GEDDER_ALARM_SAVED_VARIABLES = "__GEDDER_ALARM_SAVED_VARIABLES__";
    public static final String GEDDER_ALARM_WAS_ALARM_SET = "__GEDDER_ALARM_WAS_ALARM_SET__";
    public static final String GEDDER_ALARM_MILL_UNTIL_ALARM = "__GEDDER_ALARM_MILL_UNTIL_ALARM__";
    public static final String GEDDER_ALARM_ALARM_TIME_IN_MILL =
            "__GEDDER_ALARM_ALARM_TIME_IN_MILL__";
    public static final String GEDDER_ALARM_APP_SHUTDOWN_CORRECTLY = "__GEDDER_ALARM_SHUT_DOWN_CORRECTLY__";

    // Other necessary private variables.
    private static long scheduled_alarm_time_in_ms;
    private static boolean alarm_set;
    //I have an idea for this that isn't fully fleshed out yet
    private static boolean app_shut_down_correctly;
    private AlarmManager alarmManager;
    private final int intent_id = 31582;

    /** This is always called when an activity (can think of Activity == 1 screen) is created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
    }

    private void initializeVariables() {
        Log.v("Initialize Variables MainActivity", "initializeVariables() called");

        SharedPreferences saved_values = getSharedPreferences(GEDDER_ALARM_SAVED_VARIABLES, 0);
        alarm_set = saved_values.getBoolean(GEDDER_ALARM_WAS_ALARM_SET, false);
        ms_until_alarm = saved_values.getLong(GEDDER_ALARM_MILL_UNTIL_ALARM, 0L);
        if(!alarm_set){
            ms_until_alarm = 0L;
        }
        scheduled_alarm_time_in_ms = saved_values.getLong(GEDDER_ALARM_ALARM_TIME_IN_MILL, -1L);
        //I have an idea for this that isn't fully fleshed out yet
        app_shut_down_correctly = saved_values.getBoolean(GEDDER_ALARM_APP_SHUTDOWN_CORRECTLY , false);

        //I have an idea for this that isn't fully fleshed out yet
        if(!app_shut_down_correctly){
            //resolveBadShutDown();
        }

        /*
         * Define Views (buttons and text to show seconds for alarm)
         * we are connecting these variables to the actual objects in UI
         */
        seconds_text = (TextView) findViewById(R.id.seconds_for_alarm);
        start_cancel_btn = (Button) findViewById(R.id.start_cancel_btn);
        set_time_btn = (Button) findViewById(R.id.stop_alarm);

        // Set listeners for buttons (we're saying: "Call these functions when button is pushed").
        start_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrCancel();
            }
        });
        set_time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // This will be called when alarms are set or go off etc...
        updateDynamicVariables();
        updateSavedVariable();

        Log.v("Initialize Variables MainActivity", "initializeVariables() ending");
    }

    //I have an idea for this that isn't fully fleshed out yet
    private void resolveBadShutDown() {
        alarm_set = false;
        ms_until_alarm = 0L;
        scheduled_alarm_time_in_ms = -1L;
    }

    /**
     * This may or may not need to be public, we may need other classes to be able to call this.
     * Or we might make a class that contains global information like alarm times.
    */
    private void updateDynamicVariables() {
        Log.v("UpdateDynamicVariables", "updateDynamicVariables() called");

        // Set up text shown for start_cancel button
        if (alarm_set) {
            start_cancel_btn.setText("CANCEL ALARM");
        } else {
            start_cancel_btn.setText("START ALARM");
        }
        seconds_text.setText(String.valueOf(ms_until_alarm/1000) + " seconds");

        Log.v("UpdateDynamicVariables", "updateDynamicVariables() ending");
    }

    private void updateSavedVariable() {
        Log.v("UpdateSavedVariable", "updateSavedVariable() called");

        SharedPreferences saved_values = getSharedPreferences(GEDDER_ALARM_SAVED_VARIABLES, 0);
        SharedPreferences.Editor editor = saved_values.edit();
        editor.putBoolean(GEDDER_ALARM_WAS_ALARM_SET, alarm_set);
        editor.putLong(GEDDER_ALARM_MILL_UNTIL_ALARM, ms_until_alarm);
        editor.putLong(GEDDER_ALARM_ALARM_TIME_IN_MILL, scheduled_alarm_time_in_ms);
        editor.apply();

        Log.v("UpdateSavedVariable", "updateSavedVariable() ending");
    }

    private void setTime() {
        Log.v("Set Time", "setTime() called");

        if (alarm_set) {
            alarm_set = false;
            ms_until_alarm = 5000L;
        } else {
            // Add 10 seconds or 10000 milliseconds to alarm time.
            ms_until_alarm += 5000;
        }
        updateDynamicVariables();
        updateSavedVariable();

        Log.v("Set Time", "setTime() ending");
    }

    private void startOrCancel() {
        /*
         * This function below is just for our Log, it's for debugging
         * I'm classifying all of my logs as errors, this is a little severe
         * but I find it makes it easier to filter through all of the noise
         * in the debugging window. I'm not married to this, we can change it
         */
        Log.e("Start/Cancel Alarm", "Start/Cancel Alarm button pressed");
        Log.v("Start/Cancel Alarm", "startOrCancel() called");

        if(alarm_set) {
            alarm_set = false;
            ms_until_alarm = 0L;
            cancelAlarm();
        } else {
            alarm_set = true;
            startAlarm();
        }
        updateDynamicVariables();
        updateSavedVariable();

        Log.v("Start/Cancel Alarm", "startOrCancel() ending");
    }

    private void startAlarm() {
        Log.e("Start Alarm", "startAlarm() called");
        Log.v("Start Alarm", "startAlarm() called");

        scheduled_alarm_time_in_ms = System.currentTimeMillis() + ms_until_alarm;

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, intent_id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT >= 23) {
            Log.v("Start Alarm", "Build.VERSION.SDK_INT >= 23");
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, scheduled_alarm_time_in_ms, pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= 19) {
            Log.v("Start Alarm", "19 <= Build.VERSION.SDK_INT < 23");
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, scheduled_alarm_time_in_ms, pendingIntent
            );
        } else {
            Log.v("Start Alarm", "Build.VERSION.SDK_INT < 19");
            alarmManager.set(AlarmManager.RTC_WAKEUP, scheduled_alarm_time_in_ms, pendingIntent);
        }

        Log.v("Start Alarm", "startAlarm() ending");
    }

    private void cancelAlarm() {
        Log.e("Cancel Alarm", "cancelAlarm() called");
        Log.v("Cancel Alarm", "cancelAlarm() called");

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, intent_id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        alarmManager.cancel(pendingIntent);

        Log.v("Cancel Alarm", "cancelAlarm() ending");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.v("On Pause", "onPause() called");
    }

    @Override
    protected void onDestroy(){
        super.onPause();
        Log.v("On Destroy", "onDestroy() called");
    }
}

