package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity  {
    Button allVehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        allVehicles = findViewById(R.id.vehicle_details);
        allVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVehicleDetailsPage();
            }
        });
    }

    private void openVehicleDetailsPage() {
        Intent intent = new Intent(this, SensorData.class);
        Log.v("INFO","Started ");
        startActivity(intent);
    }


}