package com.example.continuoustempsensor;

import android.content.Context;
import android.content.Intent;

public class inAppNotify {
    private int color;
    private String timeNotification;
    private String interval = fragment_tab3.restoreNotifFreq();   //the interval of time for which each notification is sent in app (selected by the user)
    private String text;
    private String generalText;

    inAppNotify(Intent intent, Context context, int code) {
        generalText = "Continue to physically distance yourself from others at 6 feet minimum and wear a face covering. Tap here more information.";

        if (code == 0) {
            text = timeNotification + "\nYou're temperatures have been normal for the past " + interval + ". " + generalText;
        } else if (code == 1) {
            text = timeNotification + "\nYou're temperature readings have been trending upwards in the past " + interval
                    + ". Other symptoms of Coronavirus include ___, as stated by the CDC. " + generalText;
        } else if (code == 2) {
            text = timeNotification + "\nYou're temperature readings indicate you are having a fever. This is a symptom of Coronavirus. Please isolate yourself immediately and contact your supervisor for further instructions. " + generalText;
        }

        //addNotificationInside()
    }

    public int getColor() {
        return color;
    }

    public String getInterval() {
        return interval;
    }

    public String getText() {
        return text;
    }

    public String getTimeNotification(){
        return timeNotification;
    }

    public String getGeneralText(){
        return generalText;
    }

    public static void addNotificationInside() {
        //
    }
}