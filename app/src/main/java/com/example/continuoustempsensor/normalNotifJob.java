package com.example.continuoustempsensor;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class normalNotifJob extends JobService {

    private static final String TAG = "normalNotif";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        float medianTemp = MainActivity.restoreTempVal(this);
        int code;
        if (medianTemp <= 99.9 || medianTemp <= 37.7) {
            code = 3;
        } else if ((medianTemp <= 100.4 && medianTemp >= 100) || (medianTemp <= 38 && medianTemp >= 37.8)) {
            code = 2;
        } else if ((medianTemp > 100.4 && medianTemp <= 102.9) || (medianTemp > 38 && medianTemp <= 39.4)) {
            code = 1;
        } else {
            code = 0;
        }
        NotificationReceiver.sendNotification(getApplicationContext(), code);
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        return true;
    }
}
