/*
 * USER: jameskluz, mslm
 * DATE: 3/1/17
 */

package com.gedder.gedderalarm;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.gedder.gedderalarm.controller.AlarmClockCursorWrapper;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.model.AlarmClock;
import com.gedder.gedderalarm.model.GedderEngine;

import java.util.UUID;

/**
 *
 */

public class AlarmActivity extends AppCompatActivity {
    // TODO: @gil - UI.

    public static final String PARAM_ALARM_UUID = "__PARAM_ALARM_UUID__";

    private static final String TAG = AlarmActivity.class.getSimpleName();

    // This is used to get the ringtone.
    private Uri alert;
    private Ringtone ringtone;
    private TextView mInfoDisplay;
    private int mDuration;
    private int mDurationTraffic;
    private int mPrepTime;
    private String mDestination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm_2);

        // First thing's first: turn off the alarm internally.
        Intent intent = getIntent();
        Bundle results = intent.getBundleExtra("bundle");
        UUID alarmUuid = (UUID) intent.getSerializableExtra(PARAM_ALARM_UUID);
        turnOffAlarm(alarmUuid);

        mInfoDisplay = (TextView) findViewById(R.id.alarm_display_info);
        String displayStr = "";

        //this was a Gedder Alarm
        if (results != null) {
            displayStr += "GEDDER ALARM!\n\n";
            int travel_time_min = results.getInt(GedderEngine.RESULT_DURATION)/60;
            int travel_time_hour = travel_time_min / 60;
            travel_time_min = travel_time_hour % 60;
            String travel_time_string = "";
            if (travel_time_hour > 0) {
                travel_time_string += String.valueOf(travel_time_hour) + " hours(s) and ";
            }
            travel_time_string += String.valueOf(travel_time_min) + " minute(s).";
            displayStr += "Travel Time: " + travel_time_string + "\n\n";
            displayStr += "Prep Time: " + String.valueOf(mPrepTime) + " minute(s)\n\n";
            displayStr += "DESTINATION:\n" + mDestination;
            //UNSURE ABOUT THESE ARRAY LISTS, WILL FIX LATER
//            ArrayList<> warnings = results.(GedderEngine.RESULT_WARNINGS);
//            if (warnings != null && warnings.length > 0) {
//                String warnings_string = "WARNINGS:\n";
//                for (int i = 0; i < warnings.length; ++i) {
//                    warnings += "\t" + warnings[i];
//                }
//            }

        } else {  //this was a regular alarm
            displayStr += "ALARM!";
        }
        mInfoDisplay.setText(displayStr);

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

        Button stopAlarmBtn = (Button) findViewById(R.id.button_stop_alarm_2);
        stopAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffAlarmSound();
                finish();
            }
        });
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

        //Grab variables we need from the alarmClock
        mPrepTime = (int)(alarmClock.getPrepTimeMillis()/60000);
        mDestination = alarmClock.getDestinationAddress();

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
}
