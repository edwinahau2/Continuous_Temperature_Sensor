package com.example.continuoustempsensor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.app.job.JobService;
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
import java.util.Calendar;
import java.util.Date;

public class NotificationReceiver extends BroadcastReceiver {

    public static String msg;

    @Override
    public void onReceive(Context context, Intent intent) {
        //sends toast when click button called open app under the notification
        String message = intent.getStringExtra("ButtonUnderneath");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        String action = intent.getExtras().getString("status");
        assert action != null;
        if (action.equals("cancelled")) { // TODO: test
            Toast.makeText(context, "Notification Removed", Toast.LENGTH_SHORT).show();
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            assert scheduler != null;
            scheduler.cancel(123);
            MainActivity.firstNotif = true;
        }
    }

    public static void sendNotification(Context context, int RequestCode){
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent broadcastIntent = new Intent(context, MainActivity.class);
        broadcastIntent.putExtra("ButtonUnderneath", "open app");
        PendingIntent actionIntent = PendingIntent.getBroadcast(context, RequestCode, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //update current means that when we create a new pendingintent, it will update putextra

        Bitmap redIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_red);
        Bitmap orangeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_orange);
        Bitmap greenIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_green);
        Date nowTime = Calendar.getInstance().getTime();
        String currentTime = String.valueOf(nowTime);
        if (RequestCode == 0) { //red
            msg = "High Fever Detected";
            Intent activityIntent = new Intent(context, MainActivity.class); // opens the app at home when notification clicked
            activityIntent.putExtra("message", "URGENT");
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RequestCode, activityIntent, 0);
            Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.warning)
                    //.setContentTitle(title)
                    //.setContentText(message)
                    .setLargeIcon(redIcon)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(context.getString(R.string.red_spike_message))
                            .setBigContentTitle(msg)
                            .setSummaryText("Fever Temperature"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true) //when tapped the notification will go away
                    .addAction(R.mipmap.ic_launcher, "Open App", actionIntent) // button at the moment sends toast, but want to send it to notify supervisor etc.
                    //can add up to 3 action buttons
                    //******* setDeleteIntent needs to be tested
                    .setDeleteIntent(getDeleteIntent(context))
                    .build();
            writeJSON(context, RequestCode, currentTime);
            notificationManager.notify(1, notification);
        } else if (RequestCode == 1) { //orange
            msg = "Fever Detected";
            Intent activityIntent = new Intent(context, MainActivity.class); // opens the app at home when notification clicked
            activityIntent.putExtra("message", "NOT URGENT");
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RequestCode, activityIntent, 0);
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.warning)
                    //.setContentTitle(title)
                    //.setContentText(message)
                    .setLargeIcon(orangeIcon)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(context.getString(R.string.orange_spike_message))
                            .setBigContentTitle(msg)
                            .setSummaryText("Increasing Temperature"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true) //when tapped the notification will go away
                    .addAction(R.mipmap.ic_launcher, "Open App", actionIntent) // button at the moment sends toast, but want to send it to notify supervisor etc.
                    //can add up to 3 action buttons
                    .build();
            writeJSON(context, RequestCode, currentTime);
            notificationManager.notify(2, notification);
        } else if (RequestCode == 2) { // yellow
            msg = "Your Temperatures Are Near a Fever";
            Intent activityIntent = new Intent(context, MainActivity.class); // opens the app at home when notification clicked
            activityIntent.putExtra("message", "NOT URGENT");
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RequestCode, activityIntent, 0);
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_2_ID)
                    .setSmallIcon(R.drawable.announcement)
//                    .setContentTitle(title)
//                    .setContentText(message)
                    .setLargeIcon(greenIcon)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine(msg) // can add up to 7 lines
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();
            writeJSON(context, RequestCode, currentTime);
            notificationManager.notify(3, notification);
        } else {
            msg = "Your Temperatures Are Normal";
            Intent activityIntent = new Intent(context, MainActivity.class); // opens the app at home when notification clicked
            activityIntent.putExtra("message", "NOT URGENT");
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(context, RequestCode, activityIntent, 0);
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_2_ID)
                    .setSmallIcon(R.drawable.announcement)
//                    .setContentTitle(title)
//                    .setContentText(message)
                    .setLargeIcon(greenIcon)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine(msg) // can add up to 7 lines
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();
            writeJSON(context, RequestCode, currentTime);
            notificationManager.notify(4, notification);
        }
    }

    protected static PendingIntent getDeleteIntent(Context context){
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("status", "cancelled");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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
            String idx;
            if (response.isEmpty()) {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject jText = new JSONObject();
                JSONObject jTime = new JSONObject();
                JSONObject jColor = new JSONObject();
                jText.put("notifText", msg);
                jTime.put("notifTime", currentTime);
                jColor.put("notifColor", RequestCode);
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
                JSONObject jsonObject = new JSONObject(response);
                JSONArray names = jsonObject.names();
                if (names != null) {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jText = new JSONObject();
                    JSONObject jTime = new JSONObject();
                    JSONObject jColor = new JSONObject();
                    jText.put("notifText", msg);
                    jTime.put("notifTime", currentTime);
                    jColor.put("notifColor", RequestCode);
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
                    JSONObject mainObj = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jText = new JSONObject();
                    JSONObject jTime = new JSONObject();
                    JSONObject jColor = new JSONObject();
                    jText.put("notifText", msg);
                    jTime.put("notifTime", currentTime);
                    jColor.put("notifColor", RequestCode);
                    idx = "Notif 1";
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
            }
            MainActivity.notif.setImageResource(R.drawable.bell2);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}