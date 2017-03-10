package com.gedder.gedderalarm.util;

/*
 * USER: Mike, mslm
 * DATE: 3/4/2017
 * Class to generate URLs for Google Maps API.
 */


/**
 * Example usage:
 *
 * public Url url = new Url.UrlBuilder("origin", "destination", "apiKey") // required
 *                      .arrivalTime("arrivalTime")     // optional
 *                      .departureTime("departureTime") // optional
 *                      .travelMode("travelMode")       // optional
 *                      .avoidToll()                    // optional
 *                      .avoidHighways()                // optional
 *                      .build(); // must call this to get back a Url
 */
public class Url {
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

    private Url(UrlBuilder builder) {
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public boolean avoidToll() {
        return avoidToll;
    }

    public boolean avoidHighways() {
        return avoidHighways;
    }

    // Needs to abide by Google's naming rules.
    // TODO: Figure out a way to abide by Google's naming rules
    //       through possibly using a type other than String.
    private void addOrigin(String origin) {
        if (origin != null)
            this.url += "origin=" + origin;
        else
            ; // TODO: Throw exception; this variable is required.
    }

    // Needs to abide by Google's naming rules.
    // TODO: Figure out a way to abide by Google's naming rules
    //       through possibly using a type other than String.
    private void addDestination(String destination) {
        if (destination != null)
            this.url += "destination=" + destination;
        else
            ; // TODO: Throw exception; this variable is required.
    }

    private void addApiKey(String apiKey) {
        if (apiKey != null)
            this.url += "key=" + apiKey;
        else
            ; // TODO: Throw exception; this variable is required.
    }

    // TODO: Need to add unix time
    private void addArrivalTime(String arrivalTime) {
        if (arrivalTime != null)
            this.url += "arrival_time=" + arrivalTime;
    }

    // TODO: Need to add unix time
    private void addDepartureTime(String departureTime) {
        if (departureTime != null)
            this.url += "departure_time=" + departureTime;
    }

    private void addTravelMode(String travelMode) {
        if (travelMode != null)
            this.url += "mode=" + travelMode;
    }

    private void addAvoidToll(boolean avoidToll) {
        if (avoidToll)
            this.url += "avoid=tolls";
    }

    private void addAvoidHighways(boolean avoidHighways) {
        if (avoidHighways)
            this.url += "avoid=highways";
    }

    public static class UrlBuilder {
        private final String origin;        // required
        private final String destination;   // required
        private final String apiKey;        // required
        private String arrivalTime;         // optional
        private String departureTime;       // optional
        private String travelMode;          // optional
        private boolean avoidToll;          // optional
        private boolean avoidHighways;      // optional

        // Needs to abide by Google's naming rules.
        // TODO: Figure out a way to abide by Google's naming rules
        //       through possibly using a type other than String.
        public UrlBuilder(String origin, String destination, String apiKey) {
            this.origin = origin;
            this.destination = destination;
            this.apiKey = apiKey;
        }

        // TODO: Need to add unix time
        public UrlBuilder arrivalTime(String arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        // TODO: Need to add unix time
        public UrlBuilder departureTime(String departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        public UrlBuilder travelMode(String travelMode) {
            this.travelMode = travelMode;
            return this;
        }

        public UrlBuilder avoidToll() {
            this.avoidToll = true;
            return this;
        }

        public UrlBuilder avoidHighways() {
            this.avoidHighways = true;
            return this;
        }

        public Url build() {
            return new Url(this);
        }
    }
}