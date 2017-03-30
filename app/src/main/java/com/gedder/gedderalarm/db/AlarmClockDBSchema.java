/*
 * USER: mslm
 * DATE: 3/11/2017
 */

package com.gedder.gedderalarm.db;


public class AlarmClockDBSchema {
    /**
     * Contains data for each alarm clock.
     */
    public static final class AlarmClockTable {
        public static final String TABLE_NAME = "alarmClocks";

        public static final class Columns {
            public static final String ID = "_id";
            public static final String UUID = TABLE_NAME + "_uuid";
            public static final String ALARM_TIME = TABLE_NAME + "_alarmTime";
            public static final String ALARM_SET = TABLE_NAME + "_alarmSet";
        }
    }
}
