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
 * Serves as the entry point for the Gedder algorithm.
 *
 * After gathering data from the intent that was broadcast, it starts up the {@link GedderEngine}.
 * The {@link GedderEngine} sends back some of the relevant data after flowing through its pipeline.
 *
 * It then analyzes the response from the {@link GedderEngine}.
 *
 * During and after the analysis of the result data, it determines what action to take. It may do
 * one of the following:
 *
 * <ul>
 *     <li>Reschedule itself to start up the {@link GedderEngine} for some default set time in the
 *     future.</li>
 *     <li>Reschedule itself to start up the {@link GedderEngine} for some adjusted time in the
 *     future.</li>
 *     <li>Trigger the alarm.</li>
 * </ul>
 *
 * <u><strong>Case 1</strong></u><br>
 * In the first case, the algorithm sees no reason for concern, and just carries on rescheduling
 * itself for future analysis at some default rate.
 *
 * <u><strong>Case 2</strong></u><br>
 * In the second case, the algorithm picks up a possible delay, but it is not urgent enough that
 * the user needs to wake up right now. It'll reschedule itself for future analysis, but at a
 * tighter bound (how much tighter depends on the severity of the delay relative to the "wish"
 * alarm time).
 *
 * <u><strong>Case 3</strong></u><br>
 * In the last case, the algorithm makes the decision to wake the user up right away: it caught a
 * delay significant enough that if the user doesn't take action now, they'll be late to their
 * destination.
 *
 * Each time the algorithm sets itself up for a future flow through the pipeline, it checks how
 * close it is to the currently planned alarm time. The closer it gets, the more often it queries
 * for updates on traffic and possible delays. This carries on until the algorithm notices that
 * now the user must wake up as soon as possible, otherwise the delay can be detrimental to reaching
 * the destination at the planned arrival time.
 *
 * To sum, the algorithm can either reschedule itself for a default rate of analysis, or a tighter
 * rate, or trigger the alarm, all depending on information it has previously received and analyzed.
 *
 * In this way, the algorithm ensures it's awake and aware of any delays at all times, and becomes
 * increasingly active when there's a concern, i.e. when delays are present.
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
        ArrayList<String> warnings =
                (ArrayList<String>) results.getSerializable(GedderEngine.RESULT_WARNINGS);

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
