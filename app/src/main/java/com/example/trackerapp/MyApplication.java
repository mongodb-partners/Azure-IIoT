package com.example.trackerapp;

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

public class MyApplication extends Application  {
    String appid = "application-0-gmonu";
    Double static_lat = 12.9716;
    Double static_lon = 77.5946;
    App app;
    String timeline_url = "https://us-west-2.aws.webhooks.mongodb-realm.com/api/client/v2.0/app/application-0-gmonu/service/GetTimeline/incoming_webhook/webhook0"+"?reg_num=";


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
        String partitionValue = "security";
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

    public Double getStatic_lat() {
        return static_lat;
    }

    public Double getStatic_lon() {
        return static_lon;
    }

    public String getTimeline_url() { return timeline_url; }


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