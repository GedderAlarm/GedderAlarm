/*
 * USER: mslm
 * DATE: 3/15/17
 */

package com.gedder.gedderalarm.alarm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gedder.gedderalarm.R;
import com.gedder.gedderalarm.alarm.AlarmClock;

import java.util.ArrayList;


/**
 * Provides a custom ArrayAdapter for the AlarmClock class.
 * Can be used, for example, to populate ListViews with alarm clocks.
 */
public class AlarmClocksAdapter extends ArrayAdapter<AlarmClock> {
    private static final String TAG = AlarmClocksAdapter.class.getSimpleName();

    public AlarmClocksAdapter(Context context, int resource, ArrayList<AlarmClock> alarmClocks) {
        super(context, resource, alarmClocks);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        AlarmClock alarmClock = getItem(position);
        // Check to see if an existing view is being reused.
        if (convertView == null)
            convertView = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_alarm_clock, parent, false);
        // Get our text views; we will populate them.
        TextView time = (TextView) convertView.findViewById(R.id.alarm_clock_time);
        TextView set = (TextView) convertView.findViewById(R.id.alarm_clock_set);
        // Populate.
        time.setText(String.valueOf(alarmClock.getAlarmTime()));
        set.setText(alarmClock.isSet() ? "set" : "not set");
        return convertView;
    }
}
