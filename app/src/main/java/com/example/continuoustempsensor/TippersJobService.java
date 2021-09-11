package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class TippersJobService extends JobService {

    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            File file;
            FileReader fileReader;
            BufferedReader bufferedReader;
            String FILE_NAME = "tippers.json";
            file = new File(this.getFilesDir(), FILE_NAME);
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            String json = stringBuilder.toString();
            String url = "http://tippersweb.ics.uci.edu:8080/observation/temperature";
            new PushToServer().execute(url, json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        jobFinished(params, false);
        return true;
    }

    private static class PushToServer extends AsyncTask<String, Void, String> {

        private static final String TAG = "Tippers Testing";

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL urlInstance = new URL(strings[0]);
                Log.d(TAG, strings[1]);
                httpURLConnection = (HttpURLConnection) urlInstance.openConnection();
                httpURLConnection.setConnectTimeout(60*1000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                OutputStream os = httpURLConnection.getOutputStream();
                byte[] input = strings[1].getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());

            } catch (IOException e) {
                Log.d(TAG, "ERROR");
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return "SUCCESS";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, s);
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
