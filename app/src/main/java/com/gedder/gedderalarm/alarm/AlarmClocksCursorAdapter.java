/*
 * USER: mslm
 * DATE: 3/15/17
 */

package com.gedder.gedderalarm.alarm;

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
 * Provides a custom ArrayAdapter for the AlarmClock class.
 * Can be used, for example, to populate ListViews with alarm clocks.
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
        // TODO: Implement a CursorWrapper to more easily retrieve an AlarmClock from a cursor.
        // Get our views; we will populate them.
        TextView time = (TextView) view.findViewById(R.id.alarm_clock_time);
        TextView repeat = (TextView) view.findViewById(R.id.alarm_clock_repeat_dates);
        Button gedderToggle = (Button) view.findViewById(R.id.gedder_alarm_toggle_btn);
        Button alarmToggle = (Button) view.findViewById(R.id.alarm_clock_toggle_btn);
        // Get data from cursor.
        long alarmTime = cursor.getLong(cursor.getColumnIndexOrThrow(
                AlarmClockDBSchema.AlarmClockTable.Columns.ALARM_TIME));
        int alarmSet = cursor.getInt(cursor.getColumnIndexOrThrow(
                AlarmClockDBSchema.AlarmClockTable.Columns.ALARM_SET));
        // Populate our views with that data.
        time.setText(String.valueOf(alarmTime));
        alarmToggle.setText(alarmSet > 0 ? "Turn off" : "Turn on");
    }
}
