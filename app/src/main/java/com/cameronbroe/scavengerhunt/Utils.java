package com.cameronbroe.scavengerhunt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

class Location {
    public double latitude;
    public double longitude;
    public boolean isVisible;
    public boolean isVisited;
}

public class Utils {
    static final String TAG = "Utils";

    public static boolean dataFileExists(String filePath) {

        File dataFile = new File(filePath);
        return dataFile.exists();
    }

    public static JSONObject createDataFile(String filePath) {
        Location[] initialLocationData = new Location[5];

        initialLocationData[0] = new Location();
        initialLocationData[0].latitude = 35.1065016;
        initialLocationData[0].longitude = -89.9194546;
        initialLocationData[0].isVisible = true;
        initialLocationData[0].isVisited = false;

        initialLocationData[1] = new Location();
        initialLocationData[1].latitude = 35.093788;
        initialLocationData[1].longitude = -89.8664925;
        initialLocationData[1].isVisible = true;
        initialLocationData[1].isVisited = false;

        initialLocationData[2] = new Location();
        initialLocationData[2].latitude = 35.1380182;
        initialLocationData[2].longitude = -90.0122281;
        initialLocationData[2].isVisible = true;
        initialLocationData[2].isVisited = false;

        initialLocationData[3] = new Location();
        initialLocationData[3].latitude = 35.1617773;
        initialLocationData[3].longitude = -90.0566597;
        initialLocationData[3].isVisible = true;
        initialLocationData[3].isVisited = false;

        initialLocationData[4] = new Location();
        initialLocationData[4].latitude = 35.065505;
        initialLocationData[4].longitude = -89.7621286;
        initialLocationData[4].isVisible = false;
        initialLocationData[4].isVisited = false;

        JSONObject initialData = new JSONObject();
        JSONArray locations = new JSONArray();
        for(int i = 0; i < initialLocationData.length; i++) {
            Log.d(TAG, "Serializing initial data to JSON");
            JSONObject locationData = new JSONObject();
            try {
                if(initialLocationData[i] != null) {
                    locationData.put("latitude", initialLocationData[i].latitude);
                    locationData.put("longitude", initialLocationData[i].longitude);
                    locationData.put("visible", initialLocationData[i].isVisible);
                    locationData.put("visited", initialLocationData[i].isVisited);
                    locations.put(locationData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        try {
            initialData.put("locations", locations);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Log.d(TAG, initialData.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        File dataFile = new File(filePath);
        try {
            FileWriter writer = new FileWriter(dataFile);
            writer.write(initialData.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return initialData;
    }

    public static void updateJsonData(String filePath, JSONObject newData) {
        File dataFile = new File(filePath);
        try {
            FileWriter writer = new FileWriter(dataFile);
            writer.write(newData.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static JSONObject getLocationData(String filePath) {
        File dataFile = new File(filePath);
        try {
            FileReader reader = new FileReader(dataFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String data = "";
            while(bufferedReader.ready()) {
                data += bufferedReader.readLine();
            }

            JSONObject dataObject = new JSONObject(data);
            return dataObject;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String constructMapsUrl(double latitude, double longitude) {
        Uri.Builder mapsUri = new Uri.Builder();
        mapsUri.scheme("https");
        mapsUri.authority("www.google.com");
        mapsUri.path("maps/dir/");
        mapsUri.appendQueryParameter("api", "1");
        String coords = (new Double(latitude)).toString() + "," + (new Double(longitude));
        mapsUri.appendQueryParameter("destination", coords);
        return mapsUri.build().toString();
    }

    public static Uri constructLocationUri(String locationName) {
        Uri.Builder locationUri = new Uri.Builder();
        locationUri.scheme("content");
        locationUri.authority(locationName);
        return locationUri.build();
    }
}
