/*
 * USER: jameskluz
 * DATE: 3/1/17
 */

package com.gedder.gedderalarm;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.gedder.gedderalarm.util.Log;


public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = AlarmActivity.class.getSimpleName();

    private Ringtone ringtone;

    // This is used to get the ringtone.
    private Uri alert;

    // This links us to the "stop alarm" button.
    private Button stopAlarmBtn;

    private static long sScheduledAlarmTimeInMs;
    private static boolean sAlarmSet;
    private long msUntilAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        initializeVariables();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();

        if (ringtone.isPlaying())
            ringtone.stop();
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        super.onDestroy();

        if (ringtone.isPlaying())
            ringtone.stop();
        finish();
    }

    /**
     * This essentially disables the back button, if removed things will break
     * unless someone addresses what should happen on back press
     */
    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed()");
    }

    private void initializeVariables() {
        Log.v(TAG, "initializeVariables()");

        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            // Alert is null, using backup.
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                // Alert backup is null, using 2nd backup.
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        ringtone = RingtoneManager.getRingtone(this, alert);
        ringtone.play();
        stopAlarmBtn = (Button) findViewById(R.id.button_stop_alarm);
        stopAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });
        sScheduledAlarmTimeInMs = -1L;
        sAlarmSet = false;
        msUntilAlarm = 0L;
        updateSavedVariable();
    }

    private void stopAlarm() {
        if (ringtone.isPlaying())
            ringtone.stop();
        finish();
    }

    private void updateSavedVariable() {
        Log.e(TAG, "updateSavedVariable()");

        SharedPreferences saved_values = getSharedPreferences(MainActivity.PREF_SAVED_VARIABLES, 0);
        SharedPreferences.Editor editor = saved_values.edit();
        editor.putBoolean(MainActivity.PREF_WAS_ALARM_SET, sAlarmSet);
        editor.putLong(MainActivity.PREF_MS_UNTIL_ALARM, msUntilAlarm);
        editor.putLong(MainActivity.PREF_ALARM_TIME_IN_MS, sScheduledAlarmTimeInMs);
        editor.apply();
    }
}
