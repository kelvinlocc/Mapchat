package com.kkskysite.mapchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
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
import com.google.maps.android.ui.IconGenerator;
import com.kkskysite.mapchat.Uility.RESTService;
import com.kkskysite.mapchat.Uility.marker_dataModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    PopupWindow popupWindow_addText;
    EditText inputText, inputTextBody;
    Button buttonConfirm, buttonCancel, buttonUpdate;
    TextView admin_text_message;
    LatLng new_marker_latLng;

    RESTService service;
    marker_dataModel myMarker_data;
    ArrayList<marker_dataModel> myMarker_dataArray;
    HashSet hSet;

//    String

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        context = this;
        service = new RESTService(this);
        hSet = new HashSet();
        Log.i(TAG, "onCreate2: ");



        addText = (Button) findViewById(R.id.btn_add_text);
        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMapClickable = true;
            }
        });

        //get admin message from server:
        admin_text_message = (TextView) findViewById(R.id.admin_message);
        service.get_admin_message("junk", new RESTService.onAjaxFinishedListener() {
            @Override
            public void onFinished(String url, String json, AjaxStatus status) throws JSONException {
                Log.i(TAG, "onFinished: json:" + json);
                Toast.makeText(Map_activity.this, "json is :" + json, Toast.LENGTH_SHORT).show();
                admin_text_message.setText(json);
            }
        });
        //get all marker from server;
        myMarker_dataArray = new ArrayList<marker_dataModel>();

        //update the map:
        updateMap_data();

        //update button:
        buttonUpdate = (Button) findViewById(R.id.btn_update);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMap_data();
                Toast.makeText(Map_activity.this, "updating...", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < myMarker_dataArray.size(); i++) {
                    Log.i(TAG, "onCreate: username: " + myMarker_dataArray.get(i).getUsername());
                    String username = myMarker_dataArray.get(i).getUsername();
                    String string_title = myMarker_dataArray.get(i).getString_title();
                    String string_body = myMarker_dataArray.get(i).getString_body();
                    Log.i(TAG, "onClick:LOLA " + myMarker_dataArray.get(i).getLongitude() + "," + myMarker_dataArray.get(i).getLatitude());
                    Double x = Double.parseDouble(myMarker_dataArray.get(i).getLongitude());
                    Double y = Double.parseDouble(myMarker_dataArray.get(i).getLatitude());
                    LatLng new_location = new LatLng(y, x);

                    //check if the marker is already existed!
                    if (!isMarkerExist(x)) {
                        hSet.add(x);
                        Log.i(TAG, "onClick: add new marker x: "+x);
                        Log.i(TAG, "onClick: add new marker hSet.size: "+hSet.size());
                        add_marker(username, string_title, string_body, new_location);
                    }


                }


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

    public Boolean isMarkerExist(Double x) {

        Log.i(TAG, "isMarkerExist?: Double of x: "+x);
        Boolean isExist = hSet.contains(x);
        return isExist;
    }

    public void updateMap_data() {
        service.get_marker("Hong Kong", new RESTService.onAjaxFinishedListener() {
            @Override
            public void onFinished(String url, String json, AjaxStatus status) throws JSONException {
                Log.i(TAG, "onFinished: url,json:" + url + "," + json);
                String[] row = json.trim().split("\\|");
                for (int i = 0; i < row.length; i++) {
                    String[] col = row[i].split("\\,");
                    myMarker_data = new marker_dataModel();
                    myMarker_data.setUsername(col[0]);
                    myMarker_data.setLongitude(col[1]);
                    myMarker_data.setLatitude(col[2]);
                    myMarker_data.setString_title(col[3]);
                    myMarker_data.setString_body(col[4]);
                    myMarker_data.setTime(col[5]);
                    myMarker_dataArray.add(i, myMarker_data);
                }
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Toast.makeText(Map_activity.this, "update !", Toast.LENGTH_LONG).show();

        mMap = googleMap;
        mMap.addMarker(createBubbleIcon("my location", "body"));


        moveMap(userLocation);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng new_location) {

                if (googleMapClickable) {
//                    new_marker_latLng  = point;
                    initiatePopupWindow(new_location);

                    googleMapClickable = false;
                } else {
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

    private MarkerOptions createBubbleIcon(String string_title, String string_body) {
        IconGenerator icon = new IconGenerator(this);
        MarkerOptions markerOption = new MarkerOptions();

        Bitmap bitmap = icon.makeIcon(string_title);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        markerOption.icon(bitmapDescriptor);
        markerOption.position(userLocation);
        markerOption.title(string_body);
        return markerOption;
    }

    private MarkerOptions createBubbleIcon(String string_title, String string_body, LatLng latlng) {
        IconGenerator icon = new IconGenerator(this);
        MarkerOptions markerOption = new MarkerOptions();

        Bitmap bitmap = icon.makeIcon(string_title);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        markerOption.icon(bitmapDescriptor);
        markerOption.position(latlng);
        markerOption.title(string_body);
        return markerOption;
    }

    public BitmapDescriptor createPureTextIcon(String text) {

        Paint textPaint = new Paint(); // Adapt to your needs

        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();
        textPaint.setTextSize(50);

        int width = (int) (textWidth);
        int height = (int) (textHeight);
        Log.d(TAG, "createPureTextIcon: text Width&Height" + width + "," + height);
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


    public void initiatePopupWindow(final LatLng new_location) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.popup_screen, (ViewGroup) this.findViewById(R.id.popup_element));
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Log.i(TAG, "initiatePopupWindow: " + layout.getMeasuredHeight() + "," + layout.getMeasuredWidth());
//        popupWindow_addText = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow_addText = new PopupWindow(layout, layout.getMeasuredWidth(), layout.getMeasuredHeight(), true);
        popupWindow_addText.showAtLocation(layout, Gravity.CENTER, 0, 0);
        inputText = (EditText) layout.findViewById(R.id.input_text);
        inputTextBody = (EditText) layout.findViewById(R.id.input_text_body);
        buttonConfirm = (Button) layout.findViewById(R.id.btn_confirm);
        buttonCancel = (Button) layout.findViewById(R.id.btn_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow_addText.dismiss();
                Toast.makeText(Map_activity.this, "you close the pop up window", Toast.LENGTH_SHORT).show();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string_title = inputText.getText().toString().trim();
                String string_body = inputTextBody.getText().toString().trim();
                Toast.makeText(Map_activity.this, "you confirm the text to add!", Toast.LENGTH_SHORT).show();

                Log.i(TAG, "?onClick: " + string_body);
                add_marker("username_from android", string_title, string_body, new_location);
                popupWindow_addText.dismiss();

            }
        });


    }

    public void add_marker(String username, String string_title, String string_body, LatLng new_location) {
        String longitude = Double.toString(new_location.longitude);
        String latitude = Double.toString(new_location.latitude);

        mMap.addMarker(createBubbleIcon(string_title, string_body, new_location)).showInfoWindow();
        service.add_new_marker(username, longitude, latitude, string_title, string_body, "time", new RESTService.onAjaxFinishedListener() {
            @Override
            public void onFinished(String url, String json, AjaxStatus status) throws JSONException {
                Log.i(TAG, "onFinished: url,json" + url + "," + json);
                Toast.makeText(Map_activity.this, "Json: " + json, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
