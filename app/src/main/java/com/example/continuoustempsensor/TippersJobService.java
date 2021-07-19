package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TippersJobService extends JobService implements LocationListener{

    protected LocationManager locationManager;

    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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
            String url = "http://tippersweb.ics.uci.edu:8080/POST/observation/";
            new PushToServer().execute(url, json);
            // [
                // {
            // ID = string
            // time = string
            // temp {
                // val = float
                // unit = string
            // }
        //},
            // { temp {
                // time = string
                //val = float
                // unit = string
            // }
        // }, ... ]
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
                httpURLConnection = (HttpURLConnection) new URL(strings[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes("PostData=" + strings[1]);
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (IOException e) {
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

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
