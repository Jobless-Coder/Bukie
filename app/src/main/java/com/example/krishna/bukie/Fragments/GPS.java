package com.example.krishna.bukie.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


public class GPS {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION =21 ;
    private static final String TAG = "GPS";
    //private static final TAG ="GPS";
    private static boolean pGps, pNetwork;
    private static LocationManager locManager;
    private static String provider;
    private static double longitude;
    private static double latitude;
    private static Context context;
    private static Activity activity;
    public GPS(Context context, Activity activity) {
        this.context=context;
        this.activity=activity;
    }

    private static void updateAvailability() {
        try {
            pNetwork = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            provider = LocationManager.NETWORK_PROVIDER;
        } catch (Exception ex) {
            Log.w(TAG, "Ex getting NETWORK provider");
        }
        try {
            pGps = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            provider = LocationManager.GPS_PROVIDER;
        } catch (Exception ex) {
            Log.w(TAG, "Ex getting GPS provider");
        }
    }

    public static Location getLastLocation(Context ctx) {
        Location loc = null;
        if (ctx != null) {
            if (locManager == null) {
                locManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            }
            updateAvailability();
            if (provider != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
                loc = locManager.getLastKnownLocation(provider);
            }
        }
        return loc;
    }


}
