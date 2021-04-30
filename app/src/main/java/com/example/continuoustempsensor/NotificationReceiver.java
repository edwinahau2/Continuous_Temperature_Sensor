package com.example.continuoustempsensor;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //sends toast when click button called open app under the notification
        String message = intent.getStringExtra("ButtonUnderneath");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void sendNotification(Context context, int RequestCode){
        NotificationManagerCompat notificationManager;
        notificationManager = NotificationManagerCompat.from(context);

        Intent activityIntent = new Intent(context, MainActivity.class); // opens the app at fragment 2 when notification clicked
        PendingIntent contentIntent = PendingIntent.getActivity(context, RequestCode, activityIntent, 0);

        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("ButtonUnderneath", "open app");
        PendingIntent actionIntent = PendingIntent.getBroadcast(context, RequestCode, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //update current means that when we create a new pendingintent, it will update putextra

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_spike);

        if (RequestCode == 0) {
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.warning)
                    //.setContentTitle(title)
                    //.setContentText(message)
                    .setLargeIcon(largeIcon)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(context.getString(R.string.temp_spike_message))
                            .setBigContentTitle("Fever Temperatures Detected")
                            .setSummaryText("Summary Text"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true) //when tapped the notification will go away
                    //.setOnlyAlertOnce(true) will only make sound and popup the first time we show it
                    .addAction(R.mipmap.ic_launcher, "Notify Others", actionIntent) // button at the moment sends toast, but want to send it to notify supervisor etc.
                    //can add up to 3 action buttons
                    .build();
            notificationManager.notify(1, notification);
        }
        if (RequestCode == 1) {
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_2_ID)
                    .setSmallIcon(R.drawable.announcement)
//                    .setContentTitle(title)
//                    .setContentText(message)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine("Notification message 1") // can add up to 7 lines
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(2, notification);
        }
    }
}