package com.example.continuoustempsensor.ui.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.core.app.RemoteInput;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.continuoustempsensor.MainActivity;
import com.example.continuoustempsensor.R;
import com.example.continuoustempsensor.notifications;

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
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class NotificationReceiver extends BroadcastReceiver {

    public final String TAG = "notifemail";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean supervisor = intent.getBooleanExtra("send", false);
        if (supervisor) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.cancel(123);
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            assert scheduler != null;
            scheduler.cancel(123);
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                CharSequence supsemail = remoteInput.getCharSequence("key_email_reply");
                assert supsemail != null;
                if (Patterns.EMAIL_ADDRESS.matcher(supsemail).matches()) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        Properties props = new Properties();
                        props.put("mail.smtp.auth", "true");
                        props.put("mail.smtp.host", "smtp.gmail.com");
                        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                        props.put("mail.smtp.socketFactory.port", "465");
                        props.put("mail.smtp.port", "465");
                        String username = "teggtemp@gmail.com";
                        String password = "xAJ;%DW!C4,qB=62";
                        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });
                        try {
                            MimeMessage message = new MimeMessage(session);
                            message.setFrom(new InternetAddress(username));
                            message.setRecipient(Message.RecipientType.TO, new InternetAddress(supsemail.toString()));
                            message.setSubject("[URGENT] ");
                            message.setText(""); //TODO: fill automatic email
                            Transport.send(message);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    });
                } else {
                    Log.d(TAG, "NOT A VALID ADDRESS");
                }
            }
        }
    }

    public static void sendNotification(Context context, String notifSignal) {
        String msg = null;
        int RequestCode = -1;
        if ("URGENT_NOTIFY".equals(notifSignal)) {
            msg = "High Fever Detected";
            RequestCode = 0;
        } else if ("NORMAL_NOTIFY".equals(notifSignal)) {
            float medianTemp = MainActivity.restoreTempVal(context);
            String unit = MainActivity.restoreTempUnit(context);
            if (unit.equals(" °F")) {
                if (medianTemp <= 99.9) {
                    RequestCode = 4; // green
                    msg = "Normal Temperature";
                } else if (medianTemp < 100.4 && medianTemp >= 100) {
                    RequestCode = 3; // yellow
                    msg = "Near a Fever";
                } else if (medianTemp >= 100.4 && medianTemp <= 102.9) {
                    RequestCode = 2; // orange
                    msg = "Fever Detected";
                } else {
                    RequestCode = 1; // red
                    msg = "High Fever Detected";
                }
            } else {
                if (medianTemp <= 37.7) {
                    RequestCode = 4; // green
                    msg = "Normal Temperature";
                } else if (medianTemp <= 38 && medianTemp >= 37.8) {
                    RequestCode = 3; // yellow
                    msg = "Near a Fever";
                } else if (medianTemp > 38 && medianTemp <= 39.4) {
                    RequestCode = 2; // orange
                    msg = "Fever Detected";
                } else {
                    RequestCode = 1; // red
                    msg = "High Fever Detected";
                }
            }
        }
        Date nowTime = Calendar.getInstance().getTime();
        String currentTime = String.valueOf(nowTime);
        writeJSON(context, currentTime, msg);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Intent activityIntent = new Intent(context, MainActivity.class); // opens the app at home when notification clicked

        if (RequestCode == 0) { //red
            Bitmap redIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.temp_red);
            activityIntent.putExtra("status", true);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
            RemoteInput remoteInput = new RemoteInput.Builder("key_email_reply")
                    .setLabel("Supervisor's Email")
                    .build();
            Intent supervisorIntent = new Intent(context, NotificationReceiver.class);
            supervisorIntent.putExtra("send", true);
            PendingIntent emailIntent = PendingIntent.getBroadcast(context, 1, supervisorIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                    R.mipmap.ic_launcher, "Notify Supervisor", emailIntent)
                    .addRemoteInput(remoteInput)
                    .build();
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_1_ID)
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
                    .addAction(action)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(123, notification);
        } else {
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
            PendingIntent pendingIntent = PendingIntent.getActivity(context, RequestCode, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            android.app.Notification notification = new NotificationCompat.Builder(context, notifications.CHANNEL_2_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(msg)
                    .setContentText(context.getString(contentText))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(context.getString(contentText))
                            .setSummaryText("Your Temperature Update"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setColor(Color.argb(255, 48, 154, 230))
                    .setLargeIcon(icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(456, notification);
        }
    }

    private static void writeJSON(Context context, String currentTime, String msg) {
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
}
