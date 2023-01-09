package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import com.example.sensorapp.Model.Device;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SensorDetail extends AppCompatActivity {

    Button back;
//    Button details;
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
            System.out.println(value);
            backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    results = realm.where(Device.class).findAll();
                }

            });
            List<String> sensorData = results.get(value).returnList();
            String sensor_detail = "DEVICE ID : "+sensorData.get(0) +"\n\nDECSRIPTION : "+
                    sensorData.get(1) +"\n\nPARENT ID : "+sensorData.get(2) +"\n\nDEVICE ADDED ON : "
                    + sensorData.get(3)+ "\n\nSTATUS : "+ sensorData.get(4) + "\n\nPROTOCOL : "+sensorData.get(5);
            System.out.println(sensor_detail);
            device_details = findViewById(R.id.device_details);
            device_details.setText(sensor_detail);
            back = findViewById(R.id.landing_page);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    backgroundThreadRealm.close();
                    openLandingPage();
                }
            });

            String html = "<!DOCTYPE html><html><body><h1>Sensor Data</h1><iframe style=\"background: #FFFFFF;border: none;border-radius: 2px;box-shadow: 0 2px 10px 0 rgba(70, 76, 79, .2);\" width=\"640\" height=\"480\" src=\"https://charts.mongodb.com/charts-utsav_sagemaker_integrati-eqiqj/embed/charts?id=63add2c1-b222-441c-8a63-6652dfcad908&maxDataAge=3600&theme=light&autoRefresh=true\"></iframe></body></html>\n";
            WebView webview;
            webview = findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^#################@@@@@@@@@@@@@@@@@@@");
            System.out.println(html);
            webview.loadDataWithBaseURL("https://charts.mongodb.com/", html, "text/html", "UTF-8", null);



        }

    }

    private void openLandingPage() {
        Intent intent = new Intent(this, SensorData.class);
        Log.v("INFO>>","activity started");
        startActivity(intent);
    }
}