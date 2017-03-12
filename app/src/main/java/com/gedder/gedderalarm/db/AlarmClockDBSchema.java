/*
 * USER: mslm
 * DATE: 3/11/2017
 */

package com.gedder.gedderalarm.db;


/**
 *
 */
public class AlarmClockDBSchema {

    /**
     *
     */
    public static final class AlarmClockTable {
        public static final String TABLE_NAME = "alarmClocks";

        public static final class Columns {
            public static final String ID = "alarmClocks.id";
            public static final String ALARM_TIME = "alarmClocks.alarmTime";
            public static final String ALARM_SET = "alarmClocks.alarmSet";
        }
    }

    /**
     *
     */
    public static final class UuidToIdTable {
        public static final String TABLE_NAME = "uuidToId";

        public static final class Columns {
            public static final String UUID = "uuidToId.uuid";
            public static final String ID = "uuidToId.id";
        }
    }
}
