/*
 * USER: mslm
 * DATE: 3/25/17
 */

package com.gedder.gedderalarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gedder.gedderalarm.model.AlarmClock;

/** The activity where the user edits an alarm, new or old. */

public class AddEditAlarmScrollingActivity extends AppCompatActivity {
    private static final String TAG = AddEditAlarmScrollingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit_scrolling);

        // Get the alarm clock in question.
        AlarmClock alarmClock = getIntent().getParcelableExtra(MainActivity.PARCEL_ALARM_CLOCK);

        // Programmatically change settings of views to match this alarm clock's settings.
    }

    /**
     *
     * @param view
     */
    public void cancel(View view) {
        finish();
    }

    /**
     *
     * @param view
     */
    public void done(View view) {
        finish();
    }
}
