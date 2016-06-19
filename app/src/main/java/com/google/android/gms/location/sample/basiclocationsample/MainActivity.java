/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.basiclocationsample;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;

import android.location.Geocoder;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;



public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String TAG = "MainActivity";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected String mResolvedAddressLabel;
    protected TextView mResolvedAddresstext;
    public Geocoder geocoder;

    String locality;
    String adminArea;
    String countryCode;
    String throughFare;
    Button track;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mResolvedAddressLabel=getResources().getString(R.string.Resolved_address_label);


                buildGoogleApiClient();


        track = (Button) findViewById(R.id.button2);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg1)
            {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    mLatitudeText = (TextView) findViewById((R.id.latitude_text));
                    mLongitudeText = (TextView) findViewById((R.id.longitude_text));
                    mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                            mLastLocation.getLatitude()));
                    mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                            mLastLocation.getLongitude()));
                    mResolvedAddresstext=(TextView)findViewById((R.id.Resolved_address_text));
                    addressResolver(mLastLocation);
                    String resolvedAddress= "You are at - " + throughFare + ", " + locality + ", " + adminArea + ", " + countryCode;
                    mResolvedAddresstext.setText(String.format("%s: %s", mResolvedAddressLabel,
                            resolvedAddress));
                    Toast.makeText(getApplicationContext(), "You are at - " + throughFare + ", " + locality + ", " + adminArea + ", " + countryCode + "\n" +
                            "Latitude: " +  mLastLocation.getLatitude() + "\nLongitude: " + mLastLocation.getLongitude(),  Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_location_detected, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
    public void addressResolver(Location location)
            //Resolves the location variable into a user understandable address format
    {

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        geocoder = new Geocoder(getApplicationContext());
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        try {
            if (isConnected)
            {
                Log.v(TAG, "Attempting to resolve address");
                List<Address> locationList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (locationList.get(0).getLocality() != null) {
                    locality = locationList.get(0).getLocality();
                    Log.v("[LOCALITY]", locality);
                }
                if (locationList.get(0).getAdminArea() != null) {
                    adminArea = locationList.get(0).getAdminArea();
                    Log.v("[ADMIN AREA]", adminArea);
                }
                if (locationList.get(0).getCountryName() != null) {
                    countryCode = locationList.get(0).getCountryName();
                    Log.v("[COUNTRY]", countryCode);
                }
                if (locationList.get(0).getThoroughfare() != null) {
                    throughFare = locationList.get(0).getThoroughfare();
                    Log.v("[THROUGH FARE]", throughFare);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
