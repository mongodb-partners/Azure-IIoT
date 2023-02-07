package com.example.sensorapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sensorapp.Model.Device;
import com.example.sensorapp.Model.Failure;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmAny;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class AlertsView extends AppCompatActivity implements AlertsRCView.ItemClickListener {

    AlertsRCView new_adapter;
    AlertsRCView ack_adapter;
    Button back_btn;
    String Appid;
    public Realm realm;
    Realm backgroundThreadRealm;
    ImageView image;
    TextView text;

    String CHANNEL_ID = "alert_channel";
    private RealmResults<Failure> new_failures;
    private RealmResults<Failure> ack_failures;
    // data to populate the RecyclerView with
    ArrayList<String> ack_failure_type = new ArrayList<>();
    ArrayList<String> ack_deviceNames = new ArrayList<>();
    ArrayList<String> ack_dates = new ArrayList<>();

    // data to populate the RecyclerView with
    ArrayList<String> new_failure_type = new ArrayList<>();
    ArrayList<String> new_deviceNames = new ArrayList<>();
    ArrayList<String> new_dates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alerts_view);

        AppConfigs dbConfigs = new AppConfigs();
        Appid = dbConfigs.getAppid();
        backgroundThreadRealm = dbConfigs.getAppConfigs();

        try{
            getFailureDataFromSync();
        } catch (Exception e)
        {
            System.out.println("EXCEPTION:"+ e);
        }

        image = findViewById(R.id.all_done_img);
        text = findViewById(R.id.all_done_text);

        // set up the New Alerts RecyclerView
        RecyclerView new_rv = findViewById(R.id.new_recycler);
        new_rv.setLayoutManager(new LinearLayoutManager(this));
        new_adapter = new AlertsRCView(this, new_failure_type, new_deviceNames, new_dates, Boolean.FALSE);
        new_adapter.setClickListener(this);
        new_rv.setAdapter(new_adapter);
        change_visibility_all_done();

        new_failures.addChangeListener(new RealmChangeListener<RealmResults<Failure>>() {
            @Override
            public void onChange(RealmResults<Failure> new_failure_data) {
                Failure last_failure_data = new_failure_data.last();
                new_failure_type.add(last_failure_data.getFailure());
                new_deviceNames.add(last_failure_data.getDeviceId());
                new_dates.add(String.valueOf(last_failure_data.getTs()));
                new_adapter.notifyDataSetChanged();
                change_visibility_all_done();
            }
        });

        // set up the Acknowledge Alerts RecyclerView
        RecyclerView acknowledge_rv = findViewById(R.id.ack_recycler);
        acknowledge_rv.setLayoutManager(new LinearLayoutManager(this));
        ack_adapter = new AlertsRCView(this, ack_failure_type, ack_deviceNames, ack_dates, Boolean.TRUE);
//        ack_adapter.setClickListener(this);
        acknowledge_rv.setAdapter(ack_adapter);

        ack_failures.addChangeListener(new RealmChangeListener<RealmResults<Failure>>() {
            @Override
            public void onChange(RealmResults<Failure> ack_failure_data) {
                Failure last_failure_data = ack_failure_data.last();
                ack_failure_type.add(last_failure_data.getFailure());
                ack_deviceNames.add(last_failure_data.getDeviceId());
                ack_dates.add(String.valueOf(last_failure_data.getTs()));
                ack_adapter.notifyDataSetChanged();
            }
        });

        back_btn = findViewById(R.id.back_bt);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Are you notified??");
                onBackPressed();
            }
        });
    }

    private void getFailureDataFromSync() {
        backgroundThreadRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                System.out.println(Calendar.getInstance().getTime());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date yesterday = cal.getTime();
                cal.add(Calendar.DATE, +2);
                Date tomorrow = cal.getTime();

                ack_failures = realm.where(Failure.class).isNotNull("ack").between("ts", yesterday, tomorrow).findAll().sort("ts", Sort.ASCENDING);
                new_failures = realm.where(Failure.class).isNull("ack").between("ts", yesterday, tomorrow).findAll().sort("ts", Sort.ASCENDING);

                // Adding to arraylist for displaying in new UI
                for (Failure f: ack_failures) {
                    ack_failure_type.add(f.getFailure());
                    ack_deviceNames.add(f.getDeviceId());
                    ack_dates.add(String.valueOf(f.getTs()));
                }
                // Adding to arraylist for displaying in alert UI
                for (Failure f: new_failures) {
                    new_failure_type.add(f.getFailure());
                    new_deviceNames.add(f.getDeviceId());
                    new_dates.add(String.valueOf(f.getTs()));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        backgroundThreadRealm.close();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onItemClick(View view, int position) {
        backgroundThreadRealm.close();
        Intent intent = new Intent(this, SensorDetail.class);
        intent.putExtra("key", position);
        startActivity(intent);
    }

    public void change_visibility_all_done() {
        if (new_adapter.getItemCount() == 0) {
            image.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.INVISIBLE);
            text.setVisibility(View.INVISIBLE);
        }
    }

}

