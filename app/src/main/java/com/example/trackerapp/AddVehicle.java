package com.example.trackerapp;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.trackerapp.Model.TrackingGeoSpatial_location;
import com.example.trackerapp.Model.TrackingGeoSpatial;
import java.util.Date;
import io.realm.Realm;
import io.realm.RealmList;


public class AddVehicle extends AppCompatActivity {
    EditText name;
    EditText reg_num;
    EditText city;
    Button btnSave;
    Button home;
    MyApplication dbConfigs;
    Realm backgroundThreadRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbConfigs = new MyApplication();
        backgroundThreadRealm = dbConfigs.getAppConfigs();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_vehicle);
        name = findViewById(R.id.name);
        reg_num = findViewById(R.id.reg_no);
        city = findViewById(R.id.city);
        btnSave = findViewById(R.id.btn_save);

        TrackingGeoSpatial tracking_data = new TrackingGeoSpatial();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String owner_name = name.getText().toString();
                String registration_num = reg_num.getText().toString();
                String city_of_reg = city.getText().toString();

                /* Geospatial object */
                TrackingGeoSpatial_location location = new TrackingGeoSpatial_location();
                RealmList<Double> latlonlist = new RealmList<>();
                latlonlist.add(dbConfigs.getStatic_lat());
                latlonlist.add(dbConfigs.getStatic_lon());
                location.setCoordinates(latlonlist);
                location.setType("Point");

                /* Tracking object */
                tracking_data.setPartition_key("security");
                tracking_data.setLocation(location);
                tracking_data.set_id(registration_num);
                tracking_data.setCity(city_of_reg);
                tracking_data.setOwner(owner_name);
                //tracking_data.setReg_num(registration_num);
                tracking_data.set_modifiedTS(new Date());
                showCustomDialog();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                backgroundThreadRealm.executeTransaction(transactionRealm -> {
                    transactionRealm.insert(tracking_data);
                    System.out.println("Instered successfully !!!!!!!!!!!!!!!!!!!!");
                });
                backgroundThreadRealm.close();
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

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_dialog, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.closeOptionsMenu();
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