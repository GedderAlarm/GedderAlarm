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

import com.gedder.gedderalarm.controller.AlarmClockCursorWrapper;
import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.model.AlarmClock;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm);

        // First thing's first: turn off the alarm internally.
        Intent intent = getIntent();
        UUID alarmUuid = (UUID) intent.getSerializableExtra(PARAM_ALARM_UUID);
        turnOffAlarm(alarmUuid);

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

        Button stopAlarmBtn = (Button) findViewById(R.id.button_stop_alarm);
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

        // Since the alarm just went off, we need to now internally say it's off.
        alarmClock.setAlarm(AlarmClock.OFF);
        alarmClock.turnGedderOff();
        db.updateAlarmClock(alarmClock);
        db.close();
    }

    private void turnOffAlarmSound() {
        if (ringtone.isPlaying()) {
            ringtone.stop();
        }
    }
}
