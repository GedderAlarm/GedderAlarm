/*
 * USER: mslm
 * DATE: 3/11/2017
 */

package com.gedder.gedderalarm.db;


public class AlarmClockDBSchema {
    public static final class AlarmClockTable {
        public static final String TABLE_NAME = "alarmClocks";

        public static final class Columns {
            public static final String UUID = "uuid";
            public static final String ALARM_TIME = "alarmTime";
            public static final String ALARM_SET = "alarmSet";
        }
    }
}
