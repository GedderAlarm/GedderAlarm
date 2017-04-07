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
import android.widget.Toast;

import com.gedder.gedderalarm.controller.AlarmClockCursorWrapper;
import com.gedder.gedderalarm.controller.AlarmClocksCursorAdapter;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.model.AlarmClock;

import java.util.Calendar;
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
                AlarmClockDBHelper db = new AlarmClockDBHelper(
                        GedderAlarmApplication.getAppContext());
                AlarmClockCursorWrapper cursor = new AlarmClockCursorWrapper(
                        db.getAlarmClock(UUID.fromString(view.getTag().toString())));
                cursor.moveToFirst();
                Intent intent = new Intent(GedderAlarmApplication.getAppContext(),
                        AddEditAlarmScrollingActivity.class);
                intent.putExtra(PARCEL_ALARM_CLOCK, cursor.getAlarmClock());
                startActivityForResult(intent, mIntentRequestCode);
                db.close();
            }
        });
        alarmClocksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               // First make visible the delete button.
               findViewById(R.id.activityMain_DeleteAlarmBtn).setVisibility(View.VISIBLE);
               AlarmClockDBHelper db = new AlarmClockDBHelper(GedderAlarmApplication.getAppContext());
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

               db.close();
               return true;
           }
       });
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
        for (int i = 0; i < alarmClocksListView.getCount(); ++i) {
            // Get a row.
            View child = alarmClocksListView.getChildAt(i);
            CheckBox cb = (CheckBox) child.findViewById(R.id.itemAlarmClock_removeCheckBox);
            cb.setVisibility(View.GONE);
            if (cb.isChecked()) {
                // Uncheck each.
                cb.setChecked(false);

                // Get the UUID for this item.
                UUID uuid = UUID.fromString(child.getTag().toString());
                AlarmClockCursorWrapper cursor = new AlarmClockCursorWrapper(
                        db.getAlarmClock(uuid));
                cursor.moveToFirst();
                AlarmClock alarmClock = cursor.getAlarmClock();
                // Turn off any remaining alarms.
                if (alarmClock.isAlarmOn()) {
                    alarmClock.toggleAlarm();
                }
                if (alarmClock.isGedderOn()) {
                    alarmClock.toggleGedder();
                }
                // Delete.
                db.deleteAlarmClock(UUID.fromString(child.getTag().toString()));
            }
        }
        // DB is presumably different, so tell the adapter that.
        mAlarmClocksCursorAdapter.changeCursor(db.getAllAlarmClocks());
        view.setVisibility(View.GONE);
        db.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mIntentRequestCode) {
            if (resultCode == RESULT_OK) {
                AlarmClock alarmClock = data.getParcelableExtra(PARCEL_ALARM_CLOCK);
                //For testing
                Calendar temp_cal = alarmClock.getAlarmTime();
                String hour = Integer.toString(temp_cal.get(Calendar.HOUR_OF_DAY));
                String minute = Integer.toString(temp_cal.get(Calendar.MINUTE));
                Toast.makeText(this, "Hour: " + hour + " Minute: " + minute, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

