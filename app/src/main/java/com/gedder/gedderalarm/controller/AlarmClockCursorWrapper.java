/*
 * USER: mslm
 * DATE: 4/5/2017
 */

package com.gedder.gedderalarm.controller;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.gedder.gedderalarm.db.AlarmClockDBSchema.AlarmClockTable;
import com.gedder.gedderalarm.model.AlarmClock;
import com.gedder.gedderalarm.util.DaysOfWeek;

import java.util.UUID;

/** A cursor wrapper to easily retrieve alarm clocks from a cursor. */

public class AlarmClockCursorWrapper extends CursorWrapper {
    private static final String TAG = AlarmClockCursorWrapper.class.getSimpleName();

    public AlarmClockCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public AlarmClock getAlarmClock() {
        return new AlarmClock(
                UUID.fromString(getString(getColumnIndexOrThrow(AlarmClockTable.Columns.UUID))),
                getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.REQUEST_CODE)),
                getString(getColumnIndexOrThrow(AlarmClockTable.Columns.ORIGIN_ID)),
                getString(getColumnIndexOrThrow(AlarmClockTable.Columns.ORIGIN_ADDRESS)),
                getString(getColumnIndexOrThrow(AlarmClockTable.Columns.DESTINATION_ID)),
                getString(getColumnIndexOrThrow(AlarmClockTable.Columns.DESTINATION_ADDRESS)),
                new DaysOfWeek(getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.REPEAT_DAYS))),
                DaysOfWeek.DAY.getDay(getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.ALARM_DAY))),
                getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.ALARM_HOUR)),
                getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.ALARM_MINUTE)),
                DaysOfWeek.DAY.getDay(getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.ARRIVAL_DAY))),
                getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.ARRIVAL_HOUR)),
                getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.ARRIVAL_MINUTE)),
                getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.PREP_HOUR)),
                getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.PREP_MINUTE)),
                DaysOfWeek.DAY.getDay(getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.UPPER_BOUND_DAY))),
                getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.UPPER_BOUND_HOUR)),
                getInt(getColumnIndexOrThrow(AlarmClockTable.Columns.UPPER_BOUND_MINUTE))
        );
    }
}
