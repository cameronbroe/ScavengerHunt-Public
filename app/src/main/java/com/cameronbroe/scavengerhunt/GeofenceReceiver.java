package com.cameronbroe.scavengerhunt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class GeofenceReceiver extends BroadcastReceiver {
    static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(TAG, "Got a geofence!");
        GeofenceJobIntentService.enqueueWork(context, intent);
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
