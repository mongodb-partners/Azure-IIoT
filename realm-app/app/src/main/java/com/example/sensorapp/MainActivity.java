package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity  {
    ImageButton allVehicles;

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

        TextView tvDisplayDate = (TextView) findViewById(R.id.curr_date);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String dateString = sdf.format(date);
        tvDisplayDate.setText(dateString);

        String html = "<!DOCTYPE html><html><body><iframe style=\"background: #FFFFFF;border: none;border-radius: 2px;\" width=\"380\" height=\"300\" src=\"https://charts.mongodb.com/charts-utsav_sagemaker_integrati-eqiqj/embed/charts?id=63d7c09d-d62c-4230-8968-46fabe27cd77&maxDataAge=3600&theme=light&autoRefresh=true\"></iframe></body></html>\n";
        WebView webview;
        webview = findViewById(R.id.failure_pie);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadDataWithBaseURL("https://charts.mongodb.com/", html, "text/html", "UTF-8", null);

    }

    private void openVehicleDetailsPage() {
        Intent intent = new Intent(this, AlertsView.class);
        Log.v("INFO","Started ");
        startActivity(intent);
    }


}