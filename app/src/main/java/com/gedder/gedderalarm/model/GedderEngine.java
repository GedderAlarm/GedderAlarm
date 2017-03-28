/*
 * USER: mslm
 * DATE: 3/27/17
 */

package com.gedder.gedderalarm.model;


/**
 * The engine goes through the following pipeline:
 *
 * <ol>
 *     <li>Get as input (Origin, Destination, Arrival Time, Prep Time, Upper Bound Time).</li>
 *     <li>Generate a URL to query Google Maps API with the above parameters in some fashion.</li>
 *     <li>Query Google Maps API using the URL.</li>
 *     <li>Store the response JSON for analysis.</li>
 *     <li>Analyze the response JSON and take action.</li>
 * </ol>
 *
 * After analyzing the response JSON, it may do nothing. That is only if there is no delay and no
 * significant change in the duration of the total trip. In short, it will do nothing if the user
 * won't be late given the current alarm settings.
 *
 * If the engine's analysis implies lateness under the current settings, it adjusts the alarm for
 * which it's working under the hood. This may mean that the alarm will trigger immediately, or
 * that the alarm is set for a time earlier than originally planned. Note that as this feature
 * is unpredictable (since delays are unpredictable), there may be several occasions where the
 * engine self-adjusts backwards.
 *
 * The engine may adjust the alarm forward, but never past the <b>upper bound</b>, as per the user's
 * wish.
 *
 * No matter what action it takes, the engine will set itself up to flow through the pipeline again
 * for some time in the future, if that time is not past the planned alarm time. It'll then re-
 * analyze the response from Google Maps API, determining if there is <em>then</em> any delay,
 * and again take appropriate action.
 *
 * Each time the engine sets itself up for a future flow through the pipeline, it checks how close
 * it is to the currently planned alarm time. The closer it gets, the more often it queries for
 * updates on traffic and possible delays.
 *
 * In this way, the engine ensures it's awake and aware of any delays at all times, and it becomes
 * increasingly active near the planned alarm time, when delays are the most fateful to lateness.
 */
public final class GedderEngine {
    private static final String TAG = GedderEngine.class.getSimpleName();

    private GedderEngine() {}
}
