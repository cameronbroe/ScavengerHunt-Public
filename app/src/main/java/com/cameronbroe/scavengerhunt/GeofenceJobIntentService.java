package com.cameronbroe.scavengerhunt;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeofenceJobIntentService extends JobIntentService {
    private static final int JOB_ID = 573;

    private static final String TAG = "GeofenceTransitionsIS";

    private static final String CHANNEL_ID = "channel_01";

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceJobIntentService.class, JOB_ID, intent);
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleWork(Intent intent) {
        Uri dataFromIntent = intent.getData();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        String filePath = this.getFilesDir().getAbsolutePath() + "/scavenger_hunt.json";

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);

            try {
                JSONObject locationData = Utils.getLocationData(filePath);
                JSONArray locationsData = locationData.getJSONArray("locations");
                JSONObject location;
                switch (dataFromIntent.getAuthority()) {
                    case "dixon-gallery":
                        location = locationsData.getJSONObject(0);
                        location.put("visited", true);
                        Utils.updateJsonData(filePath, locationData);
                        sendNotification("You've arrived at the Dixon Gallery!", "Tap this notification", FirstLocationActivity.class);
                        break;
                    case "lynnfield-place":
                        location = locationsData.getJSONObject(1);
                        location.put("visited", true);
                        Utils.updateJsonData(filePath, locationData);
                        sendNotification("You've arrived at Lynnfield Place!", "Tap this notification", SecondLocationActivity.class);
                        break;
                    case "minglewood-hall":
                        location = locationsData.getJSONObject(2);
                        location.put("visited", true);
                        Utils.updateJsonData(filePath, locationData);
                        sendNotification("You've arrived at Minglewood Hall!", "Tap this notification", ThirdLocationActivity.class);
                        break;
                    case "harbor-town":
                        location = locationsData.getJSONObject(3);
                        location.put("visited", true);
                        Utils.updateJsonData(filePath, locationData);
                        sendNotification("You've arrived at Harbor Town!", "Tap this notification", FourthLocationActivity.class);
                        break;
                    case "me":
                        location = locationsData.getJSONObject(4);
                        location.put("visited", true);
                        Utils.updateJsonData(filePath, locationData);
                        sendNotification(geofenceTransitionDetails, "Tap this notification", FifthLocationActivity.class);
                        break;
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            // Send notification and log the transition details.
            //sendNotification(geofenceTransitionDetails, "Tap this notification", MainActivity.class);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private void sendNotification(String notificationTitle, String notificationDetails, Class activityToLaunch) {
        int requestID = (int) System.currentTimeMillis();
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ScavengerHunt";
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Utils.constructLocationUri(activityToLaunch.getSimpleName()), getApplicationContext(), activityToLaunch);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        Log.d(TAG, "Created intent: " + notificationIntent.toString());

        // Get a PendingIntent containing the entire back stack.
        //PendingIntent notificationPendingIntent =
        //        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(getApplicationContext(), requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(TAG, "Created pending intent: " + notificationPendingIntent.toString());

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(Color.RED)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDetails)
                .setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        int notificationId = (int) System.currentTimeMillis();
        mNotificationManager.notify(notificationId, builder.build());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_dwell);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
