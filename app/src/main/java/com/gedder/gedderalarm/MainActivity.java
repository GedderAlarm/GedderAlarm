/*
 * USER: jameskluz, mslm
 * DATE: 2/24/17
 */

package com.gedder.gedderalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.gedder.gedderalarm.alarm.AlarmClock;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.util.Log;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final int intentId = 31582;

    private ArrayList<AlarmClock> mAlarmClocks;
    private ArrayAdapter<AlarmClock> mAlarmClocksAdapter;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        mAlarmClocks = db.getAllAlarmClocks(this);
        mAlarmClocksAdapter = new ArrayAdapter<>(this,
                R.layout.item_alarm_clock, mAlarmClocks);

    }

    /**
     *
     */
    @Override
    protected void onResume() {
        Log.e(TAG, "onResume()");
        super.onResume();

    }

    /**
     *
     */
    @Override
    protected void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();

    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        super.onDestroy();

    }

    /**
     * Called by some view when a new alarm is to be made. Brings us to the alarm creation activity.
     * @param view The view that references this function.
     */
    public void newAlarm(View view) {

    }
}

