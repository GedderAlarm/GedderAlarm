/*
 * USER: mslm
 * DATE: 3/11/2017
 */

package com.gedder.gedderalarm.db;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gedder.gedderalarm.alarm.AlarmClock;
import com.gedder.gedderalarm.db.AlarmClockDBSchema.AlarmClockTable;
import com.gedder.gedderalarm.db.AlarmClockDBSchema.UuidToIdTable;
import com.gedder.gedderalarm.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import static android.content.Context.ALARM_SERVICE;


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
        String CREATE_ALARM_CLOCK_TABLE =
                "CREATE TABLE " + AlarmClockTable.TABLE_NAME + "(" +
                            AlarmClockTable.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            AlarmClockTable.Columns.ALARM_TIME + " INT8," +
                            AlarmClockTable.Columns.ALARM_SET + " BOOLEAN" +
                        ")";

        String CREATE_UUID_TO_ID_TABLE =
                "CREATE TABLE " + UuidToIdTable.TABLE_NAME + "(" +
                        UuidToIdTable.Columns.UUID + " TEXT PRIMARY KEY," +
                        UuidToIdTable.Columns.ID + " INTEGER," +
                        "FOREIGN KEY(" +
                                UuidToIdTable.Columns.ID +
                            ")" +
                        "REFERENCES " + AlarmClockTable.TABLE_NAME + "(" +
                                UuidToIdTable.Columns.ID +
                            ")" +
                        ")";

        db.execSQL(CREATE_ALARM_CLOCK_TABLE);
        db.execSQL(CREATE_UUID_TO_ID_TABLE);
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
        String DROP_ALARM_CLOCK_TABLE = "DROP TABLE IF EXISTS " + AlarmClockTable.TABLE_NAME;
        String DROP_UUID_TO_ID_TABLE = "DROP TABLE IF EXISTS " + UuidToIdTable.TABLE_NAME;
        db.execSQL(DROP_ALARM_CLOCK_TABLE);
        db.execSQL(DROP_UUID_TO_ID_TABLE);
    }

    /**
     * Adds an alarm clock to the database, including the time for its alarm, and whether it is
     * currently set or not. Also adds its UUID to the database.
     * @param alarmClock The AlarmClock object to add to the database.
     */
    public void addAlarmClock(AlarmClock alarmClock) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Getting the next ID to map UUID to it in the uuidToID table.
        int nextId = getLastId() + 1;

        // SQLite alarm clock insertion strings
        String INSERT_ALARM_CLOCK =
                "INSERT INTO " + AlarmClockTable.TABLE_NAME + "(" +
                            AlarmClockTable.Columns.ALARM_TIME + "," +
                            AlarmClockTable.Columns.ALARM_SET +
                        ") VALUES (" +
                            alarmClock.getAlarmTime() + ", " + alarmClock.isSet() +
                        ")";

        // SQLite UUID-ID mapping insertion strings
        String INSERT_UUID_TO_ID =
                "INSERT INTO " + UuidToIdTable.TABLE_NAME + "(" +
                            UuidToIdTable.Columns.UUID + "," +
                            UuidToIdTable.Columns.ID +
                        ") VALUES (" +
                            alarmClock.getUUID() + "," + nextId +
                        ")";

        db.execSQL(INSERT_ALARM_CLOCK);
        db.execSQL(INSERT_UUID_TO_ID);
        db.close();
    }

    /**
     * Gets the alarm clock with UUID uuid, if it exists in the database.
     * @param context The required context for the alarm clock.
     * @param uuid The UUID of the alarm clock to get.
     * @return The AlarmClock object having UUID uuid.
     */
    public AlarmClock getAlarmClock(Context context, UUID uuid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + AlarmClockTable.Columns.ALARM_TIME + "," +
                            AlarmClockTable.Columns.ALARM_SET + " " +
                "FROM " + AlarmClockTable.TABLE_NAME + "," + UuidToIdTable.TABLE_NAME + " " +
                "WHERE " + AlarmClockTable.Columns.ID + "=" + UuidToIdTable.Columns.ID + " " +
                "AND " + UuidToIdTable.Columns.UUID + "='" + uuid.toString() + "'",
                null);

        if (cursor != null)
            cursor.moveToFirst();

        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            long scheduledAlarmTime = cursor.getLong(0);
            boolean alarmSet = cursor.getInt(1) > 0;
            cursor.close();
            return new AlarmClock(context, alarmManager, uuid, scheduledAlarmTime, alarmSet);
        } catch (NullPointerException e) {
            Log.e(TAG, "getAlarmClock() NullPointerException");
            return new AlarmClock(context);
        }
    }

    /**
     * Returns a list of all alarm clocks currently in the database, active or not.
     * @param context The required context for each alarm clock.
     * @return A list of all alarm clocks currently existing in the database.
     */
    public ArrayList<AlarmClock> getAllAlarmClocks(Context context) {
        ArrayList<AlarmClock> alarmClockList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + AlarmClockTable.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + AlarmClockTable.Columns.ALARM_TIME + "," +
                            AlarmClockTable.Columns.ALARM_SET + "," +
                            UuidToIdTable.Columns.UUID + " " +
                "FROM " + AlarmClockTable.TABLE_NAME + "," + UuidToIdTable.TABLE_NAME + " " +
                "WHERE " + AlarmClockTable.Columns.ID + "=" + UuidToIdTable.Columns.ID,
                null);

        if (cursor.moveToFirst()) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            do {
                long scheduledAlarmTime = cursor.getLong(0);
                boolean alarmSet = cursor.getInt(1) > 0;
                UUID uuid = UUID.fromString(cursor.getString(2));

                alarmClockList.add(new AlarmClock(context, alarmManager, uuid,
                        scheduledAlarmTime, alarmSet));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return alarmClockList;
    }

    /**
     * Returns the number of alarm clocks in the database, active or not.
     * @return The number of alarm clocks in the database.
     */
    public int getAlarmClockCount() {
        String countQuery = "SELECT  * FROM " + AlarmClockTable.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
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

        // First get the ID by matching for it with UUID in one query.
        // Then do the regular update using the convenience methods below.

        // Getting the ID associated with the input UUID.
        int id = getId(uuid);

        ContentValues contentValues = new ContentValues();
        contentValues.put(AlarmClockTable.Columns.ALARM_TIME, scheduledTimeInMs);
        contentValues.put(AlarmClockTable.Columns.ALARM_SET, alarmSet);

        return db.update(AlarmClockTable.TABLE_NAME, contentValues,
                AlarmClockTable.Columns.ID + "=?", new String[] { String.valueOf(id) });
    }

    /**
     * Updates an existing alarm clock to the parameters of the passed in alarm clock.
     * @param uuid The UUID of the alarm clock to update.
     * @param alarmClock The alarm clock whose values to copy into the alarm clock with UUID uuid.
     * @return Number of rows affected by the update. Not 1 if failed.
     */
    public int updateAlarmClock(UUID uuid, AlarmClock alarmClock) {
        return updateAlarmClock(uuid, alarmClock.getAlarmTime(), alarmClock.isSet());
    }

    /**
     * Deletes the alarm clock that has UUID uuid.
     * @param uuid The UUID of the alarm clock to delete.
     */
    public void deleteAlarmClock(UUID uuid) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id = getId(uuid);
        db.delete(AlarmClockTable.TABLE_NAME,
                AlarmClockTable.Columns.ID + "=?", new String[] { String.valueOf(id) });
        db.close();
    }

    /**
     * Gets the ID of the alarm clock with UUID uuid.
     * @param uuid UUID of the alarm clock to get the ID of.
     * @return The ID of the alarm clock associated with uuid. -1 if it doesn't exist.
     */
    private int getId(UUID uuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        int id = -1;
        Cursor idCursor = db.rawQuery(
                "SELECT " + UuidToIdTable.Columns.ID + " " +
                "FROM " + UuidToIdTable.TABLE_NAME + " " +
                "WHERE " + UuidToIdTable.Columns.UUID + "='" + uuid.toString() + "'",
                null);
        if (idCursor != null) {
            idCursor.moveToFirst();
            id = idCursor.getInt(0);
        }
        idCursor.close();
        db.close();
        return id;
    }

    /**
     * Gets the last ID in the database.
     * @return The last ID that exists in the database. -1 if no alarm clocks exist.
     */
    private int getLastId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor idCursor = db.rawQuery("SELECT seq FROM sqlite_sequence " +
                "WHERE NAME=\"" + AlarmClockTable.TABLE_NAME + "\"", null);
        int lastId = -1;
        if (idCursor != null) {
            idCursor.moveToFirst();
            lastId = idCursor.getInt(0);
        }
        idCursor.close();
        db.close();
        return lastId;
    }
}
