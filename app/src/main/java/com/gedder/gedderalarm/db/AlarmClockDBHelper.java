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

import com.gedder.gedderalarm.model.AlarmClock;
import com.gedder.gedderalarm.db.AlarmClockDBSchema.AlarmClockTable;

import java.util.UUID;


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
     * @see com.gedder.gedderalarm.db.AlarmClockDBSchema
     * @param db The database we create tables for.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + AlarmClockTable.TABLE_NAME + "("
                + AlarmClockTable.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AlarmClockTable.Columns.UUID + " TEXT, "
                + AlarmClockTable.Columns.ALARM_TIME + " INT8, "
                + AlarmClockTable.Columns.ALARM_SET + " BOOLEAN)"
        );
    }

    /**
     * Will drop all tables if they exist and recreate them.
     * @see com.gedder.gedderalarm.db.AlarmClockDBSchema
     * @param db The database to drop and recreate.
     * @param oldVersion Old version of the database.
     * @param newVersion New version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmClockTable.TABLE_NAME);
        onCreate(db);
    }

    /**
     * Adds an alarm clock to the database, including the time for its alarm, and whether it is
     * currently set or not. Also adds its UUID to the database.
     * @param alarmClock The AlarmClock object to add to the database.
     */
    public void addAlarmClock(AlarmClock alarmClock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AlarmClockTable.Columns.UUID, alarmClock.getUUID().toString());
        cv.put(AlarmClockTable.Columns.ALARM_TIME, alarmClock.getAlarmTime());
        cv.put(AlarmClockTable.Columns.ALARM_SET, alarmClock.isOn());
        db.insert(AlarmClockTable.TABLE_NAME, null, cv);
        db.close();
    }

    /**
     * Gets the alarm clock with UUID uuid, if it exists in the database.
     * @param uuid The UUID of the alarm clock to get.
     * @return The AlarmClock object having UUID uuid.
     */
    public Cursor getAlarmClock(UUID uuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + AlarmClockTable.TABLE_NAME
                            + " WHERE " + AlarmClockTable.Columns.UUID + "=?",
                            new String[] { uuid.toString() });
    }

    /**
     * Returns a list of all alarm clocks currently in the database, active or not.
     * @return A list of all alarm clocks currently existing in the database.
     */
    public Cursor getAllAlarmClocks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + AlarmClockTable.TABLE_NAME, null);
    }

    /**
     * Returns the number of alarm clocks in the database, active or not.
     * @return The number of alarm clocks in the database.
     */
    public int getAlarmClockCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT  * FROM " + AlarmClockTable.TABLE_NAME, null).getCount();
    }

    /**
     * Updates an existing alarm clock to a new set of parameters.
     * @param uuid The UUID of the alarm clock to update.
     * @param scheduledTimeInMs The new scheduled time for the alarm clock.
     * @param alarmSet Whether this alarm is set or not.
     * @return Number of rows affected by the update. Not 1 if failed.
     */
    public int updateAlarmClock(UUID uuid, long scheduledTimeInMs, boolean alarmSet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AlarmClockTable.Columns.UUID, uuid.toString());
        cv.put(AlarmClockTable.Columns.ALARM_TIME, scheduledTimeInMs);
        cv.put(AlarmClockTable.Columns.ALARM_SET, alarmSet);
        return db.update(AlarmClockTable.TABLE_NAME, cv,
                AlarmClockTable.Columns.UUID + "=?", new String[] { uuid.toString() });
    }

    /**
     * Updates an existing alarm clock to the parameters of the passed in alarm clock.
     * @param alarmClock The alarm clock whose values to copy into the alarm clock with UUID uuid.
     * @return Number of rows affected by the update. Not 1 if failed.
     */
    public int updateAlarmClock(AlarmClock alarmClock) {
        return updateAlarmClock(alarmClock.getUUID(), alarmClock.getAlarmTime(), alarmClock.isOn());
    }

    /**
     * Deletes the alarm clock that has UUID uuid.
     * @param uuid The UUID of the alarm clock to delete.
     */
    public int deleteAlarmClock(UUID uuid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(AlarmClockTable.TABLE_NAME,
                AlarmClockTable.Columns.UUID + "=?", new String[] { uuid.toString() });
    }

    /**
     * Gets the last ID in the database.
     * @return The last ID that exists in the database. -1 if no alarm clocks exist.
     */
    private int getLastId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor idCursor = db.rawQuery("SELECT seq FROM sqlite_sequence " +
                "WHERE NAME=\"" + AlarmClockTable.TABLE_NAME + "\"", null);
        db.close();

        int lastId = -1;
        if (idCursor != null) {
            idCursor.moveToFirst();
            lastId = idCursor.getInt(0);
            idCursor.close();
        }
        return lastId;
    }
}
