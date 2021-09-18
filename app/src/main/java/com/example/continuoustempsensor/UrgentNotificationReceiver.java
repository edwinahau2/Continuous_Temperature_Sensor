package com.example.continuoustempsensor;

import android.app.NotificationManager;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UrgentNotificationReceiver extends BroadcastReceiver {

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
            MainActivity.firstNotif = true;
            // TODO: email supervisor
        }
    }
}