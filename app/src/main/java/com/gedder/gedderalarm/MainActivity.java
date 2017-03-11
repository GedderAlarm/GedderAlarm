/*
 * USER: jameskluz
 * DATE: 2/24/17
 */

package com.gedder.gedderalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gedder.gedderalarm.util.Log;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Keys.
    public static final String GEDDER_ALARM_SAVED_VARIABLES = "__GEDDER_ALARM_SAVED_VARIABLES__";
    public static final String GEDDER_ALARM_WAS_ALARM_SET = "__GEDDER_ALARM_WAS_ALARM_SET__";
    public static final String GEDDER_ALARM_MILL_UNTIL_ALARM = "__GEDDER_ALARM_MILL_UNTIL_ALARM__";
    public static final String GEDDER_ALARM_ALARM_TIME_IN_MILL =
            "__GEDDER_ALARM_ALARM_TIME_IN_MILL__";

    private final int intentId = 31582;

    // This is connected to the display for seconds.
    private TextView secondsText;

    // This is connected to the button to start or cancel the alarm.
    private Button startCancelBtn;

    // This is (currently) connected to the "add 10 seconds" button.
    private Button setTimeBtn;

    // This is how much time we have for alarm in milliseconds.
    // NOTE: Everything that has to do with time in android is done with milliseconds.
    private long msUntilAlarm;

    // Other necessary private variables.
    private static long sScheduledAlarmTimeInMs;
    private static boolean sAlarmSet;
    private AlarmManager mAlarmManager;

    /** This is always called when an activity (can think of Activity == 1 screen) is created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume() called");

        super.onResume();
        getSavedValues();
        updateDynamicVariables();

        Log.e(TAG, "onResume() ending");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.v(TAG, "onPause() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.v(TAG, "onDestroy() called");
    }

    private void initializeVariables() {
        Log.v(TAG, "initializeVariables() called");

        getSavedValues();

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
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // This will be called when alarms are set or go off etc...
        updateDynamicVariables();
        updateSavedVariable();

        Log.v(TAG, "initializeVariables() ending");
    }

    private void updateDynamicVariables() {
        Log.v(TAG, "updateDynamicVariables() called");

        // Set up text shown for start_cancel button
        if (sAlarmSet)
            startCancelBtn.setText("CANCEL ALARM");
        else
            startCancelBtn.setText("START ALARM");

        secondsText.setText(String.valueOf(msUntilAlarm/1000) + " seconds");

        Log.v(TAG, "updateDynamicVariables() ending");
    }

    private void updateSavedVariable() {
        Log.v(TAG, "updateSavedVariable() called");

        SharedPreferences saved_values = getSharedPreferences(GEDDER_ALARM_SAVED_VARIABLES, 0);
        SharedPreferences.Editor editor = saved_values.edit();
        editor.putBoolean(GEDDER_ALARM_WAS_ALARM_SET, sAlarmSet);
        editor.putLong(GEDDER_ALARM_MILL_UNTIL_ALARM, msUntilAlarm);
        editor.putLong(GEDDER_ALARM_ALARM_TIME_IN_MILL, sScheduledAlarmTimeInMs);
        editor.apply();

        Log.v(TAG, "updateSavedVariable() ending");
    }

    private void getSavedValues() {
        SharedPreferences saved_values = getSharedPreferences(GEDDER_ALARM_SAVED_VARIABLES, 0);
        sAlarmSet = saved_values.getBoolean(GEDDER_ALARM_WAS_ALARM_SET, false);
        msUntilAlarm = saved_values.getLong(GEDDER_ALARM_MILL_UNTIL_ALARM, 0L);
        if (!sAlarmSet)
            msUntilAlarm = 0L;
        sScheduledAlarmTimeInMs = saved_values.getLong(GEDDER_ALARM_ALARM_TIME_IN_MILL, -1L);
    }

    private void setTime() {
        Log.v(TAG, "setTime() called");

        if (sAlarmSet) {
            sAlarmSet = false;
            msUntilAlarm = 5000L;
        } else {
            // Add 10 seconds or 10000 milliseconds to alarm time.
            msUntilAlarm += 5000;
        }
        updateDynamicVariables();
        updateSavedVariable();

        Log.v(TAG, "setTime() ending");
    }

    private void startOrCancel() {
        Log.e(TAG, "Start/Cancel Alarm button pressed");
        Log.v(TAG, "startOrCancel() called");

        if (sAlarmSet) {
            sAlarmSet = false;
            msUntilAlarm = 0L;
            cancelAlarm();
        } else {
            sAlarmSet = true;
            startAlarm();
        }
        updateDynamicVariables();
        updateSavedVariable();

        Log.v(TAG, "startOrCancel() ending");
    }

    private void startAlarm() {
        Log.e(TAG, "startAlarm() called");
        Log.v(TAG, "startAlarm() called");

        sScheduledAlarmTimeInMs = System.currentTimeMillis() + msUntilAlarm;

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, intentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= 23) {
            Log.v(TAG, "Build.VERSION.SDK_INT >= 23");
            mAlarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, sScheduledAlarmTimeInMs, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            Log.v(TAG, "19 <= Build.VERSION.SDK_INT < 23");
            mAlarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, sScheduledAlarmTimeInMs, pendingIntent);
        } else {
            Log.v(TAG, "Build.VERSION.SDK_INT < 19");
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, sScheduledAlarmTimeInMs, pendingIntent);
        }

        Log.v(TAG, "startAlarm() ending");
    }

    private void cancelAlarm() {
        Log.e(TAG, "cancelAlarm() called");
        Log.v(TAG, "cancelAlarm() called");

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, intentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);

        Log.v(TAG, "cancelAlarm() ending");
    }
}

