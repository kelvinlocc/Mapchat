package com.kkskysite.mapchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
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
import com.google.maps.android.ui.BubbleIconFactory;
import com.google.maps.android.ui.IconGenerator;

import java.security.cert.CollectionCertStoreParameters;

//
/*
* this app is developed by kelvin lo hi chiu (as writer), anyone who use this app without writer's permission will be asked
* */
//
public class Map_activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location userCurrentLocation;
    final String TAG = this.getClass().getName();
    mySharedPreference preference;
    Button addText;
    Boolean googleMapClickable = false;
    Context context;
    LatLng userLocation;
    PopupWindow addText_window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        context = this;
        Log.i(TAG, "onCreate: ");

        addText = (Button) findViewById(R.id.btn_add_text);
        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMapClickable = true;
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        preference = new mySharedPreference();
        userCurrentLocation = new Location("user current location");

        preference.getUserLocationKey();
        SharedPreferences sharedPreferences = getSharedPreferences(preference.getUserLocationKey(), MODE_PRIVATE);
        String x = sharedPreferences.getString(preference.getUserLocationLongitude(), "");
        String y = sharedPreferences.getString(preference.getUserLocationLatitude(), "");
        userCurrentLocation.setLongitude(Double.parseDouble(x));
        userCurrentLocation.setLatitude(Double.parseDouble(y));
        userLocation = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());

        Log.i(TAG, "onCreate: end");
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Toast.makeText(Map_activity.this, "update !", Toast.LENGTH_LONG).show();

        mMap = googleMap;
        mMap.addMarker(createBubbleIcon("my location"));


        moveMap(userLocation);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (googleMapClickable) {

                    mMap.addMarker(createBubbleIcon("new location",latLng)).showInfoWindow();
                    initiatePopupWindow();

                    googleMapClickable = false;
                }
                else {
                    Toast.makeText(Map_activity.this, "please click add button first!", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void moveMap(LatLng place) {
        Log.i(TAG, "moveMap: ");
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(18)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private MarkerOptions createBubbleIcon (String string) {
        IconGenerator icon = new IconGenerator(this);
        MarkerOptions markerOption = new MarkerOptions();

        Bitmap bitmap = icon.makeIcon(string);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        markerOption.icon(bitmapDescriptor);
        markerOption.position(userLocation);
        markerOption.title("title");
        return markerOption;
    }

    private MarkerOptions createBubbleIcon (String string,LatLng latlng) {
        IconGenerator icon = new IconGenerator(this);
        MarkerOptions markerOption = new MarkerOptions();

        Bitmap bitmap = icon.makeIcon(string);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        markerOption.icon(bitmapDescriptor);
        markerOption.position(latlng);
        markerOption.title("title");
        return markerOption;
    }

    public BitmapDescriptor createPureTextIcon(String text) {

        Paint textPaint = new Paint(); // Adapt to your needs

        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();
        textPaint.setTextSize(50);

        int width = (int) (textWidth);
        int height = (int) (textHeight);
        Log.d(TAG, "createPureTextIcon: text Width&Height"+width+","+height);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.translate(0, height);

        // For development only:
        // Set a background in order to see the
        // full size and positioning of the bitmap.
        // Remove that for a fully transparent icon.
        canvas.drawColor(Color.LTGRAY);

        canvas.drawText(text, 0, 0, textPaint);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image);
        return icon;
    }


    public void initiatePopupWindow (){
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.popup_screen, (ViewGroup) this.findViewById(R.id.popup_element));

        addText_window = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        addText_window.showAtLocation(layout, Gravity.CENTER,0,0);

    }
}
