package com.example.trackerapp;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class DialogActivity extends AppCompatActivity {

    private String value;
    public String registration_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            registration_number = extras.getString("key");
            Log.v("Intent Value", value);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        TextView textView = findViewById(R.id.display_msg);
        textView.setText(value);
    }
}