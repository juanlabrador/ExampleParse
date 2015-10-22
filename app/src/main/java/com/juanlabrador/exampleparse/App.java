package com.juanlabrador.exampleparse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

/**
 * Created by juanlabrador on 18/10/15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Player.class);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "TW60HRttJvRmPNfWEL6crcf8vsDxo9jJznCTdA1R", "NY8j0vk23AN9PnjbuNtShzFJz8ZtVDiVq0ofTLaS");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
