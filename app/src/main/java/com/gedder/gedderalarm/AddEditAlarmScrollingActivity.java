package com.gedder.gedderalarm;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.text.Html;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.gedder.gedderalarm.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.gedder.gedderalarm.model.AlarmClock;

import org.w3c.dom.Text;

import java.util.Calendar;

/** The activity where the user edits an alarm, new or old. */

public class AddEditAlarmScrollingActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = AddEditAlarmScrollingActivity.class.getSimpleName();

    private String mOriginAddressString;
    private String mOriginIdString;
    private String mDestinationAddressString;
    private String mDestinationIdString;
    private String mArrivalTimeString;
    private String mPrepTimeString;
    private int mHourArrival;
    private int mMinuteArrival;
    private int mHour;
    private int mMinute;

    //Variables for time-picker and textviews:
    TimePicker mAlarmTimePicker;
    TextView mArivalTimeEditText;
    EditText mPrepTimeEditText;

    //Variables for auto-complete text boxes
    private static final String LOG_TAG = "AddEditAlarmScrollingActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextViewOrigin;
    private AutoCompleteTextView mAutocompleteTextViewDestination;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds NEW_YORK_CITY = new LatLngBounds(
            new LatLng(40.477399, -74.259090), new LatLng(40.917577, -73.700272));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit_scrolling);
        // Get the alarm clock in question.
        AlarmClock alarmClock = getIntent().getParcelableExtra(MainActivity.PARCEL_ALARM_CLOCK);
        // Programmatically change settings of views to match this alarm clock's settings.

        //Initialize variables for textviews and timepicker
        mAlarmTimePicker = (TimePicker) findViewById(R.id
                .generalAlarmTimePicker);
        mArivalTimeEditText = (TextView) findViewById(R.id
                .editAlarm_ArrivalTimePickerMonologBox);
        mPrepTimeEditText = (EditText) findViewById(R.id
                .editAlarm_PrepTimeTextBox);

        //Initialize auto-complete textviews
        mGoogleApiClient = new GoogleApiClient.Builder(AddEditAlarmScrollingActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextViewOrigin = (AutoCompleteTextView) findViewById(R.id
                .editAlarm_OriginAutoComplete);
        mAutocompleteTextViewDestination = (AutoCompleteTextView) findViewById(R.id
                .editAlarm_DestinationAutoComplete);
        mAutocompleteTextViewOrigin.setThreshold(3);
        mAutocompleteTextViewDestination.setThreshold(3);
        mAutocompleteTextViewOrigin.setOnItemClickListener(mAutocompleteClickListenerOrigin);
        mAutocompleteTextViewDestination.setOnItemClickListener(mAutocompleteClickListenerDestination);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                NEW_YORK_CITY, null);
        mAutocompleteTextViewOrigin.setAdapter(mPlaceArrayAdapter);
        mAutocompleteTextViewDestination.setAdapter(mPlaceArrayAdapter);
    }

    //This is called when one of the drop-down results is selected on origin tab
    private AdapterView.OnItemClickListener mAutocompleteClickListenerOrigin
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackOrigin);
        }
    };

    //This is the callback for origin tab
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackOrigin
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);

            //need to check API of device here, will do later
            mOriginAddressString = Html.fromHtml(place.getAddress() + "") + "";
            mOriginIdString = Html.fromHtml(place.getId() + "") + "";

            //THIS IS FOR TESTING TO MAKE SURE PARCING CORRECTLY
            Toast.makeText(getBaseContext(),
                    "Origin Address = " + mOriginAddressString + "\nID = " + mOriginIdString,
                    Toast.LENGTH_LONG).show();
        }
    };

    //This is called when one of the drop-down results is selected on destination tab
    private AdapterView.OnItemClickListener mAutocompleteClickListenerDestination
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackDestination);
        }
    };

    //This is the callback for destination tab
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackDestination
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);

            //need to check API of device here, will do later
            mDestinationAddressString = Html.fromHtml(place.getAddress() + "") + "";
            mDestinationIdString = Html.fromHtml(place.getId() + "") + "";

            //THIS IS FOR TESTING TO MAKE SURE PARCING CORRECTLY
            Toast.makeText(getBaseContext(),
                    "Destination Address = " + mDestinationAddressString + "\nID = " + mDestinationIdString,
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    /**
     *
     * @param view
     */
    public void cancel(View view) {
        finish();
    }

    /**
     *
     * @param view
     */
    public void done(View view) {
        mPrepTimeString = mPrepTimeEditText.getText() + "";
        Toast.makeText(this, "Prep time: " + mPrepTimeString, Toast.LENGTH_SHORT).show();
        mArrivalTimeString = mArivalTimeEditText.getText() + "";
        Toast.makeText(this, "Arrival time: " + mArrivalTimeString, Toast.LENGTH_SHORT).show();
        if (! mArrivalTimeString.equals("")) {
            String temp_for_toast = "Arrival Hour: " + Integer.toString(mHourArrival) + " | Arrival Minute: " + Integer.toString(mMinuteArrival);
            Toast.makeText(this, temp_for_toast, Toast.LENGTH_SHORT).show();
        }
        //need to check API of device here, will do later
        mHour = mAlarmTimePicker.getCurrentHour();
        mMinute = mAlarmTimePicker.getCurrentMinute();
        String temp_for_toast = "Alarm Hour: " + Integer.toString(mHour) + " | Alarm Minute: " + Integer.toString(mMinute);
        Toast.makeText(this, temp_for_toast, Toast.LENGTH_SHORT).show();
        if (mOriginAddressString != null) {
            Toast.makeText(this, "Origin: " + mOriginAddressString, Toast.LENGTH_SHORT).show();
        }
        if (mDestinationAddressString != null) {
            Toast.makeText(this, "Destination: " + mDestinationAddressString, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void setArrivalTime(View view){
        // Get Current Time
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                AlertDialog.THEME_HOLO_DARK,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        mHourArrival = hourOfDay;
                        mMinuteArrival = minute;
                        String am_or_pm;
                        if (hourOfDay > 12) {
                            am_or_pm = "pm";
                            hourOfDay = hourOfDay - 12;
                        } else {
                            am_or_pm = "am";
                        }
                        String hour_string = Integer.toString(hourOfDay);
                        if(hourOfDay < 10) {
                            hour_string = "0" + hour_string;
                        }
                        String minute_string = Integer.toString(minute);
                        if(minute < 10) {
                            minute_string = "0" + minute_string;
                        }
                        mArivalTimeEditText.setText(hour_string + ":" + minute_string + " " + am_or_pm);
                    }
                }, hour, min, false);
        timePickerDialog.show();
    }
}
