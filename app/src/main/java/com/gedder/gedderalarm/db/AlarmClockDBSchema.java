/*
 * USER: mslm
 * DATE: 3/11/2017
 */

package com.gedder.gedderalarm.db;


/**
 * The schema of the application's alarm clock database.
 */
public class AlarmClockDBSchema {

    /**
     * Contains data for each alarm clock.
     */
    public static final class AlarmClockTable {
        public static final String TABLE_NAME = "alarmClocks";

        public static final class Columns {
            /** Incrementing ID */
            public static final String ID = "_id";

            /** The time in milliseconds for the alarm since the epoch. */
            public static final String ALARM_TIME = TABLE_NAME + "_alarmTime";

            /** Whether the alarm is set or not. */
            public static final String ALARM_SET = TABLE_NAME + "_alarmSet";
        }
    }

    /**
     * Contains a UUID to ID mapping for each alarm clock.
     */
    public static final class UuidToIdTable {
        public static final String TABLE_NAME = "uuidToId";

        public static final class Columns {
            /** The UUID of some alarm clock. */
            public static final String UUID = TABLE_NAME + "_uuid";

            /** The ID associated with some UUID of an alarm clock. */
            public static final String ID = TABLE_NAME + "_id";
        }
    }
}
