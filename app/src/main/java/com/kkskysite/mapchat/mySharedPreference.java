package com.kkskysite.mapchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

/**
 * Created by Owner on 7/30/2016.
 */
public class mySharedPreference {
    final static String userLocation= "user_location";
    final static String userLocationLongitude = "user_location_longitude";
    final static String userLocationLatitude = "user_location_latitude";
    final String TAG = this.getClass().getName();
    Location userCurrentLocation;


    public String getUserLocationKey () {
        return userLocation;
    }

    public String getUserLocationLongitude() {
        return userLocationLongitude;

    }
    public String getUserLocationLatitude (){
        return userLocationLatitude;

    }

    public void setUserCurrentLocation(Double Longitude, Double Latitude, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(userLocation,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userLocationLongitude,Double.toString(Longitude));
        editor.putString(userLocationLatitude,Double.toString(Latitude));
        editor.apply();

    }

    public void setUserCurrentLocation(String Longitude, String Latitude, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(userLocation,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userLocationLongitude,Longitude);
        editor.putString(userLocationLatitude,Latitude);
        editor.apply();

    }

    public void setUserCurrentLocation(Location location,Context context){
        setUserCurrentLocation(location.getLongitude(),location.getLatitude(),context);


    }


    public Location getUserCurrentLocation (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(userLocation,Context.MODE_PRIVATE);
        String x =  sharedPreferences.getString(userLocationLongitude,"");
        String y =sharedPreferences.getString(userLocationLatitude,"");
        if (x==""||y==""){
            return null;
        }
        Log.i(TAG, "getUserCurrentLocation: "+x+","+y);
        userCurrentLocation =new Location("user location");
        userCurrentLocation.setLongitude(Double.parseDouble(x));
        userCurrentLocation.setLatitude(Double.parseDouble(y));
        return this.userCurrentLocation;
    }





    
}
