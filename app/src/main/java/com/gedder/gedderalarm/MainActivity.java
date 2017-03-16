/*
 * USER: jameskluz, mslm
 * DATE: 2/24/17
 */

package com.gedder.gedderalarm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.gedder.gedderalarm.alarm.AlarmClock;
import com.gedder.gedderalarm.alarm.AlarmClocksCursorAdapter;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.db.AlarmClockDBSchema;
import com.gedder.gedderalarm.util.Log;

import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    // TODO: Redo this with a cursor adapter. We are only showing/working with alarms IN the db.

    private static final String TAG = MainActivity.class.getSimpleName();

    private final int intentId = 31582;

    private AlarmClocksCursorAdapter mAlarmClocksCursorAdapter;
    private Cursor alarmClockCursor;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a cursor pointing to all currently saved alarm clocks.
        AlarmClockDBHelper helper = new AlarmClockDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        alarmClockCursor = db.rawQuery(
                "SELECT * FROM " + AlarmClockDBSchema.AlarmClockTable.TABLE_NAME, null);

        // Make an adapter based off of the cursor.
        mAlarmClocksCursorAdapter = new AlarmClocksCursorAdapter(this, alarmClockCursor);

        // Attach the adapter to the list view which we'll populate.
        ListView alarmClocksListView = (ListView) findViewById(R.id.alarm_clocks_list);
        alarmClocksListView.setAdapter(mAlarmClocksCursorAdapter);

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
        db.close();

        updateAlarmClockCursorAdapter();
    }

    /**
     *
     * @param alarmClock
     */
    private void removeAlarm(AlarmClock alarmClock) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        db.deleteAlarmClock(alarmClock.getUUID());
        db.close();

        updateAlarmClockCursorAdapter();
    }

    /**
     *
     * @param uuid
     */
    private void removeAlarm(UUID uuid) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        AlarmClock alarmClock = db.getAlarmClock(this, uuid);
        db.deleteAlarmClock(uuid);
        db.close();

        updateAlarmClockCursorAdapter();
    }

    /**
     * Toggles the alarm. Does not reset any data.
     * @param alarmClock
     */
    private void toggleAlarm(AlarmClock alarmClock) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        if (alarmClock.isSet()) {
            db.updateAlarmClock(alarmClock.getUUID(), alarmClock.getAlarmTime(), false);
        } else {
            db.updateAlarmClock(alarmClock.getUUID(), alarmClock.getAlarmTime(), true);
        }

        // We notify the adapter to update the button text from "Unset" to "Set" and vice versa.
        updateAlarmClockCursorAdapter();
    }

    private void updateAlarmClockCursorAdapter() {
        // TODO: Need to find a nicer way to do this. Maybe CursorWrapper will do the trick, dunno.
        AlarmClockDBHelper helper = new AlarmClockDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        alarmClockCursor = db.rawQuery(
                "SELECT * FROM " + AlarmClockDBSchema.AlarmClockTable.TABLE_NAME, null);
        mAlarmClocksCursorAdapter.changeCursor(alarmClockCursor);

        db.close();
    }
}

