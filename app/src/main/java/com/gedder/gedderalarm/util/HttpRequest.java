/*
 * USER: Mike
 * DATE: 3/18/2017
 */

package com.gedder.gedderalarm.util;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class to request data from Google Maps Directions API
 *  Example usage:
 *  In MainActivity.java
 *      // Use urlGenerator class to create/encode url.
 *      url.build();
 *      String mainTest = new HTTPRequest().execute(url).get();
 *      JsonParser test = new JsonParser(mainTest);
 */
public class HttpRequest extends AsyncTask<String, String, String> {
    private static final String TAG = HttpRequest.class.getSimpleName();

    private HttpURLConnection mUrlConnection = null;

    // TEST
    // Need API key. Get your own on the Google Cloud Platform.
    // https://maps.googleapis.com/maps/api/directions/json?origin=Yonkers,NY&destination=Bronx,NY&key=MY_KEY

    /**
     * Sends a request to the Google website
     * @param urlTest url to be sent out
     * @return a string builder of jsonResults
     */
    protected String doInBackground(String... urlTest) {

        this.setURL(urlTest[0]);

        StringBuilder jsonResults = new StringBuilder();
        try {
            URL url = new URL(urlTest[0]);
            mUrlConnection = (HttpURLConnection) url.openConnection();
            mUrlConnection.setRequestMethod("GET");
            mUrlConnection.setDoOutput(true);
            mUrlConnection.setDoInput(true);

            InputStream is = mUrlConnection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            // Load the results into a StringBuilder
            String line;
            while ((line = rd.readLine()) != null) {
                jsonResults.append(line);
                jsonResults.append("\r");
            }

            rd.close();

        } catch (MalformedURLException e) {
            // TODO: Handle exception.
        } catch (IOException e) {
            // TODO: Handle exception.
        } finally {
            if (mUrlConnection != null)
                mUrlConnection.disconnect();
        }
        return jsonResults.toString();
    }

    /**
     * Sets a String to URL, exception is thrown if not an url
     * @param url url to be sent out
     */
    private void setURL(String url) {
        try {
            URL setURL = new URL(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}