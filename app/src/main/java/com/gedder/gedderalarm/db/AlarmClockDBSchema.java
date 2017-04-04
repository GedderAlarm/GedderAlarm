/*
 * USER: mslm
 * DATE: 3/11/2017
 */

package com.gedder.gedderalarm.db;

/** Contains the schema for our application's database. */

public class AlarmClockDBSchema {

    /** Contains data for each alarm clock. */

    public static final class AlarmClockTable {
        public static final String TABLE_NAME = "alarmClocks";

        public static final class Columns {
            /**  */
            public static final String ID = "_id";
            public static final String UUID = "uuid";
            public static final String REQUEST_CODE = "requestCode";

            /**  */
            public static final String REPEAT_DAYS = "repeatDays";

            /**  */
            public static final String ORIGIN = "origin";
            public static final String DESTINATION = "destination";

            /**  */
            public static final String ALARM_DAY = "alarmDay";
            public static final String ALARM_HOUR = "alarmHour";
            public static final String ALARM_MINUTE = "alarmMinute";
            public static final String ALARM_TIME = "alarmTime";

            /**  */
            public static final String ARRIVAL_DAY = "arrivalDay";
            public static final String ARRIVAL_HOUR = "arrivalHour";
            public static final String ARRIVAL_MINUTE = "arrivalMinute";
            public static final String ARRIVAL_TIME = "arrivalTime";

            /**  */
            public static final String PREP_HOUR = "prepHour";
            public static final String PREP_MINUTE = "prepMinute";
            public static final String PREP_TIME = "prepTime";

            /**  */
            public static final String UPPER_BOUND_DAY = "upperBoundDay";
            public static final String UPPER_BOUND_HOUR = "upperBoundHour";
            public static final String UPPER_BOUND_MINUTE = "upperBoundMinute";
            public static final String UPPER_BOUND_TIME = "upperBoundTime";

            /**  */
            public static final String ALARM_SET = "alarmSet";
            public static final String GEDDER_SET = "gedderSet";
        }
    }
}
