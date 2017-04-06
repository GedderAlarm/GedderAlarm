package com.gedder.gedderalarm;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gedder.gedderalarm.db.AlarmClockDBHelper;
import com.gedder.gedderalarm.model.AlarmClock;
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
    private AlarmClock mAlarmClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit_scrolling);
        InitializeVariables();
    }

    private void InitializeVariables(){
        // Get the alarm clock in question.
        mAlarmClock = (AlarmClock) getIntent().getParcelableExtra(com.gedder.gedderalarm.MainActivity.PARCEL_ALARM_CLOCK);

        //Initialize variables for textviews, edittexts and timepicker
        mAlarmTimePicker = (TimePicker) findViewById(R.id
                .generalAlarmTimePicker);
        Calendar temp_cal = mAlarmClock.getAlarmTime();
        mAlarmTimePicker.setCurrentHour(temp_cal.get(Calendar.HOUR_OF_DAY));
        mAlarmTimePicker.setCurrentMinute(temp_cal.get(Calendar.MINUTE));
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
//        mOriginAddressString = mAlarmClock.getOriginAddress();
//        if (! mOriginAddressString.equals("")) {
//            mAutocompleteTextViewOrigin.setText(mOriginAddressString, false);
//            mOriginIdString = mAlarmClock.getOrigin();
//        }
//        mDestinationAddressString = mAlarmClock.getDestinationAddress();
//        if (! mDestinationAddressString.equals("")) {
//            mAutocompleteTextViewDestination.setText(mDestinationAddressString, false);
//            mDestinationIdString = mAlarmClock.getDestination();
//        }
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
        boolean arrival_time_set = false;
        boolean origin_set = false;
        boolean destination_set = false;
        boolean prep_time_set = false;
        mPrepTimeString = mPrepTimeEditText.getText() + "";
        mArrivalTimeString = mArivalTimeEditText.getText() + "";
        if (! mPrepTimeString.equals("")) {
            prep_time_set = true;
        }
        if (! mArrivalTimeString.equals("")) {
            arrival_time_set = true;
        }
        if (! mOriginAddressString.equals("")) {
            origin_set = true;
        }
        if (! mDestinationAddressString.equals("")) {
            destination_set = true;
        }
        mHour = mAlarmTimePicker.getCurrentHour();
        mMinute = mAlarmTimePicker.getCurrentMinute();

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int alarmDay = c.get(Calendar.DAY_OF_WEEK);

        if (arrival_time_set && origin_set && destination_set && prep_time_set) {
            int arrivalDay = alarmDay;
            if (hour < mHour || hour < mHourArrival || (hour == mHour && min <= mMinute)
                    || (hour == mHourArrival && min <= mMinuteArrival)) {
                alarmDay = (alarmDay % 7) + 1;
                arrivalDay = alarmDay;
            }
            if (mHour > mHourArrival || (mHour == mHourArrival && mMinuteArrival <= mMinute)) {
                arrivalDay = (alarmDay % 7) + 1;
            }
            mAlarmClock.setAlarmTime(alarmDay, mHour, mMinute);
            mAlarmClock.setArrivalTime(arrivalDay, mHourArrival, mMinuteArrival);
            mAlarmClock.setUpperBoundTime(alarmDay, mHour, mMinute);
            int prepTimeMinutes =Integer.parseInt(mPrepTimeEditText.getText() + "");
            int prepTimeHours = prepTimeMinutes / 60;
            prepTimeMinutes = prepTimeMinutes % 60;
            mAlarmClock.setPrepTime(prepTimeHours, prepTimeMinutes);
            mAlarmClock.setOrigin(mOriginIdString);
            mAlarmClock.setOriginAddress(mOriginAddressString);
            mAlarmClock.setDestination(mDestinationIdString);
            mAlarmClock.setOriginAddress(mOriginAddressString);
            if (! mAlarmClock.isAlarmOn()) {
                mAlarmClock.toggleAlarm();
            }
            if (! mAlarmClock.isGedderOn()) {
                mAlarmClock.toggleGedder();
            }
            Toast.makeText(this, "Gedder Alarm Set!", Toast.LENGTH_SHORT).show();
        } else {
            if (hour < mHour || (hour == mHour && min <= mMinute)) {
                alarmDay = (alarmDay % 7) + 1;
            }
            mAlarmClock.setAlarmTime(alarmDay, mHour, mMinute);
            mAlarmClock.setUpperBoundTime(alarmDay, mHour, mMinute);
            if (! mAlarmClock.isAlarmOn()) {
                mAlarmClock.toggleAlarm();
            }
            //Turn off Gedder if it was on
            if (mAlarmClock.isGedderOn()) {
                mAlarmClock.toggleGedder();
            }
            Toast.makeText(this, "Regular Alarm Set!", Toast.LENGTH_SHORT).show();
        }

        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        if (db.updateAlarmClock(mAlarmClock) != 1) {
            db.addAlarmClock(mAlarmClock);
        }
        db.close();

        Intent data = new Intent();
        //set the data to pass back
        data.putExtra(com.gedder.gedderalarm.MainActivity.PARCEL_ALARM_CLOCK, mAlarmClock);
        setResult(RESULT_OK, data);
        finish();
    }

    public void setArrivalTime(View view){
        // Get Current Time
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                //AlertDialog.THEME_HOLO_DARK,
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
