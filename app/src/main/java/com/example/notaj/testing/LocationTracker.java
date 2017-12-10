package com.example.notaj.testing;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by notaj on 10/12/2017.
 */

public class LocationTracker implements LocationListener {

    private LocationManager locationManager;
    private SharedPrefs prefs;
    private String keyValue;
    private String name;

    public LocationTracker(AppCompatActivity activity, SharedPrefs prefs, String keyValue, String name){
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET},
                    1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.prefs = prefs;
        this.keyValue = keyValue;
        this.name = name;

    }

    public void turnOffListener(){
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        if(location.getLatitude() == 0.0 && location.getLongitude() == 0.0){
            String[] list;
            list = prefs.getSharedPrefs().getString("userPositionInfo", "0.0,0.0,ERROR").split(",");

            prefs.getSharedPrefsEditor().putString(keyValue, ""+list[0]+","+list[1]+"," + name);
            prefs.getSharedPrefsEditor().commit();
        }else{
            prefs.getSharedPrefsEditor().putString(keyValue, ""+location.getLatitude()+","+location.getLongitude()+"," + name);
            prefs.getSharedPrefsEditor().commit();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
