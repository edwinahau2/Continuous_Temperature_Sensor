package com.example.continuoustempsensor;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

public class TestJobService extends JobService {

    private static final String TAG = "ExampleJobService";
    private MimicAsyncTask mimicAsyncTask;
    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.jobParameters = params;
        Toast.makeText(this, "Job Started", Toast.LENGTH_SHORT).show();
        PersistableBundle bundle = params.getExtras();
        String address = bundle.getString("address");
//        Boolean spark = bundle.getBoolean("spark");
//        MyTaskParams params = new MyTaskParams(address, spark);
        mimicAsyncTask = new MimicAsyncTask();
        mimicAsyncTask.execute(address);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job Cancelled");
            if (mimicAsyncTask != null) {
                if (!mimicAsyncTask.isCancelled()) {
                    mimicAsyncTask.cancel(true);
                }
            }
        return true;
    }

    private class MimicAsyncTask extends AsyncTask<String, String, String> {

//        @Override
//        protected String doInBackground(S... integers) {
//            for (int i = 0; i <integers[0]; i++) {
//                SystemClock.sleep(1000);
//                publishProgress(i);
//            }
//            return "Job Finished";
//        }

//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            Log.d(TAG, "onProgressUpdate: i was: " + values[0]);
//        }

        @Override
        protected String doInBackground(String... strings) {
            publishProgress(strings);
            return "Job Finished";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "address: " + Arrays.toString(values));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: message: " + s);
            jobFinished(jobParameters, true);
        }
    }
}
