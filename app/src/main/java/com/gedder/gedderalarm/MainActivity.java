/*
 * USER: jameskluz, mslm
 * DATE: 2/24/17
 */

package com.gedder.gedderalarm;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gedder.gedderalarm.controller.AlarmClocksCursorAdapter;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.model.AlarmClock;

/**
 *
 */

public class MainActivity extends AppCompatActivity {
    // TODO: Stop handling UI in this activity. Move it to a view class in the view package.
    // See http://www.techyourchance.com/mvp-mvc-android-2/

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PARCEL_ALARM_CLOCK = "_GEDDER_PARCEL_ALARM_CLOCK_";

    private final int intentId = 31582;

    private ListView alarmClocksListView;
    private AlarmClocksCursorAdapter mAlarmClocksCursorAdapter;
    private Cursor mAlarmClockCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a cursor pointing to all currently saved alarm clocks.
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        db.addAlarmClock(new AlarmClock());
        db.addAlarmClock(new AlarmClock());
        mAlarmClockCursor = db.getAllAlarmClocks();

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
    public void onClickNewAlarm(View view) {
        // Pass in new, default alarm.
        Intent intent = new Intent(this, AddEditAlarmScrollingActivity.class);
        intent.putExtra(PARCEL_ALARM_CLOCK, new AlarmClock());
        startActivity(intent);
    }
}

