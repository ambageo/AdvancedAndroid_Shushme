package com.example.android.shushme;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

public class Geofencing implements ResultCallback {
    public static final String TAG= Geofencing.class.getName();

    public static final int GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000; //24 hours
    public static final int GEOFENCE_RADIUS = 50; //meters
    // COMPLETED (1) Create a Geofencing class with a Context and GoogleApiClient constructor that
// initializes a private member ArrayList of Geofences called mGeofenceList
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    public Geofencing(Context context, GoogleApiClient client) {
        mContext = context;
        mGoogleApiClient = client;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
    }

    // COMPLETED (2) Inside Geofencing, implement a public method called updateGeofencesList that
// given a PlaceBuffer will create a Geofence object for each Place using Geofence.Builder
// and add that Geofence to mGeofenceList
    public void updateGeofencesList(PlaceBuffer places) {
        mGeofenceList = null;
        if (places == null || places.getCount() == 0) return;
        for (Place place : places) {
            // Get the unique ID, latitude and longitude of the place
            String placeUID = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLng = place.getLatLng().longitude;
            // Build a Geofence Object (details in Lesson 5:Places- 18.Geofencing
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeUID)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                    // We set the transition whenever the user enters or exits the Geofence
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            // Add the Geofence Object to the list
            mGeofenceList.add(geofence);

        }
    }

// COMPLETED (3) Inside Geofencing, implement a private helper method called getGeofencingRequest that
// uses GeofencingRequest.Builder to return a GeofencingRequest object from the Geofence list
private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder= new GeofencingRequest.Builder();
        // setInitialTrigger is used to specify what happens in case the device is already inside any
        // of the Geofences that are going to be registered
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
}

// COMPLETED (5) Inside Geofencing, implement a private helper method called getGeofencePendingIntent that
// returns a PendingIntent for the GeofenceBroadcastReceiver class
    private PendingIntent getGeofencePendingIntent(){
        // Reuse the Pending Intent if it already exists.
        if(mGeofencePendingIntent!= null){
            return mGeofencePendingIntent;
        }
        Intent intent= new Intent(mContext, GeofenceBroadcastReceiver.class);
        // The UPDATE_CURRENT flag indicates that if the PendingIntent already exists, then keep it
        // but replace its extra data with what is in the new intent.
        mGeofencePendingIntent= PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

// COMPLETED (6) Inside Geofencing, implement a public method called registerAllGeofences that
// registers the GeofencingRequest by calling LocationServices.GeofencingApi.addGeofences
// using the helper functions getGeofencingRequest() and getGeofencePendingIntent()
public void registerAllGeoFences() {
    // First, check that the API client is connected and that the Geofence list has Geofences
    if (mGoogleApiClient == null || !mGoogleApiClient.isConnected() ||
            mGeofenceList == null || mGeofenceList.size() == 0) {
        return;
    }
    try {
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent())
                .setResultCallback(this);
    } catch (SecurityException e) {
        // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        Log.e(TAG, e.getMessage());
    }

}
    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, String.format("Error adding/removing geofence : %s",
                result.getStatus().toString()));
    }
// COMPLETED (7) Inside Geofencing, implement a public method called unRegisterAllGeofences that
// unregisters all geofences by calling LocationServices.GeofencingApi.removeGeofences
// using the helper function getGeofencePendingIntent()
    public void unRegisterAllGeofences(){
        if(mGoogleApiClient== null || !mGoogleApiClient.isConnected()){
            return;
        } try {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeofencePendingIntent())
                    .setResultCallback(this);
        } catch (SecurityException e){
            Log.e(TAG, e.getMessage());
        }

    }

}
