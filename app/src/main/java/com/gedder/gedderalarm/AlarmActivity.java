/*
 * USER: jameskluz, mslm
 * DATE: 3/1/17
 */

package com.gedder.gedderalarm;

import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gedder.gedderalarm.controller.AlarmClockCursorWrapper;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.model.AlarmClock;
import com.gedder.gedderalarm.model.GedderEngine;

import java.util.Calendar;
import java.util.UUID;

public class AlarmActivity extends AppCompatActivity {
    public static final String PARAM_ALARM_UUID = "__PARAM_ALARM_UUID__";

    private static final String TAG = AlarmActivity.class.getSimpleName();

    // This is used to get the ringtone.
    private Uri alert;
    private Ringtone ringtone;
    private TextView mInfoDisplay;
    private int mDuration;
    private int mDurationTraffic;
    private int mPrepTime;
    boolean mWarnLessPrep;
    private String mDestination;
    private String mOrigin;
//    private TextView mDisplayCurrentTime;
//    private TextView mDisplayCurrentDate;
    private TextView mLeaveByMinutes;
    private TextView mLeaveByTimeDisplay;
    private TextView mArriveTimeDisplay;
    private Button mGoogleMapsBtn;
    private Button mSnoozeBtn;
    private Button mStopAlarmBtn;
    private Calendar mCurrentTime;
    private Calendar mArriveTime;
    private Calendar mAlarmTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // First thing's first: turn off the alarm internally.
        Intent intent = getIntent();
        Bundle results = intent.getBundleExtra("bundle");
        UUID alarmUuid = (UUID) intent.getSerializableExtra(PARAM_ALARM_UUID);
        turnOffAlarm(alarmUuid);
        mCurrentTime = Calendar.getInstance();
        // Now play the alarm sound.
        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            // Use backup.
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                // 2nd backup.
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        ringtone = RingtoneManager.getRingtone(this, alert);
        ringtone.play();

        if (results != null) {
            gedder_initialize(results);
        } else {
            alarm_initialize();
        }
        mStopAlarmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnOffAlarmSound();
                finish();
            }
        });
        mSnoozeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                snooze();
            }
        });
//        mDisplayCurrentTime.setText(returnTimeAsString(mCurrentTime));
//        mDisplayCurrentDate.setText(returnDateAsString(mCurrentTime));
//        mInfoDisplay = (TextView) findViewById(R.id.alarm_display_info);
//        String displayStr = "";
//
//        //this was a Gedder Alarm
//        if (results != null) {
//            Button mMapsBtn = (Button) findViewById(R.id.get_directions);
//            mMapsBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    try {
//                        String origin_address = mOrigin.replaceAll(" ", "+");
//                        String destination_address = mDestination.replaceAll(" ", "+");
//                        String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+origin_address+","+"&daddr="+ destination_address;
//                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
//                        startActivity(Intent.createChooser(intent, "Select an application"));
//                    } catch (Exception e){
//                        Toast.makeText(getBaseContext(),
//                                "Trouble opening Google Maps, please make sure it is installed!",
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//            displayStr += "GEDDER ALARM!\n\n";
//            int travel_time_min = results.getInt(GedderEngine.RESULT_DURATION) / 60;
//            double travel_time_hour = travel_time_min / 60;
//            travel_time_min %= 60;
//            String travel_time_string = "";
//            if (travel_time_hour > 0) {
//                travel_time_string += String.valueOf(travel_time_hour) + " hours(s) and ";
//            }
//            travel_time_string += String.valueOf(travel_time_min) + " minute(s).";
//            displayStr += "Travel Time: " + travel_time_string + "\n\n";
//            displayStr += "Prep Time: " + String.valueOf(mPrepTime) + " minute(s)\n\n";
//            displayStr += "DESTINATION:\n" + mDestination;
//
//        } else {  // This was a regular alarm.
//            displayStr += "ALARM!";
//        }
//        mInfoDisplay.setText(displayStr);
//

