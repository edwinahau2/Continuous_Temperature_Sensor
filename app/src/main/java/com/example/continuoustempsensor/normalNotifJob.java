package com.example.continuoustempsensor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

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

public class normalNotifJob extends JobService {

    private static final String TAG = "normalNotif";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        float medianTemp = MainActivity.restoreTempVal(this);
        int RequestCode;
        String msg;
        if (medianTemp <= 99.9 || medianTemp <= 37.7) {
            RequestCode = 3; // green
            msg = "Normal Temperature";
        } else if ((medianTemp <= 100.4 && medianTemp >= 100) || (medianTemp <= 38 && medianTemp >= 37.8)) {
            RequestCode = 2; // yellow
            msg = "Near a Fever";
        } else if ((medianTemp > 100.4 && medianTemp <= 102.9) || (medianTemp > 38 && medianTemp <= 39.4)) {
            RequestCode = 1; // orange
            msg = "Fever Detected";
        } else {
            RequestCode = 0; // red
            msg = "High Fever Detected";
        }
        createNotificationChannel();
        sendNotification(getApplicationContext(), msg, RequestCode);
        Date nowTime = Calendar.getInstance().getTime();
        String currentTime = String.valueOf(nowTime);
        writeJSON(getApplicationContext(), currentTime, msg);
        jobFinished(params, false);
        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "normalChannel";
            String description = "Normal Notification Channel for Temperature Updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("rolanm0", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(Context context, String msg, int RequestCode) {
        Bitmap icon = null;
        int contentText;
        if (RequestCode == 3) {
            icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_green);
            contentText = R.string.green_spike_message;
        } else if (RequestCode == 2) {
            // TODO: create yellow icon
            contentText = R.string.yellow_spike_message;
        } else if (RequestCode == 1) {
            icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_orange);
            contentText = R.string.orange_spike_message;
        } else {
            icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_red);
            contentText = R.string.red_spike_message;
        }
        Intent intent = new Intent(context, MainActivity.class);
        int code = RequestCode + 2;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "rolanm0")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(msg)
                .setContentText(context.getString(contentText))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(contentText))
                        .setSummaryText(msg))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setColor(Color.argb(255, 48, 154, 230))
                .setLargeIcon(icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(456, builder.build());
    }

    private void writeJSON(Context context, String currentTime, String msg) {
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
                jColor.put("notifColor", 0);
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
                    jColor.put("notifColor", 0);
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
                    jColor.put("notifColor", 0);
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

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        return true;
    }
}
