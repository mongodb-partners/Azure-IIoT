package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;


import com.example.sensorapp.Model.Device;
import com.example.sensorapp.Model.Failure;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class SensorData extends AppCompatActivity implements AdapterView.OnItemClickListener {
    String Appid;
    public Realm realm;
    Button home;
    Realm backgroundThreadRealm;
    private RealmResults<Device> results;
    private RealmResults<Failure> failures;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppConfigs dbConfigs = new AppConfigs();
        Appid = dbConfigs.getAppid();
        backgroundThreadRealm = dbConfigs.getAppConfigs();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_data);

        try{
            getDataFromSync();
        } catch (Exception e)
        {
            System.out.println("EXCEPTION:"+ e);
        }

    }

    public void getDataFromSync() {
        backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                results = realm.where(Device.class).findAll();

                System.out.println(results);
                List<String> devices= new ArrayList<>();
                for (int i = 0; i < results.size(); i++) {
                    assert results.get(i) != null;
                    devices.add(results.get(i).toString());
                }
                renderListActivity(devices);
            }

        });

        backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                failures = realm.where(Failure.class).findAll();
            }

        });

        results.addChangeListener(new RealmChangeListener<RealmResults<Device>>() {
            @Override
            public void onChange(RealmResults<Device> trackingGeoSpatials) {
                List<String> sensors= new ArrayList<>();
                for (int i = 0; i < results.size(); i++) {
                    sensors.add(results.get(i).toString());
                }
                renderListActivity(sensors);
            }
        });




        failures.addChangeListener(new RealmChangeListener<RealmResults<Failure>>() {
            @Override
            public void onChange(RealmResults<Failure> failure_data) {
                // Test: Dialog for failure alert
                AlertDialog.Builder builder = new AlertDialog.Builder(SensorData.this);
                builder.setMessage(""+ "Failure :" + String.valueOf(failure_data.last().getFailure())+ "\nDevice ID: "+ String.valueOf(failure_data.last().getDeviceId())+"\nOccurred On: "+ String.valueOf(failure_data.last().getTs()));
                builder.setCancelable(false);
                builder.setNegativeButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public void renderListActivity(List<String> sensorData) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorData);
        ListView listView = findViewById(R.id.sensorList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        List<String> sensorData = results.get(pos).returnList();
        System.out.println(sensorData);
        String sensor_detail = "Device id : "+sensorData.get(0) +"\n\ndescription : "+ sensorData.get(1) +"\n\nparent_id : "+sensorData.get(2);

//        AlertDialog.Builder builder = new AlertDialog.Builder(SensorData.this);
//        builder.setMessage(""+ sensor_detail);
//        builder.setCancelable(false);
//
//        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
//            Intent intent = new Intent(this, SensorDetail.class);
//            intent.putExtra("key", pos);
//            Log.v("INFO>>","activity started");
//            startActivity(intent);
//        });
//
//        builder.setNegativeButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
//            dialog.cancel();
//        });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
        Intent intent = new Intent(this, SensorDetail.class);
        intent.putExtra("key", pos);
        Log.v("INFO>>","activity started");
        startActivity(intent);
        backgroundThreadRealm.close();
    }


    @Override
    public void onBackPressed() {
        backgroundThreadRealm.close();
        Intent intent = new Intent(this, MainActivity.class);
        Log.v("INFO>>","activity started");
        startActivity(intent);
    }
}
