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
import com.gedder.gedderalarm.util.TimeUtilities;

import java.util.ArrayList;


/**
 * <p>Serves as the entry point for the Gedder algorithm.</p>
 *
 * <p>After gathering data from the intent that was broadcast, it starts up the
 * {@link GedderEngine}. The {@link GedderEngine} sends back some of the relevant data after flowing
 * through its pipeline.</p>
 *
 * <p>It then analyzes the response from the {@link GedderEngine}.</p>
 *
 * <p>During and after the analysis of the result data, it determines what action to take. It may do
 * one of the following:</p>
 *
 * <ul>
 *     <li>Reschedule itself to start up the {@link GedderEngine} for some default set time in the
 *     future.</li>
 *     <li>Reschedule itself to start up the {@link GedderEngine} for some adjusted time in the
 *     future.</li>
 *     <li>Trigger the alarm.</li>
 * </ul><br>
 *
 * <u><strong>Case 1</strong></u>
 *
 * <p>In the first case, the algorithm sees no reason for concern, and just carries on rescheduling
 * itself for future analysis at some default rate.</p>
 *
 * <u><strong>Case 2</strong></u>
 *
 * <p>In the second case, the algorithm picks up a possible delay, but it is not urgent enough that
 * the user needs to wake up right now. It'll reschedule itself for future analysis, but at a
 * tighter bound (how much tighter depends on the severity of the delay relative to the "wish"
 * alarm time).</p>
 *
 * <u><strong>Case 3</strong></u>
 *
 * <p>In the last case, the algorithm makes the decision to wake the user up right away: it caught a
 * delay significant enough that if the user doesn't take action now, they'll be late to their
 * destination.</p>
 *
 * <p>Each time the algorithm sets itself up for a future flow through the pipeline, it checks how
 * close it is to the currently planned alarm time. The closer it gets, the more often it queries
 * for updates on traffic and possible delays. This carries on until the algorithm notices that
 * now the user must wake up as soon as possible, otherwise the delay can be detrimental to reaching
 * the destination at the planned arrival time.</p>
 *
 * <p>To sum, the algorithm can either reschedule itself for a default rate of analysis, or a
 * tighter rate, or trigger the alarm, all depending on information it has previously received and
 * analyzed.</p>
 *
 * <p>In this way, the algorithm ensures it's awake and aware of any delays at all times, and
 * becomes increasingly active when there's a concern, i.e. when delays are present.</p>
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

        /*
        // We will need these calculations in the analysis.
        long   plannedLeaveTime   = upperBoundTime + prepTime;
        long   plannedTravelTime  = arrivalTime - plannedLeaveTime;

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
        */

        // PROBLEM: Say we plan to get up at 6am and it's currently 5am.
        //          Gedder detects a delay big and dangerous enough that we have to get up NOW.
        //          But in reality, this delay may end by 6am! So it's like Gedder woke up the user
        //          for nothing. But this is what this algorithm does by the `if` check below.
        //
        // SOLUTION: ???
        long durationMillis = TimeUtilities.minToMillis(duration);
        if (arrivalTime - durationMillis - prepTime <= System.currentTimeMillis()) {
            // There's a delay currently that's big enough that the user is forced to wake up now.
            // Consider the problem above, though, which in reality may happen.
        } else {
            // No delay urgent enough, so just reschedule the alarm for a time that depends on how
            // close the upper bound is. The closer the upper bound is, the less time we wait until
            // we go through this algorithm again.
        }

        // TODO: Call the pending intent, which ever one was created/updated, here.
    }
}
