/*
 * USER: jameskluz, mslm
 * DATE: 2/24/17
 */

package com.gedder.gedderalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gedder.gedderalarm.alarm.AlarmClock;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.util.Log;

import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    // TODO: Redo this with a cursor adapter. We are only showing/working with alarms IN the db.
    
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

        // Get currently saved alarm clocks.
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        mAlarmClocks = db.getAllAlarmClocks(this);
        // Attach our alarm clock list to the adapter.
        mAlarmClocksAdapter = new ArrayAdapter<>(this,
                R.layout.item_alarm_clock, mAlarmClocks);
        // Attach the adapter to the list view which we'll populate.
        ListView alarmClocksListView = (ListView) findViewById(R.id.alarm_clocks_list);
        alarmClocksListView.setAdapter(mAlarmClocksAdapter);

        // Anytime we call add/insert/remove/clear on the adapter, the view will automatically
        // update its data.
        mAlarmClocksAdapter.setNotifyOnChange(true);

        db.close();
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
     * Called by some view when a new alarm is to be made. Brings up some alarm creation
     * functionality.
     * @param view The view that references this function.
     */
    public void newAlarmBtnHandler(View view) {

    }

    /**
     *
     * @param alarmClock
     */
    private void addAlarm(AlarmClock alarmClock) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        db.addAlarmClock(alarmClock);
        mAlarmClocksAdapter.add(alarmClock);
        db.close();
    }

    /**
     *
     * @param alarmClock
     */
    private void removeAlarm(AlarmClock alarmClock) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        db.deleteAlarmClock(alarmClock.getUUID());
        mAlarmClocksAdapter.remove(alarmClock);
        db.close();
    }

    /**
     *
     * @param uuid
     */
    private void removeAlarm(UUID uuid) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        AlarmClock alarmClock = db.getAlarmClock(this, uuid);
        db.deleteAlarmClock(uuid);
        mAlarmClocksAdapter.remove(alarmClock);
        db.close();
    }

    /**
     * Toggles the alarm. Does not reset any data.
     * @param alarmClock
     */
    private void toggleAlarm(AlarmClock alarmClock) {
        if (alarmClock.isSet()) {
            alarmClock.cancelAlarm();
        } else {
            alarmClock.setAlarm();
        }
        // We notify the adapter to update the button text from "Unset" to "Set" and vice versa.
        mAlarmClocksAdapter.notifyDataSetChanged();
    }
}

