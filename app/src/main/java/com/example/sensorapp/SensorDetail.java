package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import com.example.sensorapp.Model.Device;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SensorDetail extends AppCompatActivity {

    Button back;
    Button details;
    Realm backgroundThreadRealm;
    String Appid;
    private RealmResults<Device> results;
    public Realm realm;
    TextView device_details;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppConfigs dbConfigs = new AppConfigs();
        Appid = dbConfigs.getAppid();
        backgroundThreadRealm = dbConfigs.getAppConfigs();


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sensor_detail);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Integer value = extras.getInt("key");
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^");
            System.out.println(value);
            backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    results = realm.where(Device.class).findAll();
                }

            });
            List<String> sensorData = results.get(value).returnList();
            String sensor_detail = "Device id : "+sensorData.get(0) +"\n\ndescription : "+ sensorData.get(1) +"\n\nparent_id : "+sensorData.get(2);
            System.out.println(sensor_detail);
            device_details = findViewById(R.id.device_details);
            device_details.setText(sensor_detail);

        }

        back = findViewById(R.id.landing_page);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundThreadRealm.close();
                openLandingPage();
            }
        });

        details = findViewById(R.id.get_graph);

    }

    private void openLandingPage() {
        Intent intent = new Intent(this, SensorData.class);
        Log.v("INFO>>","activity started");
        startActivity(intent);
    }
}