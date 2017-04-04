/*
 * USER: Mike, mslm
 * DATE: 3/4/2017
 */

package com.gedder.gedderalarm.google;

import com.gedder.gedderalarm.util.Log;
import com.gedder.gedderalarm.util.except.RequiredParamMissingException;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


/**
 * <p>Class to generate URLs for Google Maps API.</p>
 *
 * <p>Example usage:</p>
 *
 * <code><pre>
 * public UrlGenerator url = new UrlGenerator.UrlBuilder("origin", "destination", "apiKey") // required
 *                      .arrivalTime("arrivalTime")     // optional
 *                      .departureTime("departureTime") // optional
 *                      .travelMode("travelMode")       // optional
 *                      .avoidToll()                    // optional
 *                      .avoidHighways()                // optional
 *                      .build(); // must call this to get back a Url
 * </pre></code>
 */

public class UrlGenerator {
    private static final String TAG = UrlGenerator.class.getSimpleName();

    private final String origin;            // required
    private final String destination;       // required
    private final String apiKey;            // required
    private final String arrivalTime;       // optional
    private final String departureTime;     // optional
    private final String travelMode;        // optional
    private final boolean avoidToll;        // optional
    private final boolean avoidHighways;    // optional
    private final String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?";

    private String url = baseUrl;

    /**
     *
     * @param builder
     */
    private UrlGenerator(UrlBuilder builder) {
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.apiKey = builder.apiKey;
        this.arrivalTime = builder.arrivalTime;
        this.departureTime = builder.departureTime;
        this.travelMode = builder.travelMode;
        this.avoidToll = builder.avoidToll;
        this.avoidHighways = builder.avoidHighways;

        addOrigin(origin);
        addDestination(destination);
        addArrivalTime(arrivalTime);
        addDepartureTime(departureTime);
        addTravelMode(travelMode);
        addAvoidToll(avoidToll);
        addAvoidHighways(avoidHighways);
        addApiKey(apiKey);
    }

    /**
     *
     * @return
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     *
     * @return
     */
    public String getOrigin() {
        return origin;
    }

    /**
     *
     * @return
     */
    public String getDestination() {
        return destination;
    }

    /**
     *
     * @return
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     *
     * @return
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     *
     * @return
     */
    public String getDepartureTime() {
        return departureTime;
    }

    /**
     *
     * @return
     */
    public String getTravelMode() {
        return travelMode;
    }

    /**
     *
     * @return
     */
    public boolean avoidToll() {
        return avoidToll;
    }

    /**
     *
     * @return
     */
    public boolean avoidHighways() {
        return avoidHighways;
    }

    /**
     *
     * @param origin
     */
    private void addOrigin(String origin) {
        this.url += "origin=place_id:" + origin;
    }

    /**
     *
     * @param destination
     */
    private void addDestination(String destination) {
        this.url += "destination=place_id:" + destination;
    }

    /**
     *
     * @param apiKey
     */
    private void addApiKey(String apiKey) {
        this.url += "key=" + apiKey;
    }

    /**
     *
     * @param arrivalTime
     */
    private void addArrivalTime(String arrivalTime) {
        if (arrivalTime != null) {
            this.url += "arrival_time=" + arrivalTime;
        }
    }

    /**
     *
     * @param departureTime
     */
    private void addDepartureTime(String departureTime) {
        if (departureTime != null) {
            this.url += "departure_time=" + departureTime;
        }
    }

    /**
     *
     * @param travelMode
     */
    private void addTravelMode(String travelMode) {
        if (travelMode != null) {
            this.url += "mode=" + travelMode;
        }
    }

    /**
     *
     * @param avoidToll
     */
    private void addAvoidToll(boolean avoidToll) {
        if (avoidToll) {
            this.url += "avoid=tolls";
        }
    }

    /**
     *
     * @param avoidHighways
     */
    private void addAvoidHighways(boolean avoidHighways) {
        if (avoidHighways) {
            this.url += "avoid=highways";
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return url;
    }

    /**
     *
     * @return
     */
    public URL getUrl() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException");
        }
        return null;
    }

    /** Builds a URL and instantiates it with {@link #build()}. */
    public static class UrlBuilder {
        private static final String SUB_TAG = UrlBuilder.class.getSimpleName();

        private String origin;              // required
        private String destination;         // required
        private String apiKey;              // required
        private String arrivalTime;         // optional
        private String departureTime;       // optional
        private String travelMode;          // optional
        private boolean avoidToll;          // optional
        private boolean avoidHighways;      // optional

        /**
         * Initializes required parameters for the URL.
         * @param origin
         * @param destination
         * @param apiKey
         * @throws RequiredParamMissingException Google Maps API required that we have an origin,
         * destination, and API key in the HTTP request, at the least.
         */
        public UrlBuilder(String origin, String destination, String apiKey) {
            try {
                if (origin == null || destination == null
                        || origin.equals("") || destination.equals("")) {
                    throw new RequiredParamMissingException();
                }
                this.origin = URLEncoder.encode(origin, "UTF-8");
                this.destination = URLEncoder.encode(destination, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.wtf(SUB_TAG, "UTF-8 is apparently unsupported.");
            }

            this.apiKey = apiKey;
        }

        /**
         *
         * @param arrivalTime
         * @return
         */
        public UrlBuilder arrivalTime(String arrivalTime) {
            this.arrivalTime = toUnixTime(arrivalTime);
            return this;
        }

        /**
         *
         * @param arrivalTime
         * @return
         */
        public UrlBuilder arrivalTime(long arrivalTime) {
            this.arrivalTime = String.valueOf(arrivalTime);
            return this;
        }

        /**
         *
         * @param departureTime
         * @return
         */
        public UrlBuilder departureTime(String departureTime) {
            this.departureTime = toUnixTime(departureTime);
            return this;
        }

        /**
         *
         * @param departureTime
         * @return
         */
        public UrlBuilder departureTime(long departureTime) {
            this.departureTime = String.valueOf(departureTime);
            return this;
        }

        /**
         *
         * @param travelMode
         * @return
         */
        public UrlBuilder travelMode(String travelMode) {
            if (!isAvailableTravelMode(travelMode)) {
                throw new IllegalArgumentException(
                        SUB_TAG + "::UrlBuilder::travelMode: travel mode not available.");
            }

            this.travelMode = travelMode;
            return this;
        }

        /**
         *
         * @return
         */
        public UrlBuilder avoidToll() {
            this.avoidToll = true;
            return this;
        }

        /**
         *
         * @return
         */
        public UrlBuilder avoidHighways() {
            this.avoidHighways = true;
            return this;
        }

        /**
         *
         * @return A built UrlGenerator class.
         */
        public UrlGenerator build() {
            return new UrlGenerator(this);
        }

        /**
         * Turns time into unix time format, appropriate for Google Maps API.
         * @param time
         * @return
         */
        private String toUnixTime(String time) {
            // TODO: Implement.
            return time;
        }

        /**
         * Convenience function to check whether the mode string is valid.
         * @param mode The intended mode of travel.
         * @return whether the intended mode of travel is valid for Google Maps API.
         */
        private boolean isAvailableTravelMode(String mode) {
            return !mode.equals(TravelModes.bus.name())     &&
                   !mode.equals(TravelModes.subway.name())  &&
                   !mode.equals(TravelModes.train.name())   &&
                   !mode.equals(TravelModes.tram.name())    &&
                   !mode.equals(TravelModes.rail.name());
        }
    }
}