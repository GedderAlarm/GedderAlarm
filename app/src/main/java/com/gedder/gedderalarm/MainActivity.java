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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.gedder.gedderalarm.controller.AlarmClockCursorWrapper;
import com.gedder.gedderalarm.controller.AlarmClocksCursorAdapter;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.model.AlarmClock;

import java.util.UUID;

/**
 *
 */

public class MainActivity extends AppCompatActivity {
    // TODO: Stop handling UI in this activity. Move it to a view class in the view package.
    // See http://www.techyourchance.com/mvp-mvc-android-2/

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PARCEL_ALARM_CLOCK = "_GEDDER_PARCEL_ALARM_CLOCK_";

    private final int mIntentRequestCode = 31582;

    private ListView alarmClocksListView;
    private AlarmClocksCursorAdapter mAlarmClocksCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Cursor mAlarmClockCursor;

        // Get a cursor pointing to all currently saved alarm clocks.
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);

        /////////////////////////////
        if (db.getAlarmClockCount() == 0) {
            db.addAlarmClock(new AlarmClock());
            db.addAlarmClock(new AlarmClock());
            db.addAlarmClock(new AlarmClock());
            db.addAlarmClock(new AlarmClock());
        }
        /////////////////////////////

        mAlarmClockCursor = db.getAllAlarmClocks();

        // Make an adapter based off of the cursor.
        mAlarmClocksCursorAdapter = new AlarmClocksCursorAdapter(this, mAlarmClockCursor);

        // Attach the adapter to the list view which we'll populate.
        alarmClocksListView = (ListView) findViewById(R.id.alarm_clocks_list);
        alarmClocksListView.setAdapter(mAlarmClocksCursorAdapter);
        db.close();

        // When an alarm in the list is clicked, go to the add/edit activity with that alarm's info.
        alarmClocksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlarmClock alarmClock = getAlarmClockFromView(view);
                Intent intent = new Intent(GedderAlarmApplication.getAppContext(),
                        AddEditAlarmScrollingActivity.class);
                intent.putExtra(PARCEL_ALARM_CLOCK, alarmClock);
                startActivityForResult(intent, mIntentRequestCode);
            }
        });
        // When an alarm in the list is long-clicked, we activate deletion mode.
        alarmClocksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               // First make visible the delete button.
               findViewById(R.id.activityMain_DeleteAlarmBtn).setVisibility(View.VISIBLE);
               // Now loop through all rows and make visible the checkboxes.
               for (int i = 0; i < parent.getCount(); ++i) {
                   View child = parent.getChildAt(i);
                   View item = child.findViewById(R.id.itemAlarmClock_removeCheckBox);
                   CheckBox cb = (CheckBox) item.findViewById(R.id.itemAlarmClock_removeCheckBox);
                   cb.setVisibility(View.VISIBLE);
               }
               // Finally, the item initially long-clicked is checked.
               CheckBox cb = (CheckBox) view.findViewById(R.id.itemAlarmClock_removeCheckBox);
               cb.setChecked(true);
               return true;
           }
       });
    }

    @Override
    public void onBackPressed() {
        // Check to see if we're in alarm clock delete state.
        if (findViewById(R.id.activityMain_DeleteAlarmBtn).getVisibility() != View.GONE) {
            // Make the delete button invisible (gone).
            findViewById(R.id.activityMain_DeleteAlarmBtn).setVisibility(View.GONE);
            // Now loop through all rows and make hide the checkboxes.
            for (int i = 0; i < alarmClocksListView.getCount(); ++i) {
                View child = alarmClocksListView.getChildAt(i);
                View item = child.findViewById(R.id.itemAlarmClock_removeCheckBox);
                CheckBox cb = (CheckBox) item.findViewById(R.id.itemAlarmClock_removeCheckBox);
                cb.setChecked(false);
                cb.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Called by some view when a new alarm is to be made. Brings up some alarm creation activity.
     * @param view The view that references this function.
     */
    public void onClickNewAlarm(View view) {
        // Pass in new, default alarm.
        Intent intent = new Intent(this, AddEditAlarmScrollingActivity.class);
        intent.putExtra(PARCEL_ALARM_CLOCK, new AlarmClock());
        startActivityForResult(intent, mIntentRequestCode);
    }

    public void onClickDeleteAlarm(View view) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        // For each row, uncheck the checkbox, hide it, and delete alarms which were checked.
        for (int i = 0; i < alarmClocksListView.getCount(); ++i) {
            View child = alarmClocksListView.getChildAt(i);
            CheckBox cb = (CheckBox) child.findViewById(R.id.itemAlarmClock_removeCheckBox);
            cb.setVisibility(View.GONE);
            if (cb.isChecked()) {
                cb.setChecked(false);

                AlarmClock alarmClock = getAlarmClockFromView(child);
                if (alarmClock.isAlarmOn()) {
                    alarmClock.toggleAlarm();
                }
                if (alarmClock.isGedderOn()) {
                    alarmClock.toggleGedder();
                }
                db.deleteAlarmClock(UUID.fromString(child.getTag().toString()));
            }
        }
        // DB is presumably different, so tell the adapter that.
        mAlarmClocksCursorAdapter.changeCursor(db.getAllAlarmClocks());
        view.setVisibility(View.GONE);
        db.close();
    }

    public void onClickToggleGedder(View view) {
        ToggleButton tb = (ToggleButton) view;
        View row = (View) view.getParent();
        AlarmClock alarmClock = getAlarmClockFromView(row);
        if (alarmClock.isGedderOn()) {
            alarmClock.toggleGedder();
            tb.setChecked(false);
        } else {
            if (alarmClock.getOriginId().equals("") || alarmClock.getDestinationId().equals("")) {
                // Gedder information is incomplete. Go request for it.
                Intent intent = new Intent(GedderAlarmApplication.getAppContext(),
                        AddEditAlarmScrollingActivity.class);
                intent.putExtra(PARCEL_ALARM_CLOCK, alarmClock);
                startActivityForResult(intent, mIntentRequestCode);
                tb.setChecked(false);
            } else if (!alarmClock.isAlarmOn()) {
                // Alarm is off but trying to activate Gedder. So turn alarm on too.
                alarmClock.toggleAlarm();
                alarmClock.toggleGedder();
                tb.setChecked(true);
            } else {
                alarmClock.toggleGedder();
                tb.setChecked(true);
            }
        }
    }

    public void onClickToggleAlarm(View view) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mIntentRequestCode) {
            if (resultCode == RESULT_OK) {
                AlarmClock alarmClock = data.getParcelableExtra(PARCEL_ALARM_CLOCK);
                mAlarmClocksCursorAdapter.changeCursor(new AlarmClockDBHelper(this).getAllAlarmClocks());
            }
        }
    }

    public static AlarmClock getAlarmClockFromView(View view) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(GedderAlarmApplication.getAppContext());
        AlarmClockCursorWrapper cursor = new AlarmClockCursorWrapper(
                db.getAlarmClock(UUID.fromString(view.getTag().toString())));
        cursor.moveToFirst();
        AlarmClock alarmClock = cursor.getAlarmClock();
        cursor.close();
        db.close();
        return alarmClock;
    }
}

