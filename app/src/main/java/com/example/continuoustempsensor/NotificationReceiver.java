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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationReceiver extends BroadcastReceiver {

    public static JSONObject mainObj = new JSONObject();

    @Override
    public void onReceive(Context context, Intent intent) {

        //sends toast when click button called open app under the notification
        String message = intent.getStringExtra("ButtonUnderneath");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void sendNotification(Context context, int RequestCode){
        NotificationManagerCompat notificationManager;
        notificationManager = NotificationManagerCompat.from(context);

        Intent activityIntent = new Intent(context, MainActivity.class); // opens the app at home when notification clicked
        //activityIntent.putExtra("message", "message");
        PendingIntent contentIntent = PendingIntent.getActivity(context, RequestCode, activityIntent, 0);


        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("ButtonUnderneath", "open app");
        PendingIntent actionIntent = PendingIntent.getBroadcast(context, RequestCode, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //update current means that when we create a new pendingintent, it will update putextra

        Bitmap redIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_red);
        Bitmap orangeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_orange);
        Bitmap greenIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_green);
        Date nowTime = Calendar.getInstance().getTime();
        String currentTime = String.valueOf(nowTime);
        if (RequestCode == 0) {//red
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.warning)
                    //.setContentTitle(title)
                    //.setContentText(message)
                    .setLargeIcon(redIcon)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(context.getString(R.string.red_spike_message))
                            .setBigContentTitle("Fever Temperatures Detected")
                            .setSummaryText("Fever Temperature"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true) //when tapped the notification will go away
                    //.setOnlyAlertOnce(true) will only make sound and popup the first time we show it
                    .addAction(R.mipmap.ic_launcher, "Open App", actionIntent) // button at the moment sends toast, but want to send it to notify supervisor etc.
                    //can add up to 3 action buttons
                    .build();
            writeJSON(context, RequestCode, currentTime); // ADD STRING AND TIME
            notificationManager.notify(1, notification);
        }
        if (RequestCode == 1) {//orange
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.warning)
                    //.setContentTitle(title)
                    //.setContentText(message)
                    .setLargeIcon(orangeIcon)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(context.getString(R.string.orange_spike_message))
                            .setBigContentTitle("Fever Temperatures Detected")
                            .setSummaryText("Increasing Temperature"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true) //when tapped the notification will go away
                    //.setOnlyAlertOnce(true) will only make sound and popup the first time we show it
                    .addAction(R.mipmap.ic_launcher, "Open App", actionIntent) // button at the moment sends toast, but want to send it to notify supervisor etc.
                    //can add up to 3 action buttons
                    .build();
            writeJSON(context, RequestCode, currentTime);
            notificationManager.notify(2, notification);
        }
        if (RequestCode == 2) {//green
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_2_ID)
                    .setSmallIcon(R.drawable.announcement)
//                    .setContentTitle(title)
//                    .setContentText(message)
                    .setLargeIcon(greenIcon)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine("Your temperatures are normal") // can add up to 7 lines
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();
            writeJSON(context, RequestCode, currentTime);
            notificationManager.notify(3, notification);
        }
    }

    private static void writeJSON(Context context, int RequestCode, String currentTime) {
        File file;
        FileReader fileReader;
        BufferedReader bufferedReader;
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        String FILE_NAME = "notif.json";
        file = new File(context.getFilesDir(), FILE_NAME);
        try {
            String idx;
            if (mainObj.length() != 0) {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                String response = stringBuilder.toString();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = new JSONArray();
                JSONObject jText = new JSONObject();
                JSONObject jTime = new JSONObject();
                JSONObject jColor = new JSONObject();
                jText.put("notifText", "Testing 4th Text"); //change
                jTime.put("notifTime", currentTime);
                jColor.put("notifColor", RequestCode); //change
                idx = MainActivity.restoreIdx(context);
                jsonArray.put(jText);
                jsonArray.put(jTime);
                jsonArray.put(jColor);
                jsonObject.put(idx, jsonArray);
                String jsonStr = jsonObject.toString();
                fileWriter = new FileWriter(file, false);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(jsonStr);
                bufferedWriter.close();
            } else {
                JSONArray jsonArray = new JSONArray();
                JSONObject jText = new JSONObject();
                JSONObject jTime = new JSONObject();
                JSONObject jColor = new JSONObject();
                jText.put("notifText", "Testing 1st Text"); //change
                jTime.put("notifTime", "Testing 1st Time"); //change
                jColor.put("notifColor", RequestCode); //change
                idx = "Notif 1"; // save this value
                MainActivity.saveIdx(1, context);
                jsonArray.put(jText);
                jsonArray.put(jTime);
                jsonArray.put(jColor);
                mainObj.put(idx, jsonArray);
                String jsonStr = mainObj.toString();
                fileWriter = new FileWriter(file, true);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(jsonStr);
                bufferedWriter.close();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}