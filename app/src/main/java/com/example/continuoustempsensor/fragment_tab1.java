package com.example.continuoustempsensor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class fragment_tab1 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab1_layout, container, false);

//        public void sendUrgentWarning1(View sendUrgentWarning1) {
//            String title = editTextTitle.getText().toString();
//            String message = editTextMessage.getText().toString();
//            android.app.Notification notification = new NotificationCompat.Builder(this, Notification.CHANNEL_1_ID)
//                    .setSmallIcon(R.drawable.warning)
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setCategory(NotificationCompat.CATEGORY_ALARM)
//                    .build();
//            notificationManager.notify(1,notification);
//        }
//
//        public void sendUpdate2(View sendUpdate2) {
//            String title = editTextTitle.getText().toString();
//            String message = editTextMessage.getText().toString();
//            android.app.Notification notification = new NotificationCompat.Builder(this, Notification.CHANNEL_2_ID)
//                    .setSmallIcon(R.drawable.ic_announcement_black_24dp)
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setCategory(NotificationCompat.CATEGORY_EVENT)
//                    .build();
//            notificationManager.notify(2,notification);
//        }
    }
}
