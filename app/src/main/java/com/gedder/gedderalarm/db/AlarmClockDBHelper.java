/*
 * USER: mslm
 * DATE: 3/11/2017
 */

package com.gedder.gedderalarm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gedder.gedderalarm.db.AlarmClockDBSchema.AlarmClockTable;


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
}
