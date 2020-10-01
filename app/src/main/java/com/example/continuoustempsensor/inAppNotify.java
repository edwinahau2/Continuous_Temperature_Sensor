package com.example.continuoustempsensor;

import android.content.Context;
import android.content.Intent;

public class inAppNotify {
    private int color;
    public String time;

    inAppNotify(Intent intent, Context context, int code) {
        if (code == 0) {

        } else if (code == 1) {

        } else if (code == 2) {

        }
    }

    public int getColor() {
        return color;
    }

    public String getTime() {
        return time;
    }
}