/*
 * USER: mslm, Mike
 * DATE: March 8th, 2017
 */

package com.gedder.gedderalarm.util;

import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.gedder.gedderalarm.util.JsonStatus;


/**
 * Class to parse the JSON received from Google Maps API.
 *
 * Example usage:
 *
 * public String json = "put json string here";
 * public JsonParser jsonParser = new JsonParser(json);
 * private int duration = jsonParser.duration();
 * private String origin = jsonParser.origin();
 * private String destination = jsonParser.destination();
 * private String route2_origin = jsonParser.origin(2);
 * private String route2_destination = jsonParser.destination(2);
 */
public class JsonParser {
    // For mslm:
    // TODO: Add origin() and destination() functionality.
    // TODO: Add originLatitude() and destinationLatitude() functionality.
    // TODO: Add originLongitude() and destinationLongitude() functionality.
    // TODO: Add choose-your-step functionality for everything that is in the `steps` key.
    // TODO: Add travelMode() functionality for individual steps.

    private String json;
    private JSONObject obj;

    /**
     *
     * @param json The JSON string to parse.
     */
    public JsonParser(String json) {
        this.json = json;

        try {
            this.obj = new JSONObject(this.json);
        } catch (JSONException e) {
            // TODO: Implement
        }
    }

    /**
     * Grabs json['routes'][0]['legs'][0]['duration']['value'].
     * @return duration of travel in seconds.
     */
    public int duration() {
        return duration(1, 1);
    }

    /**
     * Grabs json['routes'][route-1]['legs'][0]['duration']['value'].
     * param[0]: which route, if multiple. Starts from 1.
     * return: duration of travel in seconds.
     */
    public int duration(int route) {
        return duration(route, 1);
    }

