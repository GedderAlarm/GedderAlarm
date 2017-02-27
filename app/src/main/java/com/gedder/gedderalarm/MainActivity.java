package com.gedder.gedderalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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
    boolean alarm_set;
    AlarmManager alarmManager;

    //this is always called when an activity (can think of Activity == 1 screen) is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
    }

    private void initializeVariables() {
        //THIS NEEDS TO BE CHANGED HERE WHEN WE INTRODUCE SAVED VARIABLES:
        alarm_set = false;
        milliseconds_until_alarm = 0L;

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

        //This will be called when alarms are set or go off etc...
        updateDynamicVariables();
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

    private void setTime() {
        if(alarm_set){
            alarm_set = false;
            milliseconds_until_alarm = 5000L;
        } else {
            //add 10 seconds or 10000 milliseconds to alarm time
            milliseconds_until_alarm += 5000;
        }
        updateDynamicVariables();
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
    }

    private void startAlarm() {
        Log.e("Start Alarm", "startAlarm method called");
        Long alarmTime = System.currentTimeMillis() + milliseconds_until_alarm;
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this,
        //        1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, PendingIntent.getBroadcast(this,
                1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    alarmTime, PendingIntent.getBroadcast(this,
                            1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, PendingIntent.getBroadcast(this,
                    1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, PendingIntent.getBroadcast(this,
                    1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    private void cancelAlarm() {
        Log.e("Cancel Alarm", "cancelAlarm method called");
        if(alarmManager != null){
            //do something....
        }
    }
}
