package com.example.android.shushme;

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.app.LoaderManager;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static com.google.android.gms.location.places.Places.GEO_DATA_API;

// Implementing the GoogleApi Interfaces is needed to implement the callbacks in the MainActivity
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_REQUEST = 1;

    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState The Bundle that contains the data supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the recycler view
        mRecyclerView = findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // COMPLETED (4) Create a GoogleApiClient with the LocationServices API and GEO_DATA_API
         /*
         This client is needed to connect to Google Play Services and communicate with the APIs.

         - addConnectionCallbacks and addOnConnectionFailedListener are setting the callbacks that will
         notify when a connection is successful
         - enableAutoManage means that tha client will connect/disconnect on its own

         */
        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
    }

    // COMPLETED (5) Override onConnected, onConnectionSuspended and onConnectionFailed for GoogleApiClient
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "API Client Connection Successful!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "API Client Connection Failed!");
    }

    // COMPLETED (7) Override onResume and inside it initialize the location permissions checkbox

    @Override
    protected void onResume() {
        super.onResume();
        CheckBox locationPermissions = findViewById(R.id.location_permission_checkbox);
        // If there is no permission granted for ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissions.setChecked(false);
        } else {
            locationPermissions.setChecked(true);
            // If the user checks the checkbox, we disable it so they cannot uncheck it
            locationPermissions.setEnabled(false);
        }
    }

    // COMPLETED (8) Implement onLocationPermissionClicked to handle the CheckBox click event
    public void onLocationPermissionClicked(View view) {
        // When the user checks the CheckBox, since we disable it, we don't have to check
        // if it is checked or unchecked, we just request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

    }

    // COMPLETED (9) Implement the Add Place Button click event to show  a toast message with the permission status
    public void onAddPlaceButton(View view) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission to access your location is denied!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission to access your location granted!", Toast.LENGTH_SHORT).show();
        }

    }
}
