/*
 * USER: jameskluz, mslm
 * DATE: 2/24/17
 */

package com.gedder.gedderalarm;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gedder.gedderalarm.alarm.AlarmClock;
import com.gedder.gedderalarm.alarm.AlarmClocksCursorAdapter;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.db.AlarmClockDBSchema;

import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    // TODO: See todo in com.gedder.gedderalarm.alarm.AlarmClocksCursorAdapter.

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String SERIALIZED_ALARM_CLOCK = "_GEDDER_SERIALIZED_ALARM_CLOCK_";

    private final int intentId = 31582;

    private ListView alarmClocksListView;
    private AlarmClocksCursorAdapter mAlarmClocksCursorAdapter;
    private Cursor mAlarmClockCursor;

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
        mAlarmClockCursor = db.rawQuery(
                "SELECT * FROM " + AlarmClockDBSchema.AlarmClockTable.TABLE_NAME, null);

        // Make an adapter based off of the cursor.
        mAlarmClocksCursorAdapter = new AlarmClocksCursorAdapter(this, mAlarmClockCursor);

        // Attach the adapter to the list view which we'll populate.
        alarmClocksListView = (ListView) findViewById(R.id.alarm_clocks_list);
        alarmClocksListView.setAdapter(mAlarmClocksCursorAdapter);

        // When an alarm in the list is touched, we go to the alarm edit activity.
        alarmClocksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Some alarm clock item on the list of alarms is clicked.
                // TODO: bring to the alarm edit activity.
            }
        });

        db.close();
    }

    /**
     * Called by some view when a new alarm is to be made. Brings up some alarm creation activity.
     * @param view The view that references this function.
     */
    public void newAlarm(View view) {
        // Pass in new, default alarm.
        Intent intent = new Intent(this, AlarmEditScrollingActivity.class);
        intent.putExtra(SERIALIZED_ALARM_CLOCK, new AlarmClock(this.getApplicationContext()));
        startActivity(intent);
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
        AlarmClock alarmClock = db.getAlarmClock(this.getApplicationContext(), uuid);
        db.deleteAlarmClock(uuid);
        db.close();

        updateAlarmClockCursorAdapter();
    }

    /**
     * Toggles the alarm. Does not reset any data.
     * @param alarmClock
     */
    private void toggleAlarm(AlarmClock alarmClock) {
        // TODO: Use updated updateAlarmClock function later.
        // TODO: Make sure to turn off gedder functionality when toggling alarm.

        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        db.updateAlarmClock(alarmClock.getUUID(), alarmClock.getAlarmTime(), !alarmClock.isSet());
        db.close();

        // We notify the adapter to update the views to match changes.
        updateAlarmClockCursorAdapter();
    }

    private void updateAlarmClockCursorAdapter() {
        // TODO: Need to find a nicer way to do this. Maybe CursorWrapper will do the trick, dunno.
        AlarmClockDBHelper helper = new AlarmClockDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        mAlarmClockCursor = db.rawQuery(
                "SELECT * FROM " + AlarmClockDBSchema.AlarmClockTable.TABLE_NAME, null);
        mAlarmClocksCursorAdapter.changeCursor(mAlarmClockCursor);

        db.close();
    }
}

