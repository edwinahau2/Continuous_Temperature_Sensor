package com.example.continuoustempsensor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

public class fragment_tab1 extends Fragment {
    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_layout, container, false);

        notificationManager = NotificationManagerCompat.from(this.getContext());
        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextMessage = view.findViewById(R.id.edit_text_message);
        Button warningButton = view.findViewById(R.id.urgent_warning);
        warningButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View w) {
                String title = editTextTitle.getText().toString();
                String message = editTextMessage.getText().toString();
                android.app.Notification notification = new NotificationCompat.Builder(this.getContext(), notifications.CHANNEL_1_ID).setSmallIcon(R.drawable.warning)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .build();
                notificationManager.notify(1, notification);
            }
        });

        Button updateButton = view.findViewById(R.id.send_update);

        return view;
    }

//    public void sendUrgentWarning1(View view){ //fragment does not recognize this
//        //java.lang.IllegalStateException: Could not find method sendUrgentWarning1(View) in a parent or ancestor Context for android:onClick attribute defined on view class androidx.appcompat.widget.AppCompatButton
//        String title = editTextTitle.getText().toString();
//        String message = editTextMessage.getText().toString();
//        android.app.Notification notification = new NotificationCompat.Builder(this.getContext(), notifications.CHANNEL_1_ID)
//                .setSmallIcon(R.drawable.warning)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_ALARM)
//                .build();
//        notificationManager.notify(1, notification);
//    }
//
//    public void sendUpdate2(View view){
//        String title = editTextTitle.getText().toString();
//        String message = editTextMessage.getText().toString();
//        android.app.Notification notification = new NotificationCompat.Builder(this.getContext(), notifications.CHANNEL_2_ID)
//                .setSmallIcon(R.drawable.announcement)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setCategory(NotificationCompat.CATEGORY_EVENT)
//                .build();
//        notificationManager.notify(2, notification);
//    }
}
