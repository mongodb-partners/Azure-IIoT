package com.example.trackerapp;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.trackerapp.Model.TrackingGeoSpatial;
import com.example.trackerapp.Model.TrackingGeoSpatial_location;
import com.example.trackerapp.databinding.ActivityShowAllVehiclesBinding;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ShowAllVehiclesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityShowAllVehiclesBinding binding;
    String Appid;
    double lat;
    double lon;
    String reg_num;
    Realm backgroundThreadRealm;
    Button refresh;
    Button home;
    Button vehicle_list;
    TrackingGeoSpatial tracking=null;
    boolean inCircle;
    MyApplication dbConfigs;
    private RealmResults<TrackingGeoSpatial> results;
    List<RealmResults<TrackingGeoSpatial>> tracking_data = new ArrayList<RealmResults<TrackingGeoSpatial>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbConfigs = new MyApplication();
        backgroundThreadRealm = dbConfigs.getAppConfigs();
        Appid = dbConfigs.getAppid();

        super.onCreate(savedInstanceState);

        binding = ActivityShowAllVehiclesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundThreadRealm.close();
                openHomePage();
            }
        });

        vehicle_list = findViewById(R.id.vehicle_list);
        vehicle_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundThreadRealm.close();
                showVehicleList();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();


        backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                results = realm.where(TrackingGeoSpatial.class).sort("_modifiedTS", Sort.DESCENDING).distinct("_id").findAll();
                tracking_data.add(results);
                mMap.clear();
                set_coordinates();
            }
        });

        results.addChangeListener(new RealmChangeListener<RealmResults<TrackingGeoSpatial>>() {
            @Override
            public void onChange(RealmResults<TrackingGeoSpatial> trackingGeoSpatials) {
                tracking_data = new ArrayList<RealmResults<TrackingGeoSpatial>>();
                tracking_data.add(trackingGeoSpatials);
                mMap.clear();
                set_coordinates();

            }
        });
    }

    public  void set_coordinates() {
        for (int i = 0; i < tracking_data.get(0).size(); i++) {
            lat = tracking_data.get(0).get(i).getLocation().getCoordinates().get(0);
            lon = tracking_data.get(0).get(i).getLocation().getCoordinates().get(1);
            reg_num = tracking_data.get(0).get(i).get_id();
            // Add a marker in Sydney and move the camera
            LatLng custom = new LatLng(lat, lon);
            MarkerOptions marker = new MarkerOptions().position(custom).title(reg_num + "\n " + lat + " " + lon);
            marker.icon(bitmapDescriptorFromVector(this, R.mipmap.car_icon_03));
            mMap.addMarker(marker).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(custom));
        }
        addCircle(new LatLng(dbConfigs.getStatic_lat(),dbConfigs.getStatic_lon()), 10000f);
        filterMarkers(20000f);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }


    /* Create fence circle on googlemaps */
    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(128,0,255,255));
        circleOptions.fillColor(Color.argb(64,0,128,255));
        circleOptions.strokeWidth(3);
        mMap.addCircle(circleOptions);
    }

    private void filterMarkers(double radiusForCircle){
        float[] distance = new float[2];

        for (int i = 0; i < tracking_data.get(0).size(); i++) {
            double lat = tracking_data.get(0).get(i).getLocation().getCoordinates().get(0);
            double lon = tracking_data.get(0).get(i).getLocation().getCoordinates().get(1);
            String reg_num_1 = tracking_data.get(0).get(i).get_id();
            Location.distanceBetween(lat,lon, dbConfigs.getStatic_lat(),dbConfigs.getStatic_lon()
                    , distance);

            inCircle = distance[0] <= radiusForCircle;
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    /* Button Function */
    private void showVehicleList(){
        Intent intent = new Intent(this, AllVehicleDetails.class);
        Log.v("INFO>>","The Add vehicle activity started");
        startActivity(intent);
    }

    private void openHomePage() {
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