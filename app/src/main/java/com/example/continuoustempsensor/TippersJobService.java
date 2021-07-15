package com.example.continuoustempsensor;

import android.app.job.JobParameters;
import android.app.job.JobService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TippersJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            URL url = new URL("https://uci-tippers.ics.uci.edu/POST/observation/{obsTypeId}/many");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
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
            // [
                // {
            // ID = string/float
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
            OutputStream os = con.getOutputStream();
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