//
//        Button stopAlarmBtn = (Button) findViewById(R.id.button_stop_alarm_2);
//        stopAlarmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                turnOffAlarmSound();
//                finish();
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        turnOffAlarmSound();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffAlarmSound();
        finish();
    }

    /** This disables the back button; the user should explicitly say what to do next. */
    @Override
    public void onBackPressed() {
        /* Intentionally empty */
    }

    /**
     * Turns off both the alarm clock and any associated services (i.e. Gedder).
     * @param uuid The UUID of the alarm clock in question.
     */
    private void turnOffAlarm(UUID uuid) {
        AlarmClockDBHelper db = new AlarmClockDBHelper(GedderAlarmApplication.getAppContext());
        AlarmClockCursorWrapper cursor = new AlarmClockCursorWrapper(db.getAlarmClock(uuid));
        cursor.moveToFirst();
        AlarmClock alarmClock = cursor.getAlarmClock();

        // Grab variables we need from the alarmClock.
        mPrepTime = (int) (alarmClock.getPrepTimeMillis() / 60000);
        mDestination = alarmClock.getDestinationAddress();
        mOrigin = alarmClock.getOriginAddress();
        mArriveTime = alarmClock.getArrivalTime();
        mAlarmTime = alarmClock.getAlarmTime();

        // Since the alarm just went off, we need to now internally say it's off.
        alarmClock.setAlarm(AlarmClock.OFF);

        if (alarmClock.isGedderOn()) {
            alarmClock.turnGedderOff();
            MainActivity.cancelGedderPersistentIcon();
        }

        db.updateAlarmClock(alarmClock);
        db.close();
    }

    private void turnOffAlarmSound() {
        if (ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    private void gedder_initialize(Bundle results){
        setContentView(R.layout.alarm_display_gedder);
//        mDisplayCurrentTime = (TextView) findViewById(R.id.gedderAlarmDisp_currentTime);
//        mDisplayCurrentDate = (TextView) findViewById(R.id.gedderAlarmDisp_currentDate);
        mLeaveByMinutes = (TextView) findViewById(R.id.gedderAlarmDisp_leaveByXminBox);
        Long arriveTimeMilli = mArriveTime.getTimeInMillis();
        int arriveTimeMin = (int)(arriveTimeMilli / 60000);
        int currentTimeMin = (int)(mCurrentTime.getTimeInMillis()/60000);
        int travelTimeMin = results.getInt(GedderEngine.RESULT_DURATION) / 60;
        int actualPrepMin = arriveTimeMin - (currentTimeMin + travelTimeMin);
        if (actualPrepMin < mPrepTime) {
            mWarnLessPrep = true;
        } else {
            mWarnLessPrep = false;
        }
        int actualPrepHours = actualPrepMin / 60;
        actualPrepMin %= 60;
        mLeaveByMinutes.setText("You have " + Integer.toString(actualPrepHours) + " hour(s) and "
                + Integer.toString(actualPrepMin) + " minute(s) until you need to leave.");
        if (mWarnLessPrep) {
            mLeaveByMinutes.setTextColor(Color.RED);
        } else {
            mLeaveByMinutes.setTextColor(Color.parseColor("#FF74BA59"));
        }
        mLeaveByTimeDisplay = (TextView) findViewById(R.id.gedderAlarmDisp_leaveByTime);
        Long leaveTimeMilli = arriveTimeMilli - (travelTimeMin*60000);
        Calendar leaveBy = Calendar.getInstance();
        leaveBy.setTimeInMillis(leaveTimeMilli);
        mLeaveByTimeDisplay.setText(returnTimeAsString(leaveBy));
        mArriveTimeDisplay = (TextView) findViewById(R.id.gedderAlarmDisp_getThereByTime);
        mArriveTimeDisplay.setText(returnTimeAsString(mArriveTime));
        mGoogleMapsBtn = (Button) findViewById(R.id.gedderAlarmDisp_GoogleMapsBtn);
        mGoogleMapsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String origin_address = mOrigin.replaceAll(" ", "+");
                    String destination_address = mDestination.replaceAll(" ", "+");
                    String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+origin_address+","+"&daddr="+ destination_address;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(Intent.createChooser(intent, "Select an application"));
                } catch (Exception e){
                    Toast.makeText(getBaseContext(),
                            "Trouble opening Google Maps, please make sure it is installed!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        mStopAlarmBtn = (Button) findViewById(R.id.gedderAlarmDisp_stopAlarmBtn);
        mSnoozeBtn = (Button) findViewById(R.id.gedderAlarmDisp_snoozeBtn);

//        mInfoDisplay = (TextView) findViewById(R.id.alarm_display_info);
//        String displayStr = "";
//
//        //this was a Gedder Alarm
//        if (results != null) {
//            Button mMapsBtn = (Button) findViewById(R.id.get_directions);
//            mMapsBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    try {
//                        String origin_address = mOrigin.replaceAll(" ", "+");
//                        String destination_address = mDestination.replaceAll(" ", "+");
//                        String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+origin_address+","+"&daddr="+ destination_address;
//                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
//                        startActivity(Intent.createChooser(intent, "Select an application"));
//                    } catch (Exception e){
//                        Toast.makeText(getBaseContext(),
//                                "Trouble opening Google Maps, please make sure it is installed!",
//                                Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//            displayStr += "GEDDER ALARM!\n\n";
//            int travel_time_min = results.getInt(GedderEngine.RESULT_DURATION) / 60;
//            double travel_time_hour = travel_time_min / 60;
//            travel_time_min %= 60;
//            String travel_time_string = "";
//            if (travel_time_hour > 0) {
//                travel_time_string += String.valueOf(travel_time_hour) + " hours(s) and ";
//            }
//            travel_time_string += String.valueOf(travel_time_min) + " minute(s).";
//            displayStr += "Travel Time: " + travel_time_string + "\n\n";
//            displayStr += "Prep Time: " + String.valueOf(mPrepTime) + " minute(s)\n\n";
//            displayStr += "DESTINATION:\n" + mDestination;
//
//        } else {  // This was a regular alarm.
//            displayStr += "ALARM!";
//        }
//        mInfoDisplay.setText(displayStr);
//
//        // Now play the alarm sound.
//        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alert == null) {
//            // Use backup.
//            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            if (alert == null) {
//                // 2nd backup.
//                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//            }
//        }
//        ringtone = RingtoneManager.getRingtone(this, alert);
//        ringtone.play();
//
//        Button stopAlarmBtn = (Button) findViewById(R.id.button_stop_alarm_2);
//        stopAlarmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                turnOffAlarmSound();
//                finish();
//            }
//        });
    }

    private void alarm_initialize() {
        setContentView(R.layout.alarm_display_normal);
//        mDisplayCurrentTime = (TextView) findViewById(R.id.normalAlarmDisp_currentTime);
//        mDisplayCurrentDate = (TextView) findViewById(R.id.normalAlarmDisp_currentDate);
        mStopAlarmBtn = (Button) findViewById(R.id.normalAlarmDisp_stopAlarmBtn);
        mSnoozeBtn = (Button) findViewById(R.id.normalAlarmDisp_snoozeBtn);
    }

    private String returnTimeAsString(Calendar time) {
        int hourOfDay = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        String am_or_pm;
        if (hourOfDay >= 12) {
            am_or_pm = "pm";
            hourOfDay = hourOfDay - 12;
        } else {
            am_or_pm = "am";
        }
        if (hourOfDay == 0) {
            hourOfDay = 12;
        }
        String hour_string = Integer.toString(hourOfDay);
        if(hourOfDay < 10) {
            hour_string = "0" + hour_string;
        }
        String minute_string = Integer.toString(minute);
        if(minute < 10) {
            minute_string = "0" + minute_string;
        }
        return (hour_string + ":" + minute_string + " " + am_or_pm);
    }

//    private String returnDateAsString(Calendar time) {
//        String date = "";
//
//        date += getMonth(time.get(Calendar.MONTH));
//    }

//    private String getMonth(int month) {
//        String monthString = "";
//        switch (month + 1) {
//            case 1:  monthString = "January";
//                break;
//            case 2:  monthString = "February";
//                break;
//            case 3:  monthString = "March";
//                break;
//            case 4:  monthString = "April";
//                break;
//            case 5:  monthString = "May";
//                break;
//            case 6:  monthString = "June";
//                break;
//            case 7:  monthString = "July";
//                break;
//            case 8:  monthString = "August";
//                break;
//            case 9:  monthString = "September";
//                break;
//            case 10: monthString = "October";
//                break;
//            case 11: monthString = "November";
//                break;
//            case 12: monthString = "December";
//                break;
//            default: monthString = "Invalid month";
//                break;
//        }
//        return monthString;
//    }

//    private String getDay(int day) {
//        String dayString = "";
//        switch (day) {
//            case 1:  dayString = "Monday";
//                break;
//            case 2:  dayString = "Tuesday";
//                break;
//            case 3:  dayString = "Wednesday";
//                break;
//            case 4:  dayString = "Thursday";
//                break;
//            case 5:  dayString = "Friday";
//                break;
//            case 6:  dayString = "Saturday";
//                break;
//            case 7:  dayString = "Sunday";
//            default: dayString = "Invalid month";
//                break;
//        }
//        return dayString;
//    }
    private void snooze(){

    }
}
