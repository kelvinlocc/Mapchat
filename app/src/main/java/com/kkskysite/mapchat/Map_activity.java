package com.kkskysite.mapchat;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//
/*
* this app is developed by kelvin lo hi chiu (as writer), anyone who use this app without writer's permission will be asked
* */
//
public class Map_activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location userCurrentLocation;
    final  String TAG = this.getClass().getName();
    mySharedPreference preference;
    Button addText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        addText = (Button) findViewById(R.id.btn_add_text);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        preference = new mySharedPreference();
        userCurrentLocation = new Location("user current location");

        preference.getUserLocationKey();
        SharedPreferences sharedPreferences = getSharedPreferences(preference.getUserLocationKey(),MODE_PRIVATE);
        String x =  sharedPreferences.getString(preference.getUserLocationLongitude(),"");
        String y = sharedPreferences.getString(preference.getUserLocationLatitude(),"");
        userCurrentLocation.setLongitude(Double.parseDouble(x));
        userCurrentLocation.setLatitude(Double.parseDouble(y));


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                lstLatLngs.add(latLng);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.location);
                Bitmap bitmap = bitmapDrawable.getBitmap();
                BitmapDescriptor bitmapDescriptor= BitmapDescriptorFactory.fromBitmap(bitmap);

                Log.i(TAG, "you click on "+latLng.latitude+","+latLng.longitude);
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.title("new market");
                markerOption.icon(bitmapDescriptor);
                markerOption.position(latLng);

//                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.addMarker(markerOption);



            }
        });
        LatLng userLocation = new LatLng(userCurrentLocation.getLatitude(),userCurrentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker in Sydney"));
        moveMap(userLocation);
    }

    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(18)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


}
