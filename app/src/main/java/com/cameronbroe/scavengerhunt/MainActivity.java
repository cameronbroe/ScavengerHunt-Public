package com.cameronbroe.scavengerhunt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.internal.StatusPendingResult;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements OnCompleteListener<Void> {
    private static String TAG = "MainActivity";

    private static int visitedColor = Color.GREEN;
    private static int notVisitedColor = Color.RED;
    private static int notVisitedTextColor = Color.WHITE;
    private static int visitedTextColor = Color.DKGRAY;
    private static int disabledColor = Color.GRAY;

    private JSONObject loadedLocationData;

    private GeofencingClient geofencingClient;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final long GEOFENCE_TIMEOUT = 1000 * 60 * 60 * 2; // 2 hours

    static final String CHANNEL_ID = "channel_01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geofencingClient = LocationServices.getGeofencingClient(this);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        }

        ((TextView) findViewById(R.id.textView)).setText("Tap the coordinates to start GPS");
        Log.i(TAG, "Activity started!");
        String filePath = this.getFilesDir().getAbsolutePath() + "/scavenger_hunt.json";
        Log.d(TAG, "Data file path " + filePath);
        if(!Utils.dataFileExists(filePath)) {
            loadedLocationData = Utils.createDataFile(filePath);
        } else {
            loadedLocationData = Utils.getLocationData(filePath);
        }
        try {
            Log.d(TAG, "Read data from file: " + loadedLocationData.toString(4));

            JSONArray locations = loadedLocationData.getJSONArray("locations");
            if(locations.length() >= 1) {
                JSONObject location = locations.getJSONObject(0);
                boolean isVisible = location.getBoolean("visible");
                boolean isVisited = location.getBoolean("visited");
                String buttonText = isVisited ? "Dixon Gallery" : "First Location";
                Button button = findViewById(R.id.button);
                button.setTextColor(isVisited ? visitedTextColor : notVisitedTextColor);
                button.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
                boolean shouldBeEnabled = locationButtonShouldBeEnabled(0);
                if(shouldBeEnabled) {
                    button.setBackgroundColor(isVisited ? visitedColor : notVisitedColor);
                } else {
                    button.setBackgroundColor(disabledColor);
                }
                button.setText(buttonText);
                button.setEnabled(shouldBeEnabled);
            }

            if(locations.length() >= 2) {
                JSONObject location = locations.getJSONObject(1);
                boolean isVisible = location.getBoolean("visible");
                boolean isVisited = location.getBoolean("visited");
                String buttonText = isVisited ? "Lynnfield Place" : "Second Location";
                Button button = findViewById(R.id.button2);
                button.setTextColor(isVisited ? visitedTextColor : notVisitedTextColor);
                button.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
                boolean shouldBeEnabled = locationButtonShouldBeEnabled(1);
                if(shouldBeEnabled) {
                    button.setBackgroundColor(isVisited ? visitedColor : notVisitedColor);
                } else {
                    button.setBackgroundColor(disabledColor);
                }
                button.setText(buttonText);
                button.setEnabled(shouldBeEnabled);
            }

            if(locations.length() >= 3) {
                JSONObject location = locations.getJSONObject(2);
                boolean isVisible = location.getBoolean("visible");
                boolean isVisited = location.getBoolean("visited");
                String buttonText = isVisited ? "Minglewood Hall" : "Third Location";
                Button button = findViewById(R.id.button3);
                button.setTextColor(isVisited ? visitedTextColor : notVisitedTextColor);
                button.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
                boolean shouldBeEnabled = locationButtonShouldBeEnabled(2);
                if(shouldBeEnabled) {
                    button.setBackgroundColor(isVisited ? visitedColor : notVisitedColor);
                } else {
                    button.setBackgroundColor(disabledColor);
                }
                button.setText(buttonText);
                button.setEnabled(shouldBeEnabled);
            }

            if(locations.length() >= 4) {
                JSONObject location = locations.getJSONObject(3);
                boolean isVisible = location.getBoolean("visible");
                boolean isVisited = location.getBoolean("visited");
                String buttonText = isVisited ? "Harbor Town" : "Fourth Location";
                Button button = findViewById(R.id.button4);
                button.setTextColor(isVisited ? visitedTextColor : notVisitedTextColor);
                button.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
                boolean shouldBeEnabled = locationButtonShouldBeEnabled(3);
                if(shouldBeEnabled) {
                    button.setBackgroundColor(isVisited ? visitedColor : notVisitedColor);
                } else {
                    button.setBackgroundColor(disabledColor);
                }
                button.setText(buttonText);
                button.setEnabled(shouldBeEnabled);
            }

            if(locations.length() == 5) {
                JSONObject location = locations.getJSONObject(4);
                boolean isVisible = location.getBoolean("visible");
                boolean isVisited = location.getBoolean("visited");
                String buttonText = isVisited ? "Me!" : "Fifth Location";
                Button button = findViewById(R.id.button5);
                button.setTextColor(isVisited ? visitedTextColor : notVisitedTextColor);
                button.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
                boolean shouldBeEnabled = locationButtonShouldBeEnabled(4);
                if(shouldBeEnabled) {
                    button.setBackgroundColor(isVisited ? visitedColor : notVisitedColor);
                } else {
                    button.setBackgroundColor(disabledColor);
                }
                button.setText(buttonText);
                button.setEnabled(shouldBeEnabled);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean locationButtonShouldBeEnabled(int index) {
        if(index == 0) {
            return true;
        } else {
            boolean shouldBeEnabled = true;
            try {
                JSONArray locations = loadedLocationData.getJSONArray("locations");
                for(int i = 0; i < index; i++) {
                    JSONObject locationData = locations.getJSONObject(i);
                    boolean previousLocationHasBeenVisited = locationData.getBoolean("visited");
                    if(!previousLocationHasBeenVisited) {
                        shouldBeEnabled = false;
                    }
                }
                return shouldBeEnabled;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void firstLocation(View v) {
        Log.d(TAG, "First location tapped");
        try {
            JSONArray locations = loadedLocationData.getJSONArray("locations");
            JSONObject locationData = locations.getJSONObject(0);
            if(locationData.getBoolean("visited")) {
                Log.d(TAG, "Location already visited");
                Intent firstLocationActivityIntent = new Intent(getApplicationContext(), FirstLocationActivity.class);
                startActivity(firstLocationActivityIntent);
            } else {
                int requestId = (int) System.currentTimeMillis();
                double latitude = locationData.getDouble("latitude");
                double longitude = locationData.getDouble("longitude");
                String fullMapsUrl = Utils.constructMapsUrl(latitude, longitude);
                Log.d(TAG, "Going to open maps with URI: " + fullMapsUrl);

                Log.d(TAG, "Building geofence for location 1");
                Geofence geofence = new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId("dixon-gallery")

                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                latitude,
                                longitude,
                                75.0f
                        )

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(30000)

                        // Create the geofence.
                        .build();
                Log.d(TAG, "Built geofence: " + geofence.toString());

                Log.d(TAG, "Building request for geofence");
                GeofencingRequest.Builder geofenceRequestBuilder = new GeofencingRequest.Builder();
                geofenceRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
                geofenceRequestBuilder.addGeofence(geofence);

                GeofencingRequest geofencingRequest = geofenceRequestBuilder.build();
                Log.d(TAG, "Geofence request built: " + geofencingRequest.toString());

                Intent geofenceIntent = new Intent( Intent.ACTION_VIEW, Utils.constructLocationUri("dixon-gallery"), this, GeofenceReceiver.class);
                Log.d(TAG, "Created geofence intent: " + geofenceIntent.toString());

                PendingIntent firstLocationPendingIntent = PendingIntent.getBroadcast(this, requestId, geofenceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d(TAG, "Created pending intent: " + firstLocationPendingIntent.toString());

                if(checkPermissions()) {
                    geofencingClient.addGeofences(geofencingRequest, firstLocationPendingIntent).addOnCompleteListener(this);
                    Log.d(TAG, "Added geofence to client");
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullMapsUrl));
                startActivity(intent);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void secondLocation(View v) {
        Log.d(TAG, "Second location tapped");
        try {
            JSONArray locations = loadedLocationData.getJSONArray("locations");
            JSONObject locationData = locations.getJSONObject(1);
            if(locationData.getBoolean("visited")) {
                Log.d(TAG, "Location already visited");
                Intent firstLocationActivityIntent = new Intent(getApplicationContext(), SecondLocationActivity.class);
                startActivity(firstLocationActivityIntent);
            } else {
                int requestId = (int) System.currentTimeMillis();
                double latitude = locationData.getDouble("latitude");
                double longitude = locationData.getDouble("longitude");
                String fullMapsUrl = Utils.constructMapsUrl(latitude, longitude);
                Log.d(TAG, "Going to open maps with URI: " + fullMapsUrl);

                Log.d(TAG, "Building geofence for location 1");
                Geofence geofence = new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId("lynnfield-place")

                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                latitude,
                                longitude,
                                75.0f
                        )

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(30000)

                        // Create the geofence.
                        .build();
                Log.d(TAG, "Built geofence: " + geofence.toString());

                Log.d(TAG, "Building request for geofence");
                GeofencingRequest.Builder geofenceRequestBuilder = new GeofencingRequest.Builder();
                geofenceRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
                geofenceRequestBuilder.addGeofence(geofence);

                GeofencingRequest geofencingRequest = geofenceRequestBuilder.build();
                Log.d(TAG, "Geofence request built: " + geofencingRequest.toString());

                Intent geofenceIntent = new Intent( Intent.ACTION_VIEW, Utils.constructLocationUri("lynnfield-place"), this, GeofenceReceiver.class);
                Log.d(TAG, "Created geofence intent: " + geofenceIntent.toString());

                PendingIntent firstLocationPendingIntent = PendingIntent.getBroadcast(this, requestId, geofenceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d(TAG, "Created pending intent: " + firstLocationPendingIntent.toString());

                if(checkPermissions()) {
                    geofencingClient.addGeofences(geofencingRequest, firstLocationPendingIntent).addOnCompleteListener(this);
                    Log.d(TAG, "Added geofence to client");
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullMapsUrl));
                startActivity(intent);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void thirdLocation(View v) {
        Log.d(TAG, "Third location tapped");
        try {
            JSONArray locations = loadedLocationData.getJSONArray("locations");
            JSONObject locationData = locations.getJSONObject(2);
            if(locationData.getBoolean("visited")) {
                Log.d(TAG, "Location already visited");
                Intent firstLocationActivityIntent = new Intent(getApplicationContext(), ThirdLocationActivity.class);
                startActivity(firstLocationActivityIntent);
            } else {
                int requestId = (int) System.currentTimeMillis();
                double latitude = locationData.getDouble("latitude");
                double longitude = locationData.getDouble("longitude");
                String fullMapsUrl = Utils.constructMapsUrl(latitude, longitude);
                Log.d(TAG, "Going to open maps with URI: " + fullMapsUrl);

                Log.d(TAG, "Building geofence for location 1");
                Geofence geofence = new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId("minglewood-hall")

                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                latitude,
                                longitude,
                                75.0f
                        )

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(30000)

                        // Create the geofence.
                        .build();
                Log.d(TAG, "Built geofence: " + geofence.toString());

                Log.d(TAG, "Building request for geofence");
                GeofencingRequest.Builder geofenceRequestBuilder = new GeofencingRequest.Builder();
                geofenceRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
                geofenceRequestBuilder.addGeofence(geofence);

                GeofencingRequest geofencingRequest = geofenceRequestBuilder.build();
                Log.d(TAG, "Geofence request built: " + geofencingRequest.toString());

                Intent geofenceIntent = new Intent( Intent.ACTION_VIEW, Utils.constructLocationUri("minglewood-hall"), this, GeofenceReceiver.class);
                Log.d(TAG, "Created geofence intent: " + geofenceIntent.toString());

                PendingIntent firstLocationPendingIntent = PendingIntent.getBroadcast(this, requestId, geofenceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d(TAG, "Created pending intent: " + firstLocationPendingIntent.toString());

                if(checkPermissions()) {
                    geofencingClient.addGeofences(geofencingRequest, firstLocationPendingIntent).addOnCompleteListener(this);
                    Log.d(TAG, "Added geofence to client");
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullMapsUrl));
                startActivity(intent);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void fourthLocation(View v) {
        Log.d(TAG, "Fourth location tapped");
        try {
            JSONArray locations = loadedLocationData.getJSONArray("locations");
            JSONObject locationData = locations.getJSONObject(3);
            if(locationData.getBoolean("visited")) {
                Log.d(TAG, "Location already visited");
                Intent firstLocationActivityIntent = new Intent(getApplicationContext(), FourthLocationActivity.class);
                startActivity(firstLocationActivityIntent);
            } else {
                int requestId = (int) System.currentTimeMillis();
                double latitude = locationData.getDouble("latitude");
                double longitude = locationData.getDouble("longitude");
                String fullMapsUrl = Utils.constructMapsUrl(latitude, longitude);
                Log.d(TAG, "Going to open maps with URI: " + fullMapsUrl);

                Log.d(TAG, "Building geofence for location 1");
                Geofence geofence = new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId("harbor-town")

                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                latitude,
                                longitude,
                                75.0f
                        )

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(30000)

                        // Create the geofence.
                        .build();
                Log.d(TAG, "Built geofence: " + geofence.toString());

                Log.d(TAG, "Building request for geofence");
                GeofencingRequest.Builder geofenceRequestBuilder = new GeofencingRequest.Builder();
                geofenceRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
                geofenceRequestBuilder.addGeofence(geofence);

                GeofencingRequest geofencingRequest = geofenceRequestBuilder.build();
                Log.d(TAG, "Geofence request built: " + geofencingRequest.toString());

                Intent geofenceIntent = new Intent( Intent.ACTION_VIEW, Utils.constructLocationUri("harbor-town"), this, GeofenceReceiver.class);
                Log.d(TAG, "Created geofence intent: " + geofenceIntent.toString());

                PendingIntent firstLocationPendingIntent = PendingIntent.getBroadcast(this, requestId, geofenceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d(TAG, "Created pending intent: " + firstLocationPendingIntent.toString());

                if(checkPermissions()) {
                    geofencingClient.addGeofences(geofencingRequest, firstLocationPendingIntent).addOnCompleteListener(this);
                    Log.d(TAG, "Added geofence to client");
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullMapsUrl));
                startActivity(intent);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void fifthLocation(View v) {
        Log.d(TAG, "Fifth location tapped");
        try {
            JSONArray locations = loadedLocationData.getJSONArray("locations");
            JSONObject locationData = locations.getJSONObject(4);
            if(locationData.getBoolean("visited")) {
                Log.d(TAG, "Location already visited");
                Intent firstLocationActivityIntent = new Intent(getApplicationContext(), FifthLocationActivity.class);
                startActivity(firstLocationActivityIntent);
            } else {
                int requestId = (int) System.currentTimeMillis();
                double latitude = locationData.getDouble("latitude");
                double longitude = locationData.getDouble("longitude");
                String fullMapsUrl = Utils.constructMapsUrl(latitude, longitude);
                Log.d(TAG, "Going to open maps with URI: " + fullMapsUrl);

                Log.d(TAG, "Building geofence for location 1");
                Geofence geofence = new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId("me")

                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                latitude,
                                longitude,
                                75.0f
                        )

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(30000)

                        // Create the geofence.
                        .build();
                Log.d(TAG, "Built geofence: " + geofence.toString());

                Log.d(TAG, "Building request for geofence");
                GeofencingRequest.Builder geofenceRequestBuilder = new GeofencingRequest.Builder();
                geofenceRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
                geofenceRequestBuilder.addGeofence(geofence);

                GeofencingRequest geofencingRequest = geofenceRequestBuilder.build();
                Log.d(TAG, "Geofence request built: " + geofencingRequest.toString());

                Intent geofenceIntent = new Intent( Intent.ACTION_VIEW, Utils.constructLocationUri("me"), this, GeofenceReceiver.class);
                Log.d(TAG, "Created geofence intent: " + geofenceIntent.toString());

                PendingIntent firstLocationPendingIntent = PendingIntent.getBroadcast(this, requestId, geofenceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d(TAG, "Created pending intent: " + firstLocationPendingIntent.toString());

                if(checkPermissions()) {
                    geofencingClient.addGeofences(geofencingRequest, firstLocationPendingIntent).addOnCompleteListener(this);
                    Log.d(TAG, "Added geofence to client");
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullMapsUrl));
                startActivity(intent);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            /*updateGeofencesAdded(!getGeofencesAdded());
            setButtonsEnabledState();*/

            /*int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;*/
            Toast.makeText(this, "Geofence added", Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }
}
