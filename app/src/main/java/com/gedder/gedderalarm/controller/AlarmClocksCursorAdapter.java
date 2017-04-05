/*
 * USER: mslm
 * DATE: 3/15/17
 */

package com.gedder.gedderalarm.controller;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gedder.gedderalarm.R;
import com.gedder.gedderalarm.db.AlarmClockDBSchema;

/**
 * <p>Provides a custom ArrayAdapter for the AlarmClock class.</p>
 *
 * <p>Can be used, for example, to populate ListViews with alarm clocks.</p>
 */

public class AlarmClocksCursorAdapter extends CursorAdapter {
    private static final String TAG = AlarmClocksCursorAdapter.class.getSimpleName();

    public AlarmClocksCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_alarm_clock, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get our views; we will populate them.
        TextView time = (TextView) view.findViewById(R.id.itemAlarmClock_WakeupTime);
        TextView repeat = (TextView) view.findViewById(R.id.itemAlarmClock_RepeatDates);
        Button gedderToggle = (Button) view.findViewById(R.id.itemAlarmClock_GedderAlarmToggleBtn);
        Button alarmToggle = (Button) view.findViewById(R.id.itemAlarmClock_alarmClockToggleBtn);
        // Get data from cursor.
        long alarmTime = cursor.getLong(cursor.getColumnIndexOrThrow(
                AlarmClockDBSchema.AlarmClockTable.Columns.ALARM_TIME));
        int alarmSet = cursor.getInt(cursor.getColumnIndexOrThrow(
                AlarmClockDBSchema.AlarmClockTable.Columns.ALARM_SET));
        // Populate our views with that data.
        time.setText(String.valueOf(alarmTime));
//        alarmToggle.setText(alarmSet > 0 ? "Turn off" : "Turn on");
        view.setTag(cursor.getString(cursor.getColumnIndexOrThrow(
                AlarmClockDBSchema.AlarmClockTable.Columns.UUID)));
    }
}
