/*
 * USER: mslm
 * DATE: 3/31/2017
 */

package com.gedder.gedderalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
    public static final String ANALYSIS_CONSECUTIVE_DELAY_COUNT =
            "__ANALYSIS_CONSECUTIVE_DELAY_COUNT__";

    @Override
    public void onReceive(Context context, Intent intent) {
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

        // Turn on the engine, and get back the results.
        Bundle results = GedderEngine.start(origin, dest, arrivalTime, prepTime, upperBoundTime);
        int duration = results.getInt(GedderEngine.RESULT_DURATION, -1);
        int durationInTraffic = results.getInt(GedderEngine.RESULT_DURATION_IN_TRAFFIC, -1);
        ArrayList<String> warnings =
                (ArrayList<String>) results.getSerializable(GedderEngine.RESULT_WARNINGS);

        // Need this for a comprehensible analysis below.
        long oldDuration           = arrivalTime - prepTime - upperBoundTime;
        long delayAmount           = duration - oldDuration;
        long timeUntilAlarm        = upperBoundTime - System.currentTimeMillis();
        int  consecutiveDelayCount = intent.getIntExtra(ANALYSIS_CONSECUTIVE_DELAY_COUNT, 0);

        // Initialize & declare intents and variables for our next action.
        Intent nextAction = new Intent(
                GedderAlarmApplication.getAppContext(), GedderReceiver.class);
        long nextActionTime;

        // Is Google Maps API detecting a longer trip than before?
        if (duration > oldDuration) {
            // Then we have a delay. Is this delay going to force us to wake up?
            if (delayAmount > timeUntilAlarm) {
                // Wake up the user!
                nextAction.setClass(GedderAlarmApplication.getAppContext(), AlarmReceiver.class);
                nextActionTime = System.currentTimeMillis() + TimeUtilities.secondsToMillis(1);
            } else {
                // Not as urgent, but there's still a delay. Let's keep polling to stay updated.
                nextAction.putExtra(ANALYSIS_CONSECUTIVE_DELAY_COUNT, ++consecutiveDelayCount);
                nextActionTime = System.currentTimeMillis()
                        + (getFrequencyDependendingOn(timeUntilAlarm) / (consecutiveDelayCount));
            }
        } else {
            // No delay. Check back in a time dependent on how close we are to the alarm time.
            nextActionTime = System.currentTimeMillis()
                    + getFrequencyDependendingOn(timeUntilAlarm);
        }

        // Whatever the analysis, we must set the intent.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                GedderAlarmApplication.getAppContext(), id, nextAction, PendingIntent.FLAG_UPDATE_CURRENT);
        GedderAlarmManager.setOptimal(
                AlarmManager.RTC_WAKEUP, nextActionTime, pendingIntent);
    }

    /**
     * Returns a requery frequency depending upon some time.
     * <br>
     * Uses the following intervals:
     * <br>
     * <table>
     *     <thead>
     *         <tr>
     *             <th>Interval</th>
     *             <th>Return</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>Hour > 5</td>
     *             <td>1 hour 30 minutes</td>
     *         </tr>
     *         <tr>
     *             <td>1 < Hour &le; 5</td>
     *             <td>30 minutes</td>
     *         </tr>
     *         <tr>
     *             <td>30 < Minutes &le; 60</td>
     *             <td>10 minutes</td>
     *         </tr>
     *         <tr>
     *             <td>15 < Minutes &le; 30</td>
     *             <td>5 minutes</td>
     *         </tr>
     *         <tr>
     *             <td>10 < Minutes &le; 15</td>
     *             <td>2 minutes</td>
     *         </tr>
     *         <tr>
     *             <td>Minutes &le; 10</td>
     *             <td>1 minute</td>
     *         </tr>
     *     </tbody>
     * </table>
     * @param dependent The time to base the heuristics off of.
     * @return The frequency to check based off of the dependent.
     */
    private long getFrequencyDependendingOn(long dependent) {
        double hours = TimeUtilities.millisToHours(dependent);
        double minutes = TimeUtilities.millisToMinutes(dependent);
        if (hours > 5) {
            return TimeUtilities.getMillisIn(1, 30);    // 1 hour 30 minutes
        } else if (1 < hours && hours <= 5) {
            return TimeUtilities.minutesToMillis(30);   // 30 minutes
        } else if (30 < minutes && minutes <= 60) {
            return TimeUtilities.minutesToMillis(10);   // 10 minutes
        } else if (15 < minutes && minutes <= 30) {
            return TimeUtilities.minutesToMillis(5);    // 5 minutes
        } else if (10 < minutes && minutes <= 15) {
            return TimeUtilities.minutesToMillis(2);    // 2 minutes
        } else {
            return TimeUtilities.minutesToMillis(1);    // 1 minute
        }
    }
}
