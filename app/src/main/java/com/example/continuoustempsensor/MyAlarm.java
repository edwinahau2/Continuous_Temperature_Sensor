package com.example.continuoustempsensor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyAlarm extends BroadcastReceiver {

    String TAG = "ExampleJobService";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "alarm is ringing");
    }
}
