package com.syp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.syp.model.Singleton;

import java.util.List;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        Singleton.get(context).setCurrentCafeId(triggeringGeofences.get(0).getRequestId());
        MainActivity mainActivity = MainActivity.getStaticMainActivity();

        boolean isLoggedIn = Singleton.get(mainActivity).isLoggedIn();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER && isLoggedIn ) {
            mainActivity.popUpEnteredCafe(triggeringGeofences.get(0).getRequestId());
        }

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT && isLoggedIn){
            mainActivity.popUpExitedCafe(triggeringGeofences.get(0).getRequestId());
        }
    }
}
