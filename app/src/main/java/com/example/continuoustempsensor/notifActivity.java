package com.example.continuoustempsensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class notifActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotificationRecyclerViewAdapter mAdapter;
    ArrayList<NotifItem> stringArrayList = new ArrayList<>();
    ConstraintLayout notifConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);
        recyclerView = findViewById(R.id.notif_list);
        notifConstraintLayout = findViewById(R.id.notif_constraint);
        populateRecyclerView();
        enableSwipeToDeleteAndUndo();
    }

    private void populateRecyclerView() {
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 1", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 2", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 3", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 4", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 5", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 6", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 7", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 8", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 9", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 10", "6 minutes ago | Swipe to Dismiss"));
        stringArrayList.add(new NotifItem(R.drawable.play_arrow, "Item 11", "6 minutes ago | Swipe to Dismiss"));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NotificationRecyclerViewAdapter(stringArrayList);
        recyclerView.setAdapter(mAdapter);
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallBack swipeToDeleteCallBack = new SwipeToDeleteCallBack(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAbsoluteAdapterPosition();
                final String item = stringArrayList.get(position).getTitle();
                final String subtitle = stringArrayList.get(position).getDescription();
                final int img = stringArrayList.get(position).getImg();
                mAdapter.removeItem(position);
                Snackbar snackbar = Snackbar.make(notifConstraintLayout, "Notification Removed", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", v -> {
                    mAdapter.restoreItem(item, subtitle, img, position);
                    recyclerView.scrollToPosition(position);
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}