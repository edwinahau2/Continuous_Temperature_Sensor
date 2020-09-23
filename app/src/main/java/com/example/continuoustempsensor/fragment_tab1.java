package com.example.continuoustempsensor;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

public class fragment_tab1 extends Fragment {
    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_layout, container, false);

        final Context that = this.getContext(); // used "that" because in buttons can't reference parent context from innerclass line 37 and 54 instead of using "this"
        notificationManager = NotificationManagerCompat.from(this.getContext());
        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextMessage = view.findViewById(R.id.edit_text_message);

        Button warningButton = view.findViewById(R.id.urgent_warning);
        warningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                String title = editTextTitle.getText().toString();
                String message = editTextMessage.getText().toString();
                assert that != null;

                Intent activityIntent = new Intent(that, MainActivity.class); // opens the app at fragment 1 when notification clicked
                PendingIntent contentIntent = PendingIntent.getActivity(that,
                        0, activityIntent, 0);

                Intent broadcastIntent = new Intent(that, NotificationReceiver.class);
                broadcastIntent.putExtra("ButtonUnderneath", message);
                PendingIntent actionIntent = PendingIntent.getBroadcast(that,
                        0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                //update current means that when we create a new pendingintent, it will update putextra

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.temp_spike);

                android.app.Notification notification = new NotificationCompat.Builder(that, notifications.CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.warning)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setLargeIcon(largeIcon)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getString(R.string.temp_spike_message))
                                .setBigContentTitle("Fever Temperatures Detected")
                                .setSummaryText("Summary Text"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setColor(Color.BLUE)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true) //when tapped the notification will go away
                        //.setOnlyAlertOnce(true) will only make sound and popup the first time we show it
                        .addAction(R.mipmap.ic_launcher, "Notify Others", actionIntent) // button at the moment sends toast, but want to send it to notify supervisor etc.
                        //can add up to 3 action buttons
                        .build();
                notificationManager.notify(1, notification);
            }
        });

        Button updateButton = view.findViewById(R.id.send_update);
        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                String title = editTextTitle.getText().toString();
                String message = editTextMessage.getText().toString();
                assert that != null;

                Intent activityIntent = new Intent(that, MainActivity.class); // opens the app at fragment 1 when notification clicked
                PendingIntent contentIntent = PendingIntent.getActivity(that,
                        0, activityIntent, 0);

                android.app.Notification notification = new NotificationCompat.Builder(that, notifications.CHANNEL_2_ID)
                        .setSmallIcon(R.drawable.announcement)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Notification message 1") // can add up to 7 lines
                                .addLine("Notification message 2")
                                .addLine("Notification message 3")
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_EVENT)
                        .setColor(Color.BLUE)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(2, notification);
            }
        });

        return view;
    }
}