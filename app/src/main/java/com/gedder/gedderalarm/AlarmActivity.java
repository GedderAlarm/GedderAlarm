package com.gedder.gedderalarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gedder.gedderalarm.util.Log;

/**
 * Created by jameskluz on 3/1/17.
 */

public class AlarmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        initializeVariables();
    }

    private void initializeVariables() {
        Log.v("Initialize Variables AlarmActivity", "initializeVariables() called");
        Log.v("Initialize Variables AlarmActivity", "initializeVariables() ending");
    }
}
