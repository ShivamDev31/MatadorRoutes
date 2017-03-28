package com.roots;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class AppInit extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("468371f6-5861-40f5-b6f4-66b40db4b00e")
                .clientKey(null)
                .server("https://api.parse.buddy.com/parse/")
                .build());
        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveEventually();

    }
}
