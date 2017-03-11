/*
 * USER: jameskluz, mslm
 * DATE: 2/24/17
 */

package com.gedder.gedderalarm;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gedder.gedderalarm.util.Log;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Keys.
    public static final String PREF_SAVED_VARIABLES = "__GEDDER_ALARM_SAVED_VARIABLES__";
    public static final String PREF_WAS_ALARM_SET = "__GEDDER_ALARM_WAS_ALARM_SET__";
    public static final String PREF_MS_UNTIL_ALARM = "__GEDDER_ALARM_MS_UNTIL_ALARM__";
    public static final String PREF_ALARM_TIME_IN_MS = "__GEDDER_ALARM_ALARM_TIME_IN_MS__";

    // This is connected to the display for seconds.
    private TextView secondsText;

    // This is connected to the button to start or cancel the alarm.
    private Button startCancelBtn;

    // This is (currently) connected to the "add 10 seconds" button.
    private Button setTimeBtn;

    // This is how much time we have for alarm in milliseconds.
    // NOTE: Everything that has to do with time in android is done with milliseconds.
    private long msUntilAlarm;
    private AlarmClock mAlarmClock;

    /** This is always called when an activity (can think of Activity == 1 screen) is created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume()");
        super.onResume();

        getSavedValues();
        updateDynamicVariables();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        super.onDestroy();
    }

    private void initializeVariables() {
        Log.v(TAG, "initializeVariables()");

        /*
         * Define Views (buttons and text to show seconds for alarm)
         * we are connecting these variables to the actual objects in UI
         */
        secondsText = (TextView) findViewById(R.id.text_seconds_for_alarm);
        startCancelBtn = (Button) findViewById(R.id.button_start_cancel);
        setTimeBtn = (Button) findViewById(R.id.button_stop_alarm);

        // Set listeners for buttons (we're saying: "Call these functions when button is pushed").
        startCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrCancel();
            }
        });
        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });
        mAlarmClock = new AlarmClock(this);
        msUntilAlarm = 0L;

        getSavedValues();

        // This will be called when alarms are set or go off etc...
        updateDynamicVariables();
        updateSavedVariable();
    }

    private void updateDynamicVariables() {
        Log.v(TAG, "updateDynamicVariables()");

        // Set up text shown for start_cancel button
        if (mAlarmClock.isSet())
            startCancelBtn.setText("CANCEL ALARM");
        else
            startCancelBtn.setText("START ALARM");

        secondsText.setText(String.valueOf(msUntilAlarm/1000) + " seconds");
    }

    private void updateSavedVariable() {
        Log.v(TAG, "updateSavedVariable()");

        SharedPreferences saved_values = getSharedPreferences(PREF_SAVED_VARIABLES, 0);
        SharedPreferences.Editor editor = saved_values.edit();
        editor.putLong(PREF_MS_UNTIL_ALARM, mAlarmClock.timeUntilAlarm());
        editor.apply();
    }

    private void getSavedValues() {
        Log.v(TAG, "getSavedValues()");

        SharedPreferences saved_values = getSharedPreferences(PREF_SAVED_VARIABLES, 0);
        msUntilAlarm = saved_values.getLong(PREF_MS_UNTIL_ALARM, 0L);
        if (!mAlarmClock.isSet())
            msUntilAlarm = 0L;
    }

    private void setTime() {
        Log.v(TAG, "setTime()");

        if (mAlarmClock.isSet()) {
            msUntilAlarm = 5000L;
        } else {
            msUntilAlarm += 5000;
        }
        updateDynamicVariables();
        updateSavedVariable();
    }

    private void startOrCancel() {
        Log.e(TAG, "Start/Cancel Alarm button pressed");
        Log.v(TAG, "startOrCancel()");

        if (mAlarmClock.isSet()) {
            cancelAlarm();
        } else {
            startAlarm();
        }
        updateDynamicVariables();
        updateSavedVariable();
    }

    private void startAlarm() {
        Log.e(TAG, "startAlarm()");
        Log.v(TAG, "startAlarm()");

        if (mAlarmClock.isSet())
            mAlarmClock.cancelAlarm();
        mAlarmClock.setAlarmTime(msUntilAlarm);
    }

    private void cancelAlarm() {
        Log.e(TAG, "cancelAlarm()");
        Log.v(TAG, "cancelAlarm()");

        if (mAlarmClock.isSet())
            mAlarmClock.cancelAlarm();

        msUntilAlarm = 0L;
        updateDynamicVariables();
    }
}

