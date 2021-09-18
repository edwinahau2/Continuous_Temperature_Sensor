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

public class urgentNotifJob extends JobService {

    private static final String TAG = "urgentNotif";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        createNotificationChannel();
        String msg = "High Fever Detected";
        sendNotification(getApplicationContext(), msg);
        Date nowTime = Calendar.getInstance().getTime();
        String currentTime = String.valueOf(nowTime);
        writeJSON(getApplicationContext(), currentTime, msg);
        jobFinished(params, true);
        return true;
    }

    private void sendNotification(Context context, String msg) {
        Bitmap redIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_red);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("status", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Intent supervisorIntent = new Intent(context, UrgentNotificationReceiver.class);
        supervisorIntent.putExtra("send", true);
        PendingIntent emailIntent = PendingIntent.getBroadcast(context, 1, supervisorIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "rugent0")
                .setSmallIcon(R.drawable.warning)
                .setContentTitle(msg)
                .setContentText(context.getString(R.string.red_spike_message))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.red_spike_message))
                        .setSummaryText("Fever Temperature"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.argb(255, 48, 154, 230))
                .setLargeIcon(redIcon)
                .addAction(R.mipmap.ic_launcher, "Notify Supervisor", emailIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(123, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "urgentChannel";
            String description = "Urgent Notification Channel for High Temperature Spikes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("rugent0", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
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
