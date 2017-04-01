/*
 * USER: mslm
 * DATE: 3/31/2017
 */

package com.gedder.gedderalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gedder.gedderalarm.model.GedderEngine;

import java.util.ArrayList;


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
    public static final String PARAM_ID = "__PARAM_ID__";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
         * 1. Start the engine on the above variables. It'll go through the pipeline.
         * 2. Get back some result from it.
         * 3. Analyze result.
         *  3.1. If no delay, set a new alarm for x% of the time to alarm.
         *       ex: 6 hours to the alarm and no delay right now, so set it for 36 minutes from now.
         *  3.2. If delay, but not urgent, set for
         *       ((x/2)% of time to alarm)*(# of times delay encountered consecutively).
         *  3.3. If delay and urgent, sound the alarm through AlarmReceiver.
        */
        String origin         = intent.getStringExtra(PARAM_ORIGIN);
        String dest           = intent.getStringExtra(PARAM_DESTINATION);
        long   arrivalTime    = intent.getLongExtra(PARAM_ARRIVAL_TIME, -1);
        long   prepTime       = intent.getLongExtra(PARAM_PREP_TIME, -1);
        long   upperBoundTime = intent.getLongExtra(PARAM_UPPER_BOUND_TIME, -1);
        int    id             = intent.getIntExtra(PARAM_ID, -1);

        // We will need these calculations in the analysis.
        long   plannedLeaveTime   = upperBoundTime + prepTime;
        long   plannedTravelTime  = arrivalTime - plannedLeaveTime;

        if (origin == null || dest == null
                || origin.equals("") || dest.equals("")
                || arrivalTime == -1 || prepTime == -1 || upperBoundTime == -1
                || id == -1)
            throw new IllegalArgumentException(
                    "origin = " + origin
                    + "dest = " + dest
                    + "arrivalTime = " + arrivalTime
                    + "prepTime = " + prepTime
                    + "upperBoundTime = " + upperBoundTime
                    + "id = " + id);

        Bundle results = GedderEngine.start(origin, dest, arrivalTime, prepTime, upperBoundTime);
        int duration = results.getInt(GedderEngine.RESULT_DURATION, -1);
        int durationInTraffic = results.getInt(GedderEngine.RESULT_DURATION_IN_TRAFFIC, -1);
        ArrayList<String> warnings = (ArrayList<String>) results.getSerializable(GedderEngine.RESULT_WARNINGS);

        // Is Google Maps API detecting a longer trip than before? Then we have a delay.
        if (duration > plannedTravelTime) {
            long increasedTravelTimeAmount = duration - plannedTravelTime;
            long timeLeftToAlarm = upperBoundTime - System.currentTimeMillis();
            // Is this delay going to force us to wake up?
            if (increasedTravelTimeAmount > timeLeftToAlarm) {
                // Wake up the user!
                // TODO: Update the pending intent of the alarm to trigger NOW!
            } else {
                // There's still a delay, but it's not as urgent. Let's keep polling.
                // TODO: Make a pending intent for this receiver to check back again SOON!
            }
        } else {
            // No delay. Just check back again in some default time.
            // TODO: Make a pending intent for this receiver to check back again later.
        }

        // TODO: Call the pending intent, which ever one was created/updated, here.
    }
}
