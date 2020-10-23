package com.example.continuoustempsensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ExposureNotificationBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED
                .equals(action))
        {
            // Handle exposure found action
        }
        else if (ExposureNotificationClient.ACTION_EXPOSURE_NOT_FOUND
                .equals(action))
        {
            // Handle exposure not found action
        }
        else if (ExposureNotificationClient.ACTION_SERVICE_STATE_UPDATED
                .equals(action))
        {
            // Handle service state change (for example, user manually disabled it)
        }
    }
}