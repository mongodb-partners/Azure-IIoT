package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.trackerapp.Model.TrackingGeoSpatial;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trackerapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnPolylineClickListener{
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    String Appid;
    public Realm realm;
    public String[] registration_number;
    String value;
    Button refresh;
    Button home;
    List<String> latList = new ArrayList<>();
    List<String> lonList = new ArrayList<>();
    List<LatLng> latlonList = new ArrayList<>();
    MyApplication dbConfigs = new MyApplication();
    Realm backgroundThreadRealm;
    public List<TrackingGeoSpatial> tracking_data = new ArrayList<>();
    SupportMapFragment mapFragment;
    String timeline_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        timeline_url = dbConfigs.getTimeline_url();
        Appid = dbConfigs.getAppid();
        backgroundThreadRealm = dbConfigs.getAppConfigs();

        super.onCreate(savedInstanceState);
        // Get the value passed from intent in previous activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
            Log.v("Intent Value", value);
        }
        registration_number = value.split(" - ", 2);

        
        // MAP Activity
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshPage();
            }
        });

        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHomePage();
            }
        });
    }


    private void refreshPage() {
        backgroundThreadRealm.close();
        Intent intent = getIntent();
        intent.putExtra("key", value);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            // Timeline data
            URL url = new URL(timeline_url+registration_number[1].toUpperCase());
            String readLine;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                ArrayList<String> response = new ArrayList<>();

                while ((readLine = in.readLine()) != null) {
                    response.add(readLine);
                }
                in.close();
                // TODO: Parse the result and display in maps
                JSONArray response_json_array = new JSONArray(response.get(0));

                for(int i = 0; i<response_json_array.length(); i++) {
                    String lat = response_json_array.getJSONObject(i).optString("lat");
                    JSONObject lat_json = new JSONObject(lat);
                    latList.add((String) lat_json.get("$numberDouble"));
                    String lon = response_json_array.getJSONObject(i).optString("lon");
                    JSONObject lon_json = new JSONObject(lon);
                    lonList.add((String) lon_json.get("$numberDouble"));
                }

                for(int i = 0; i< latList.size(); i++) {
                    double lat1 = Double.parseDouble(latList.get(i));
                    double lon1 = Double.parseDouble(lonList.get(i));
                    LatLng latlan = new LatLng(lat1,lon1);
                    latlonList.add(latlan);
                }
            }
            PolylineOptions opts = new PolylineOptions();
            for (LatLng location : latlonList) {
                opts.add(location)
                    .color(Color.BLUE)
                    .width(10)
                    .geodesic(true);
            }

            Polyline polyline = googleMap.addPolyline(opts);
            polyline.setClickable(true);
        } catch (Exception e){
            System.out.println("EXCEPTION: "+e);
        }

        mMap = googleMap;
        LatLng custom = new LatLng(Double.parseDouble(latList.get(latList.size()-1)), Double.parseDouble(lonList.get(lonList.size()-1)));
        MarkerOptions marker = new MarkerOptions().position(custom).title(registration_number[1].toUpperCase());
        marker.icon(bitmapDescriptorFromVector(this, R.mipmap.car_icon_03));
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(custom));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));

        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                startDialogActivity();
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        System.out.println("Polyline clicked !!!!!!");
    }

    private void startDialogActivity() {

        backgroundThreadRealm.executeTransaction(transactionRealm -> {
            TrackingGeoSpatial results = backgroundThreadRealm.where(TrackingGeoSpatial.class).sort("Timestamp", Sort.DESCENDING).equalTo("reg_num", registration_number[1].toUpperCase()).findFirst();
            tracking_data.add(results);
        });

        backgroundThreadRealm.close();
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView textView = findViewById(R.id.display_msg);

        // TODO: Set the timeline data for the registration number
        textView.setText("");

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openHomePage() {
        backgroundThreadRealm.close();
        Intent intent = new Intent(this, MainActivity.class);
        Log.v("INFO>>","The Add vehicle activity started");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        backgroundThreadRealm.close();
        Intent intent = new Intent(this, MainActivity.class);
        Log.v("INFO>>","The Add vehicle activity started");
        startActivity(intent);
    }
}