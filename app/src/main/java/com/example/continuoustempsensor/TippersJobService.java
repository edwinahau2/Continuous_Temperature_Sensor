package com.example.continuoustempsensor;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TippersJobService extends JobService {

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
            String url = "https://tippersweb.ics.uci.edu:8080/observation/temperature";
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
            HttpsURLConnection httpURLConnection = null;
            try {
                URL urlInstance = new URL(strings[0]);
                Log.d(TAG, strings[1]);

                //for SSL
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[] {new TrustAnyTrustManager()},
                        new java.security.SecureRandom());

                httpURLConnection = (HttpsURLConnection) urlInstance.openConnection();
                httpURLConnection.setSSLSocketFactory(sc.getSocketFactory());
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
                return "SUCCESS";
            } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
                return "ERROR";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, s);
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
