package com.example.sensorapp;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.SyncConfiguration;
import io.realm.mongodb.sync.SyncSession;

public class AppConfigs extends Application  {
    String appid = "iiot-azure-hwsza";
    App app;


    public Realm getAppConfigs() {
        Realm.init(this);
        SyncSession.ClientResetHandler handler = new SyncSession.ClientResetHandler() {
            @Override
            public void onClientReset(SyncSession session, ClientResetRequiredError error) {
                Log.e("EXAMPLE", "Client Reset required for: " +
                        session.getConfiguration().getServerUrl() + " for error: " +
                        error.toString());
            }
        };
        App app = new App(new AppConfiguration.Builder(appid)
                .defaultClientResetHandler(handler)
                .build());
        User user = app.currentUser();
        app.login(Credentials.anonymous());
        String partitionValue = "sensor";
        assert user != null;
        SyncConfiguration config = new SyncConfiguration.Builder(user, partitionValue)
                .allowWritesOnUiThread(true)
                .allowQueriesOnUiThread(true)
                .build();



        Realm backgroundThreadRealm = Realm.getInstance(config);
        return backgroundThreadRealm;
    }

    public String getAppid() {
        return appid;
    }


    public App getApp(){
        app = new App(new AppConfiguration.Builder(appid).build());

        app.loginAsync(Credentials.anonymous(), new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {
                    Log.v("User", "Logged In Successfully");
                } else {
                    Log.v("User", "Failed to Login");
                }
            }
        });
        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

}