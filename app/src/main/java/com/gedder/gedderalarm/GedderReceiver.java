/*
 * USER: mslm
 * DATE: 3/31/2017
 */

package com.gedder.gedderalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 *
 */
public class GedderReceiver extends BroadcastReceiver {
    private static final String TAG = GedderReceiver.class.getSimpleName();

    public static final String PARAM_ORIGIN = "__PARAM_ORIGIN__";
    public static final String PARAM_DESTINATION = "__PARAM_DESTINATION__";
    public static final String PARAM_ARRIVAL_TIME = "__PARAM_ARRIVAL_TIME__";
    public static final String PARAM_PREP_TIME = "__PARAM_PREP_TIME__";
    public static final String PARAM_UPPER_BOUND_TIME = "__PARAM_UPPER_BOUND_TIME__";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
