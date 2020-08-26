package com.example.continuoustempsensor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Notifications extends AppCompatActivity {

    //Button notify_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

//        notify_button = findViewById(R.id.push);
//        notify_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //code that appears in the notification
//                NotificationCompat.Builder mbuilder = (NotificationCompat.Builder)
//                        new NotificationCompat.Builder(getApplicationContext())
//                        .setSmallIcon(R.drawable.warning)
//                        .setContentTitle("Notification")
//                        .setContentText("you have been warned lol");
//
//                NotificationManager notificationManager = (NotificationManager)
//                        getSystemService(NOTIFICATION_SERVICE);
//                notificationManager.notify(0, mbuilder.build());
//            }
//        });

    }
}