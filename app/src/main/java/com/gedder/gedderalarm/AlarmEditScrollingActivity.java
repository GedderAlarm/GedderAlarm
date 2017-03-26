/*
 * USER: mslm
 * DATE: 3/25/17
 */

package com.gedder.gedderalarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


/**
 *
 */
public class AlarmEditScrollingActivity extends AppCompatActivity {
    private static final String TAG = AlarmEditScrollingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit_scrolling);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    public void cancel(View view) {

    }

    public void done(View view) {

    }
}
