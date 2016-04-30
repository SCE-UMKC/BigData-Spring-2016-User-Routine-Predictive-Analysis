package com.example.rakesh.tracklive;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by Rakesh Reddy on 4/29/2016.
 */
public class BackgroundService extends IntentService {
    // Must create a default constructor
    public BackgroundService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("MyTestService", "Service running");
        Intent i = new Intent();
        i.setClass(this, StartActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}