    /**
     * Grabs json['routes'][route-1]['legs'][leg-1]['duration']['value'].
     * @param route which route, if multiple. Starts from 1.
     * @param leg which leg, if multiple. Starts from 1.
     * @return duration of travel in seconds.
     */
    public int duration(int route, int leg) {
        // TODO: Test.

        // User expected to enter values starting from 1.
        // We expect to use it starting from 0.
        route -= 1;
        leg -= 1;

        JSONArray routes;
        JSONObject route_number;
        JSONArray legs;
        JSONObject leg_number;
        JSONObject durationObj;
        int duration = -1;

        try {
            routes = obj.getJSONArray("routes");
            route_number = routes.getJSONObject(route);
            legs = route_number.getJSONArray("legs");
            leg_number = legs.getJSONObject(leg);
            durationObj = leg_number.getJSONObject("duration");
            duration = durationObj.getInt("value");
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return duration;
    }

    /**
     * Grabs json['routes'][0]['legs'][0]['distance']['value'].
     * @return distance of travel in meters.
     */
    public int distance() {
        return distance(1, 1);
    }

    /**
     * Grabs json['routes'][route-1]['legs'][0]['distance']['value'].
     * @param route which route, if multiple. Starts from 1.
     * @return  distance of travel in meters.
     */
    public int distance(int route) {
        return distance(route, 1);
    }

    /**
     * Grabs json['routes'][route-1]['legs'][leg-1]['distance']['value'].
     * @param route which route, if multiple. Starts from 1.
     * @param leg which leg, if multiple. Starts from 1.
     * @return distance of travel in meters.
     */
    public int distance(int route, int leg) {
        // TODO: Test.

        // User expected to enter values starting from 1.
        // We expect to use it starting from 0.
        route -= 1;
        leg -= 1;

        JSONArray routes;
        JSONObject route_number;
        JSONArray legs;
        JSONObject leg_number;
        int distance = -1;

        try {
            routes = obj.getJSONArray("routes");
            route_number = routes.getJSONObject(route);
            legs = route_number.getJSONArray("legs");
            leg_number = legs.getJSONObject(leg);
            distance = leg_number.getInt("value");
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return distance;
    }

    /**
     * Grabs json['routes'][0]['legs'][0]['duration_in_traffic']['value'].
     * Only exists if the request specified a traffic model.
     * @return duration in traffic in seconds.
     */
    public int durationInTraffic() {
        return durationInTraffic(1, 1);
    }

    /**
     * Grabs json['routes'][route-1]['legs'][0]['duration_in_traffic']['value'].
     * Only exists if the request specified a traffic model.
     * @param route which route, if multiple. Starts from 1.
     * @return duration in traffic in seconds.
     */
    public int durationInTraffic(int route) {
        return durationInTraffic(route, 1);
    }

    /**
     * Grabs json['routes'][route-1]['legs'][leg-1]['duration_in_traffic']['value'].
     * Only exists if the request specified a traffic model.
     * @param route which route, if multiple. Starts from 1.
     * @param leg which leg, if multiple. Starts from 1.
     * @return duration in traffic in seconds.
     */
    public int durationInTraffic(int route, int leg) {
        // TODO: Test.

        // User expected to enter values starting from 1.
        // We expect to use it starting from 0.
        route -= 1;
        leg -= 1;

        JSONArray routes;
        JSONObject route_number;
        JSONArray legs;
        JSONObject leg_number;
        JSONObject duration_in_traffic;
        int value = -1;

        try {
            routes = obj.getJSONArray("routes");
            route_number = routes.getJSONObject(route);
            legs = route_number.getJSONArray("legs");
            leg_number = legs.getJSONObject(leg);
            duration_in_traffic = leg_number.getJSONObject("duration_in_traffic");
            value = duration_in_traffic.getInt("value");
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return value;
    }

    /**
     * Grabs json['routes'][0]['warnings'] elements and puts them
     * in a ArrayList of Strings.
     * @return an ArrayList<String> object containing all warnings.
     */
    public ArrayList<String> warnings() {
        return warnings(1);
    }

    /**
     * Grabs json['routes'][route-1]['warnings'] elements and puts them
     * in a ArrayList of Strings.
     * @param route which route, if multiple. Starts from 1.
     * @return an ArrayList<String> object containing all warnings.
     */
    public ArrayList<String> warnings(int route) {
        // TODO: Test.

        // User expected to enter values starting from 1.
        // We expect to use it starting from 0.
        route -= 1;

        JSONArray routes;
        JSONObject route_number;
        JSONArray warnings;
        ArrayList<String> warningsList = new ArrayList<>();

        try {
            routes = obj.getJSONArray("routes");
            route_number = routes.getJSONObject(route);
            warnings = route_number.getJSONArray("warnings");
            for (int i = 0; i < warnings.length(); i++)
                warningsList.add(warnings.getString(i));
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return warningsList;
    }

    /**
     * Grabs json['routes'][0]['copyrights'].
     * @return a String containing the copyright information.
     */
    public String copyrights() {
        return copyrights(1);
    }

    /**
     * Grabs json['routes'][route-1]['copyrights'].
     * @param route which route, if multiple. Starts from 1.
     * @return a String containing the copyright information.
     */
    public String copyrights(int route) {
        // TODO: Implement.

        // User expected to enter values starting from 1.
        // We expect to use it starting from 0.
        route -= 1;

        JSONArray routes;
        JSONObject route_number;
        String copyrights = "";

        try {
            routes = obj.getJSONArray("routes");
            route_number = routes.getJSONObject(route);
            copyrights = route_number.getString("copyrights");
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return copyrights;
    }

    /**
     * Grabs json['routes'][0]['summary'].
     * @return a String containing summary information for the route.
     */
    public String summary() {
        return summary(1);
    }

    /**
     * Grabs json['routes'][route-1]['summary'].
     * @param route which route, if multiple. Starts from 1.
     * @return a String containing summary information for the route.
     */
    public String summary(int route) {
        // TODO: Test.

        // User expected to enter values starting from 1.
        // We expect to use it starting from 0.
        route -= 1;

        JSONArray routes;
        JSONObject route_number;
        String summary = "";

        try {
            routes = obj.getJSONArray("routes");
            route_number = routes.getJSONObject(route);
            summary = route_number.getString("summary");
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return summary;
    }

    /**
     * Grabs json['routes'][0]['fare']['currency'].
     * @return the ISO 4217 currency code that the fare is expressed in.
     */
    public String fareCurrency() {
        return fareCurrency(1);
    }

    /**
     * Grabs json['routes'][route-1]['fare']['currency'].
     * @param route which route, if multiple. Starts from 1.
     * @return the ISO 4217 currency code that the fare is expressed in.
     */
    public String fareCurrency(int route) {
        // TODO: Test.

        // User expected to enter values starting from 1.
        // We expect to use it starting from 0.
        route -= 1;

        JSONArray routes;
        JSONObject route_number;
        JSONObject fare;
        String currency = "";

        try {
            routes = obj.getJSONArray("routes");
            route_number = routes.getJSONObject(route);
            fare = route_number.getJSONObject("fare");
            currency = fare.getString("currency");
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return currency;
    }

    /**
     * Grabs json['routes'][0]['fare']['value'].
     * @return the total fare amount in the currency specified by fareCurrency().
     */
    public int fare() {
        return fare(1);
    }

    /**
     * Grabs json['routes'][route-1]['fare']['value'].
     * @param route which route, if multiple. Starts from 1.
     * @return the total fare amount in the currency specified by fareCurrency().
     */
    public int fare(int route) {
        // TODO: Test.

        // User expected to enter values starting from 1.
        // We expect to use it starting from 0.
        route -= 1;

        JSONArray routes;
        JSONObject route_number;
        JSONObject fareObj;
        int fare = -1;

        try {
            routes = obj.getJSONArray("routes");
            route_number = routes.getJSONObject(route);
            fareObj = route_number.getJSONObject("fare");
            fare = fareObj.getInt("value");
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return fare;
    }

    /**
     * Grabs json['status'].
     * @return status number of the request, corresponding to
     *         com.gedder.gedderalarm.util.JSONStatus enumerations.
     */
    public JsonStatus status() {
        // TODO: Test.

        JsonStatus code;
        String status = "";

        try {
            status = obj.getString("status");
        } catch (JSONException e) {
            // TODO: Implement.
        }

        switch (status) {
            case "OK":
                code = JsonStatus.OK;
                break;
            case "NOT_FOUND":
                code = JsonStatus.NOT_FOUND;
                break;
            case "ZERO_RESULTS":
                code = JsonStatus.ZERO_RESULTS;
                break;
            case "MAX_WAYPOINTS_EXCEEDED":
                code = JsonStatus.MAX_WAYPOINTS_EXCEEDED;
                break;
            case "MAX_ROUTE_LENGTH_EXCEEDED":
                code = JsonStatus.MAX_ROUTE_LENGTH_EXCEEDED;
                break;
            case "INVALID_REQUEST":
                code = JsonStatus.INVALID_REQUEST;
                break;
            case "OVER_QUERY_LIMIT":
                code = JsonStatus.OVER_QUERY_LIMIT;
                break;
            case "REQUEST_DENIED":
                code = JsonStatus.REQUEST_DENIED;
                break;
            default:
                code = JsonStatus.UNKNOWN_ERROR;
                break;
        }

        return code;
    }

    /**
     * Grabs json['error_message'].
     * Only exists if json['status'] != "OK".
     * @return error message string.
     */
    public String errorMessage() {
        String error = "";

        try {
            error = obj.getString("error_message");
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return error;
    }

    /**
     * Grabs json['available_travel_modes'].
     * Only exists if a request specifies a travel mode and gets
     * no results.
     * @return an ArrayList<String> object containing all available
     *         travel modes.
     */
    public ArrayList<String> availableTravelModes() {
        // TODO: Test.

        JSONArray travel_modes;
        ArrayList<String> modes = new ArrayList<>();

        try {
            travel_modes = obj.getJSONArray("available_travel_modes");
            for (int i = 0; i < travel_modes.length(); i++)
                modes.add(travel_modes.getString(i));
        } catch (JSONException e) {
            // TODO: Implement.
        }

        return modes;
    }
}