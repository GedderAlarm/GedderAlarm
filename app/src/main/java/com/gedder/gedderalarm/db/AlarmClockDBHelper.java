/*
 * USER: mslm
 * DATE: 3/11/2017
 */

package com.gedder.gedderalarm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gedder.gedderalarm.AlarmClock;
import com.gedder.gedderalarm.MainActivity;
import com.gedder.gedderalarm.db.AlarmClockDBSchema.AlarmClockTable;

import java.util.List;


/**
 * A SQLite wrapper class to help in creating, reading, updating, and deleting Alarm Clocks.
 */
public class AlarmClockDBHelper extends SQLiteOpenHelper {
    private static final String TAG = AlarmClockDBHelper.class.getSimpleName();
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "alarmClockDatabase.db";

    /**
     * Default constructor requiring context.
     * @param context The context of the database.
     */
    public AlarmClockDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Creates all tables for the database.
     * @param db The database we create tables for.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARM_CLOCK_TABLE =
                "CREATE TABLE " + AlarmClockTable.TABLE_NAME + "(" +
                        AlarmClockTable.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AlarmClockTable.Columns.UUID + ", " +
                        AlarmClockTable.Columns.ALARM_TIME + " INTEGER," +
                        AlarmClockTable.Columns.ALARM_SET + " BOOLEAN" +
                        ")";
        db.execSQL(CREATE_ALARM_CLOCK_TABLE);
    }

    /**
     * Will drop all tables if they exist and recreate them.
     * @param db The database to drop and recreate.
     * @param oldVersion Old version of the database.
     * @param newVersion New version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_ALARM_CLOCK_TABLE = "DROP TABLE IF EXISTS " + AlarmClockTable.TABLE_NAME;
        db.execSQL(DROP_ALARM_CLOCK_TABLE);
    }

    /**
     *
     * @param alarmClock
     */
    public void addAlarmClock(AlarmClock alarmClock) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(AlarmClockTable.Columns.UUID, alarmClock.getUUID().toString());
        contentValues.put(AlarmClockTable.Columns.ALARM_TIME, alarmClock.getAlarmTime());
        contentValues.put(AlarmClockTable.Columns.ALARM_SET, alarmClock.isSet());

        db.insert(AlarmClockTable.TABLE_NAME, null, contentValues);
        db.close();
    }

    /**
     *
     * @param id
     * @return
     */
    public AlarmClock getAlarmClock(Context context, int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(AlarmClockTable.TABLE_NAME,
                new String[] {
                        AlarmClockTable.Columns.ID,
                        AlarmClockTable.Columns.UUID,
                        AlarmClockTable.Columns.ALARM_TIME,
                        AlarmClockTable.Columns.ALARM_SET
                },
                AlarmClockTable.Columns.ID + "=?",
                new String[] { String.valueOf(id)},
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        //AlarmClock alarmClock = new AlarmClock(context);
    }

    /**
     *
     * @return
     */
    public List<AlarmClock> getAllAlarmClocks() {

    }

    /**
     *
     * @return
     */
    public int getAlarmClockCount() {

    }

    /**
     *
     * @param alarmClock
     * @return
     */
    public int updateAlarmClock(AlarmClock alarmClock) {

    }

    /**
     *
     * @param alarmClock
     */
    public void deleteAlarmClock(AlarmClock alarmClock) {

    }
}
