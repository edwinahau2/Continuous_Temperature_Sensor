package com.example.continuoustempsensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //sends toast when click button called open app under the notification
        String message = intent.getStringExtra("ButtonUnderneath");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); // change to open email settings

        /*Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        getActivity().startActivity(intent);*/
    }
}
