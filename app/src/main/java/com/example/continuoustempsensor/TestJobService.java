package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import kotlinx.coroutines.Job;


public class TestJobService extends JobService {

    private static final String TAG = "ExampleJobService";
    String address;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        PersistableBundle bundle = params.getExtras();
        address = bundle.getString("address");
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        ComponentName componentName = new ComponentName(this, TestJobService.class);
//        if (jobScheduler != null) {
//            jobScheduler.cancel(101);
//        }
//            PersistableBundle bun = new PersistableBundle();
//            bun.putString("address", address);
//            JobInfo jobInfo = new JobInfo.Builder(101, componentName)
//                    .setExtras(bun)
//                    .setPersisted(false)
//                    .setPeriodic(15*60*1000)
//                    .build();
//            if (jobScheduler.schedule(jobInfo)==JobScheduler.RESULT_SUCCESS) {
//                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();
//            }
        new MyWorker(address, this, params).execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job Cancelled");
        return true;
    }

    private static class MyWorker extends AsyncTask<String, Void, Boolean> {

        private final String MAC;
        @SuppressLint("StaticFieldLeak")
        private final TestJobService tjs;
        private final JobParameters jp;

        public MyWorker(String addy, TestJobService testJobService, JobParameters jobParameters) {
            MAC = addy;
            tjs = testJobService;
            jp = jobParameters;
        }

        @Override
        protected Boolean doInBackground(String...voids) {
            Log.d(TAG, "address: " + MAC);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            tjs.jobFinished(jp, aBoolean);
            Log.d(TAG, "-----------------------");
        }
    }

//    private class MimicAsyncTask extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            long futureTime = Calendar.getInstance().getTimeInMillis() + 10000;
//            Log.d(TAG, "future: " + futureTime);
//            while (true) {
//                if (Calendar.getInstance().getTimeInMillis() >= futureTime) {
//                    publishProgress(String.valueOf(Calendar.getInstance().getTimeInMillis()));
//                    break;
//                }
//            }
//            return "Job Finished";
//        }
//
//        @Override
//        protected void onProgressUpdate(String... values) {
//            super.onProgressUpdate(values);
//            Log.d(TAG, "current: " + Arrays.toString(values));
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
////            Log.d(TAG, "onPostExecute: message: " + s);
//        }
//    }

}
