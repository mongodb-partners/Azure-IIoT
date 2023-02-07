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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensorapp.Model.Failure;

import java.text.SimpleDateFormat;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SensorDetail extends AppCompatActivity {

    Button ack_bt;
    Button back_bt;
    Realm backgroundThreadRealm;
    String Appid;
    private RealmResults<Failure> new_failure;
    public Realm realm;
    TextView failure_details;
    TextView timestamp;
    TextView device_name;
    EditText ack_text;

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
                    new_failure = realm.where(Failure.class).isNull("ack").findAll();
                }
            });
            List<String> sensorData = new_failure.get(value).returnList();

            failure_details = findViewById(R.id.failure_type);
            failure_details.setText(sensorData.get(1));

            device_name = findViewById(R.id.device_name);
            device_name.setText(sensorData.get(0));

            timestamp = findViewById(R.id.timestamp);
            timestamp.setText(sensorData.get(2));

            ack_bt = findViewById(R.id.ack_bt);
            ack_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addAcknowledgement(new_failure.get(value));
                }
            });

            back_bt = findViewById(R.id.back_bt);
            back_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            String html = "<!DOCTYPE html><html><body><iframe style=\"background: #FFFFFF;border: none;\" width=\"390\" height=\"380\" src=\"https://charts.mongodb.com/charts-utsav_sagemaker_integrati-eqiqj/embed/charts?id=63add227-6009-463b-80c2-228b15617a40&maxDataAge=3600&theme=light&autoRefresh=true\"></iframe></body></html>\n";
            WebView webview;
            webview = findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadDataWithBaseURL("https://charts.mongodb.com/", html, "text/html", "UTF-8", null);
        }

    }

    @Override
    public void onBackPressed() {
        backgroundThreadRealm.close();
        Intent intent = new Intent(this, AlertsView.class);
        Log.v("INFO","Started ");
        startActivity(intent);
        super.onBackPressed();
    }

    private void addAcknowledgement(Failure failure) {
        ack_text = findViewById(R.id.ack_text);
        backgroundThreadRealm.executeTransaction(r -> {
            // Update properties on the instance.
            // This change is saved to the realm.
            failure.setAck(String.valueOf(ack_text.getText()));
            Toast.makeText(this, failure.getFailure() + " moved to acknowledged!", Toast.LENGTH_SHORT).show();
        });
        backgroundThreadRealm.close();
        onBackPressed();
    }
}