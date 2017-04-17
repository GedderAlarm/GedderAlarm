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
    private String mPrepTimeString;
    private int mHourArrival;
    private int mMinuteArrival;
    private int mHour;
    private int mMinute;
    private int mPrepTime;

    //Variables for time-picker and textviews:
    TimePicker mAlarmTimePicker;
    TextView mArrivalTimeEditText;
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

        /************************************************************************/
        //FOR TESTING
//        Calendar alarmTime = mAlarmClock.getAlarmTime();
//        Toast.makeText(getBaseContext(),
//                "Alarm Time: Hour: " + Integer.toString(alarmTime.get(Calendar.HOUR_OF_DAY))
//                        + " Minute: " + Integer.toString(alarmTime.get(Calendar.MINUTE)),
//                Toast.LENGTH_SHORT).show();
//        Calendar arrivalTime = mAlarmClock.getArrivalTime();
//        Toast.makeText(getBaseContext(),
//                "Arrival Time: Hour: " + Integer.toString(arrivalTime.get(Calendar.HOUR_OF_DAY))
//                        + " Minute: " + Integer.toString(arrivalTime.get(Calendar.MINUTE)),
//                Toast.LENGTH_SHORT).show();
//        Toast.makeText(getBaseContext(),
//                "Prep Time: " + Integer.toString((((int)mAlarmClock.getPrepTimeMillis()) / 1000) / 60),
//                Toast.LENGTH_SHORT).show();
//        Toast.makeText(getBaseContext(),
//                "Destination: " + mAlarmClock.getDestinationAddress(),
//                Toast.LENGTH_SHORT).show();
//        Toast.makeText(getBaseContext(),
//                "Destination ID: " + mAlarmClock.getDestinationId(),
//                Toast.LENGTH_SHORT).show();
//        Toast.makeText(getBaseContext(),
//                "Origin: " + mAlarmClock.getOriginAddress(),
//                Toast.LENGTH_SHORT).show();
//        Toast.makeText(getBaseContext(),
//                "Origin ID: " + mAlarmClock.getOriginId(),
//                Toast.LENGTH_SHORT).show();
        /************************************************************************/

        //Initialize variables for textviews, edittexts and timepicker
        mAlarmTimePicker = (TimePicker) findViewById(R.id
                .generalAlarmTimePicker);
        Calendar temp_cal = mAlarmClock.getAlarmTime();
        mHour = temp_cal.get(Calendar.HOUR_OF_DAY);
        mMinute = temp_cal.get(Calendar.MINUTE);
        mAlarmTimePicker.setCurrentHour(mHour);
        mAlarmTimePicker.setCurrentMinute(mMinute);
        mArrivalTimeEditText = (TextView) findViewById(R.id
                .editAlarm_ArrivalTimePickerMonologBox);
        temp_cal = mAlarmClock.getArrivalTime();
        mHourArrival = temp_cal.get(Calendar.HOUR_OF_DAY);
        mMinuteArrival = temp_cal.get(Calendar.MINUTE);
        int hourOfDay = mHourArrival;
        int minute = mMinuteArrival;
        String am_or_pm;
        if (hourOfDay >= 12) {
            am_or_pm = "pm";
            hourOfDay = hourOfDay - 12;
        } else {
            am_or_pm = "am";
        }
        if (hourOfDay == 0) {
            hourOfDay = 12;
        }
        String hour_string = Integer.toString(hourOfDay);
        if(hourOfDay < 10) {
            hour_string = "0" + hour_string;
        }
        String minute_string = Integer.toString(minute);
        if(minute < 10) {
            minute_string = "0" + minute_string;
        }
        mArrivalTimeEditText.setText("Arrival time: " + hour_string + ":" + minute_string + " " + am_or_pm);
        mPrepTimeEditText = (EditText) findViewById(R.id
                .editAlarm_PrepTimeTextBox);
        //We check to see if prep time is greater than 0, otherwise we leave the hint for user
        Long prepMilli = mAlarmClock.getPrepTimeMillis();
        mPrepTime = 0;
        if (prepMilli != 0) {
            int prepSeconds = (int) (prepMilli / 1000);
            mPrepTime = prepSeconds / 60;
            mPrepTimeEditText.setText(Integer.toString(mPrepTime));
        }

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
        //we check to see if there is a non-empty origin and address and
        //if there is we replace the text hint
        mOriginAddressString = mAlarmClock.getOriginAddress();
        mOriginIdString = mAlarmClock.getOriginId();
        if (! mOriginAddressString.equals("")) {
            mAutocompleteTextViewOrigin.setText(mOriginAddressString, false);
        }
        mDestinationAddressString = mAlarmClock.getDestinationAddress();
        mOriginIdString = mAlarmClock.getDestinationId();
        mDestinationIdString = mAlarmClock.getDestinationId();
        if (! mDestinationAddressString.equals("")) {
            mAutocompleteTextViewDestination.setText(mDestinationAddressString, false);
        }
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

    public void setArrivalTime(View view){
        // Get Current Time
        int hour = mHourArrival;
        int min = mMinuteArrival;

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
                        if (hourOfDay >= 12) {
                            am_or_pm = "pm";
                            hourOfDay = hourOfDay - 12;
                        } else {
                            am_or_pm = "am";
                        }
                        if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        String hour_string = Integer.toString(hourOfDay);
                        if(hourOfDay < 10) {
                            hour_string = "0" + hour_string;
                        }
                        String minute_string = Integer.toString(minute);
                        if(minute < 10) {
                            minute_string = "0" + minute_string;
                        }
                        mArrivalTimeEditText.setText("Arrival time: " + hour_string + ":" + minute_string + " " + am_or_pm);
                    }
                }, hour, min, false);
        timePickerDialog.show();
    }

    /**
     *
     * @param view
     */
    public void cancel(View view) {
        Toast.makeText(getBaseContext(),
                "Cancel pressed! No changes made to the alarm. ",
                Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     *
     * @param view
     */
    public void done(View view) {
        mPrepTimeString = mPrepTimeEditText.getText() + "";
        if (! mPrepTimeString.equals("")) {
            mPrepTime =  Integer.parseInt(mPrepTimeString);
        }
        int prepTimeMinutes = mPrepTime;
        int prepTimeHours = prepTimeMinutes / 60;
        prepTimeMinutes = prepTimeMinutes % 60;
        mHour = mAlarmTimePicker.getCurrentHour();
        mMinute = mAlarmTimePicker.getCurrentMinute();
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int alarmDay = c.get(Calendar.DAY_OF_WEEK);
        int arrivalDay = alarmDay;
        if (mHour < hour || (mHour == hour) && mMinute <= min) {
            alarmDay = (alarmDay % 7) + 1;
        }
        if (mHour > mHourArrival || (mHour == mHourArrival && mMinuteArrival <= mMinute)) {
            arrivalDay = (alarmDay % 7) + 1;
        }
        mAlarmClock.setAlarmTime(alarmDay, mHour, mMinute);
        mAlarmClock.setArrivalTime(arrivalDay, mHourArrival, mMinuteArrival);
        mAlarmClock.setPrepTime(prepTimeHours, prepTimeMinutes);
        mAlarmClock.setOriginId(mOriginIdString);
        mAlarmClock.setOriginAddress(mOriginAddressString);
        mAlarmClock.setDestinationId(mDestinationIdString);
        mAlarmClock.setDestinationAddress(mDestinationAddressString);

        // Send off the alarm clock.
        AlarmClockDBHelper db = new AlarmClockDBHelper(this);
        if (db.updateAlarmClock(mAlarmClock) != 1) {
            db.addAlarmClock(mAlarmClock);
        }
        db.close();

        Intent data = new Intent();
        // Set the data to pass back
        data.putExtra(com.gedder.gedderalarm.MainActivity.PARCEL_ALARM_CLOCK, mAlarmClock);
        setResult(RESULT_OK, data);
        finish();
    }
}
