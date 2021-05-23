package com.example.continuoustempsensor;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class urgentNotifJob extends JobService {

    private static final String TAG = "urgentNotif";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        NotificationReceiver.sendNotification(getApplicationContext(), 0);
        jobFinished(params, true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        return true;
    }
}
