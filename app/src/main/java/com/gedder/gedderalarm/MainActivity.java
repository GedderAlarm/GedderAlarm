/*
 * USER: jameskluz, mslm
 * DATE: 2/24/17
 */

package com.gedder.gedderalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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

import java.util.UUID;

/**
 *
 */

public class MainActivity extends AppCompatActivity {
    // TODO: Stop handling UI in this activity. Move it to a view class in the view package.
    // See http://www.techyourchance.com/mvp-mvc-android-2/

    public static final String PARCEL_ALARM_CLOCK = "_GEDDER_PARCEL_ALARM_CLOCK_";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ID_GEDDER_PERSISTENT_NOTIFICATION = 5323321;

    private final int mIntentRequestCode = 31582;

    private ListView alarmClocksListView;
    private AlarmClocksCursorAdapter mAlarmClocksCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmClockDBHelper db = new AlarmClockDBHelper(this);

        /////////////////////////////
        // FOR TESTING ONLY.
        if (db.getAlarmClockCount() == 0)
        {
            db.addAlarmClock(new AlarmClock());
            db.addAlarmClock(new AlarmClock());
            db.addAlarmClock(new AlarmClock());
            db.addAlarmClock(new AlarmClock());
        }
        //
        /////////////////////////////

        Cursor mAlarmClockCursor = db.getAllAlarmClocks();

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
                AlarmClock alarmClock = getAlarmClockInListViewFromChild(view);
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
    public void onResume() {
        AlarmClockDBHelper db = new AlarmClockDBHelper(GedderAlarmApplication.getAppContext());
        mAlarmClocksCursorAdapter = new AlarmClocksCursorAdapter(this, db.getAllAlarmClocks());
        alarmClocksListView.setAdapter(mAlarmClocksCursorAdapter);
        db.close();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.activityMain_DeleteAlarmBtn).getVisibility() != View.GONE) {
            findViewById(R.id.activityMain_DeleteAlarmBtn).setVisibility(View.GONE);
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

                AlarmClock alarmClock = getAlarmClockInListViewFromChild(child);
                alarmClock.turnAlarmOff();
                if (alarmClock.isGedderOn()) {
                    alarmClock.turnGedderOff();
                    cancelGedderPersistentIcon();
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
        View row              = (View) view.getParent();
        AlarmClock alarmClock = getAlarmClockInListViewFromChild(row);

        if (alarmClock.isGedderOn()) {
            // CASE: Gedder is on.
            alarmClock.toggleGedder();
            toastMessage("Gedder service off");
            cancelGedderPersistentIcon();
        } else {
            if (alarmClock.getOriginId().equals("") || alarmClock.getDestinationId().equals("")) {
                // CASE: Gedder is off but missing required information.
                Intent intent = new Intent(GedderAlarmApplication.getAppContext(),
                        AddEditAlarmScrollingActivity.class);
                intent.putExtra(PARCEL_ALARM_CLOCK, alarmClock);
                startActivityForResult(intent, mIntentRequestCode);
            } else if (!alarmClock.isAlarmOn()) {
                // CASE: Gedder is off and alarm is off.
                alarmClock.toggleAlarm();
                alarmClock.toggleGedder();
                toastMessage("Gedder service on.");
                setGedderPersistentIcon();
            } else {
                // CASE: Gedder is off and alarm is on.
                alarmClock.toggleGedder();
                toastMessage("Gedder service on.");
                setGedderPersistentIcon();
            }
        }

        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        db.updateAlarmClock(alarmClock);
        mAlarmClocksCursorAdapter.changeCursor(db.getAllAlarmClocks());
        db.close();
    }

    public void onClickToggleAlarm(View view) {
        View row              = (View) view.getParent();
        AlarmClock alarmClock = getAlarmClockInListViewFromChild(row);

        alarmClock.toggleAlarm();
        if (alarmClock.isAlarmOn()) {
            toastMessage("Alarm set.");
        } else {
            toastMessage("Alarm off.");
        }

        if (alarmClock.isGedderOn()) {
            alarmClock.toggleGedder();
            toastMessage("Gedder service off.");
            cancelGedderPersistentIcon();
        }

        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        db.updateAlarmClock(alarmClock);
        mAlarmClocksCursorAdapter.changeCursor(db.getAllAlarmClocks());
        db.close();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mIntentRequestCode) {
            if (resultCode == RESULT_OK) {
                AlarmClockDBHelper db = new AlarmClockDBHelper(this);
                AlarmClock alarmClock = data.getParcelableExtra(PARCEL_ALARM_CLOCK);
                alarmClock.turnAlarmOn();
                Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();

                if (alarmClock.isGedderEligible() && !alarmClock.isGedderOn()) {
                    alarmClock.turnGedderOn();
                } else if (!alarmClock.isGedderEligible() && alarmClock.isGedderOn()) {
                    alarmClock.turnGedderOff();
                }

                db.updateAlarmClock(alarmClock);
                mAlarmClocksCursorAdapter.changeCursor(db.getAllAlarmClocks());
            }
        }
    }

    public static AlarmClock getAlarmClockInListViewFromChild(View view) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(GedderAlarmApplication.getAppContext());
        AlarmClockCursorWrapper cursor = new AlarmClockCursorWrapper(
                db.getAlarmClock(UUID.fromString(view.getTag().toString())));
        cursor.moveToFirst();
        AlarmClock alarmClock = cursor.getAlarmClock();
        cursor.close();
        db.close();
        return alarmClock;
    }

    private void setGedderPersistentIcon() {
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.gedder_on)
                .setContentTitle("Gedder Alarm")
                .setContentText("Gedder service on.")
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .build();
        NotificationManager notifier =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notifier.notify(ID_GEDDER_PERSISTENT_NOTIFICATION, notification);
    }

    private void cancelGedderPersistentIcon() {
        ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(ID_GEDDER_PERSISTENT_NOTIFICATION);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

