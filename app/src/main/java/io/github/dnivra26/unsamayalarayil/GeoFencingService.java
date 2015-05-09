package io.github.dnivra26.unsamayalarayil;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class GeoFencingService implements GoogleApiClient.ConnectionCallbacks, ResultCallback<Status>, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = GeoFencingService.class.getSimpleName();

    @Override
    public void onResult(Status status) {
        if(status.getStatus().isSuccess()){
            Log.d(TAG, "Successfully added GeoFence :) :)");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                geofencingRequest,
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private ArrayList<Geofence> geofenceList;
    private GeofencingRequest geofencingRequest;
    Context context;
    private GoogleApiClient googleApiClient;

    public GeoFencingService(Context context){
        this.context = context;
    }

    public void addLocationReminder(String itemnName, Double lattitude, Double longitude) {
        geofenceList = new ArrayList<Geofence>();
        geofenceList.add(new Geofence.Builder()
                .setRequestId(itemnName)

                .setCircularRegion(
                        lattitude,
                        longitude,
                        100
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(1000 * 60 * 10)
                .build());
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        geofencingRequest = builder.build();
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

    }

    private PendingIntent getGeofencePendingIntent() {

        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }
}
