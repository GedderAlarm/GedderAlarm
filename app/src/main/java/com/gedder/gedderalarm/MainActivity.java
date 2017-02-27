package com.gedder.gedderalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by jameskluz on 2/24/17.
 */

public class MainActivity extends AppCompatActivity {
    //This is connected to the display for seconds
    TextView seconds_text;
    //This is connected to the button to start or cancel the alarm
    Button start_cancel_btn;
    //this is (currently) connected to the "add 10 seconds" button
    Button set_time_btn;
    //this is how much time we have for alarm in milliseconds
    //everything that has to do with time in android is done with milliseconds
    long milliseconds_until_alarm;
    public static final String gedder_alarm_saved_variables = "__GEDDER_ALARM_SAVED_VARIABLES__";
    static long scheduled_alarm_time_in_milliseconds;
    static boolean alarm_set;
    AlarmManager alarmManager;
    final int intent_id = 31582;

    //this is always called when an activity (can think of Activity == 1 screen) is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
    }

    private void initializeVariables() {
        //THIS NEEDS TO BE CHANGED HERE WHEN WE INTRODUCE SAVED VARIABLES:
        SharedPreferences saved_values = getSharedPreferences(gedder_alarm_saved_variables, 0);
        alarm_set = saved_values.getBoolean("__WAS_ALARM_SET__", false);
        milliseconds_until_alarm = saved_values.getLong("__MILL_UNTIL_ALARM__", 0L);
        scheduled_alarm_time_in_milliseconds = saved_values.getLong("__ALARM_TIME_IN_MILL__", -1L);


        //Define Views (buttons and text to show seconds for alarm)
        //we are connecing these variables to the actual objects in UI
        seconds_text = (TextView) findViewById(R.id.seconds_for_alarm);
        start_cancel_btn = (Button) findViewById(R.id.start_cancel_btn);
        set_time_btn = (Button) findViewById(R.id.set_time);

        //set listeners for buttons (we're saying: "Call these functions when button is pushed")
        start_cancel_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startOrCancel();
            }
        });
        set_time_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //This will be called when alarms are set or go off etc...
        updateDynamicVariables();
        updateSavedVariable();
    }

    //this may or may not need to be public, we may need other classes to be able to call this
    //or we might make a class that contains global information like alarm times
    public void updateDynamicVariables(){
        //Set up text shown for start_cancel button
        if(alarm_set){
            start_cancel_btn.setText("CANCEL ALARM");
        } else {
            start_cancel_btn.setText("START ALARM");
        }
        seconds_text.setText(String.valueOf(milliseconds_until_alarm/1000) + " seconds");
    }

    private void updateSavedVariable(){
        SharedPreferences saved_values = getSharedPreferences(gedder_alarm_saved_variables, 0);
        SharedPreferences.Editor editor = saved_values.edit();
        editor.putBoolean("__WAS_ALARM_SET__", alarm_set);
        editor.putLong("__MILL_UNTIL_ALARM__", milliseconds_until_alarm);
        editor.putLong("__ALARM_TIME_IN_MILL__", scheduled_alarm_time_in_milliseconds);
        editor.commit();
    }

    private void setTime() {
        if(alarm_set){
            alarm_set = false;
            milliseconds_until_alarm = 5000L;
        } else {
            //add 10 seconds or 10000 milliseconds to alarm time
            milliseconds_until_alarm += 5000;
        }
        updateDynamicVariables();
        updateSavedVariable();
    }

    private void startOrCancel() {
        //This function is just for our Log, it's for debugging
        //I'm classifying all of my logs as errors, this is a little severe
        //but I find it makes it easier to filter through all of the noise
        //in the debugging window. I'm not married to this, we can change it
        Log.e("Start/Cancel Alarm", "Start/Cancel Alarm button Pressed");
        if(alarm_set){
            alarm_set = false;
            milliseconds_until_alarm = 0L;
            cancelAlarm();
        } else {
            alarm_set = true;
            startAlarm();
        }
        updateDynamicVariables();
        updateSavedVariable();
    }

    private void startAlarm() {
        Log.e("Start Alarm", "startAlarm method called");
        scheduled_alarm_time_in_milliseconds = System.currentTimeMillis() + milliseconds_until_alarm;
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this,
        //        1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        //alarmManager.set(AlarmManager.RTC_WAKEUP, scheduled_alarm_time_in_milliseconds, PendingIntent.getBroadcast(this,
        //        1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, intent_id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    scheduled_alarm_time_in_milliseconds, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, scheduled_alarm_time_in_milliseconds, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, scheduled_alarm_time_in_milliseconds, pendingIntent);
        }
    }

    private void cancelAlarm() {
        Log.e("Cancel Alarm", "cancelAlarm method called");
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, intent_id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}

