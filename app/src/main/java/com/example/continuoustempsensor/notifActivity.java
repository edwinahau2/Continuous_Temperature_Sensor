package com.example.continuoustempsensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class notifActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotificationRecyclerViewAdapter mAdapter;
    ArrayList<NotifItem> stringArrayList = new ArrayList<>();
    ConstraintLayout notifConstraintLayout;
    File file;
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    int img;
    TextView noNotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);
        recyclerView = findViewById(R.id.notif_list);
        noNotif = findViewById(R.id.noNotif);
        notifConstraintLayout = findViewById(R.id.notif_constraint);
        Button back = findViewById(R.id.notifBack);
        String FILE_NAME = "notif.json";
        file = new File(this.getFilesDir(), FILE_NAME);
        try {
            populateRecyclerView();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        enableSwipeToDeleteAndUndo();
        back.setOnClickListener(v -> onBackPressed());
    }

    private void populateRecyclerView() throws IOException, JSONException {
        fileReader = new FileReader(file);
        bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        String response = stringBuilder.toString();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray names = jsonObject.names();
        if (names != null) {
            for (int n = 0; n < names.length(); n++) {
                JSONArray notifJSON = (JSONArray) jsonObject.get(names.getString(n));
                JSONObject nText = notifJSON.getJSONObject(0);
                JSONObject nTime = notifJSON.getJSONObject(1);
                JSONObject nColor = notifJSON.getJSONObject(2);
                String notifText = nText.getString("notifText");
                String notifTime = nTime.getString("notifTime");
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                try {
                    Date date = sdf.parse(notifTime);
                    Date nowTime = Calendar.getInstance().getTime();
                    float diff = nowTime.getTime() - date.getTime();
                    float t = diff/(1000*60*60); // convert to hours
                    if (t >= 1) {
                        if (t < 24 && t >= 2) {
                            notifTime = String.valueOf((sdf2.parse(notifTime)));
                        } else if (t < 2) {
                            notifTime =  "1 hour ago";
                        } else {
                            notifTime = String.valueOf(sdf1.parse(notifTime));
                            float d = t/24;
                            if (d < 7 && d >= 2) {
                                notifTime = (int) Math.floor(d) + " days ago";
                            } else if (d >= 7) {
                                notifTime = String.valueOf(sdf1.parse(notifTime)); 
                            } else {
                                notifTime = "1 day ago";
                            }
                        }
                    } else if (t < 1/60){ // is t less than 1 min (1/60 hours) ?
                        float s = t*3600; // convert to seconds
                        if (s <= 1) {
                            notifTime =  "Just now";
                        }
                    } else {
                        float m = t*60; // convert to minutes
                        if (m <= 1) {
                            notifTime = "1 minute ago";
                        } else {
                            notifTime = (int) Math.floor(m) + " minutes ago";
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    noNotif.setVisibility(View.VISIBLE);
                }
                int notifColor = nColor.getInt("notifColor");
                if (notifColor == 0) {
                    img = R.drawable.temp_red;
                } else if (notifColor == 1) {
                    img = R.drawable.temp_orange;
                } else if (notifColor == 2){
                    img = R.drawable.ic_b1; //change
                } else {
                    img = R.drawable.temp_green;
                }
                stringArrayList.add(new NotifItem(img, notifText, notifTime + " | Swipe to Dismiss"));
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new NotificationRecyclerViewAdapter(stringArrayList);
            recyclerView.setAdapter(mAdapter);
            noNotif.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noNotif.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
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
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        try {
                            fileReader = new FileReader(file);
                            bufferedReader = new BufferedReader(fileReader);
                            StringBuilder stringBuilder = new StringBuilder();
                            String line = bufferedReader.readLine();
                            while (line != null) {
                                stringBuilder.append(line).append("\n");
                                line = bufferedReader.readLine();
                            }
                            bufferedReader.close();
                            String response = stringBuilder.toString();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray names = jsonObject.names();
                            if (names != null) {
                                String tmp = names.getString(position);
                                jsonObject.remove(tmp);
                                String jsonStr = jsonObject.toString();
                                fileWriter = new FileWriter(file, false);
                                bufferedWriter = new BufferedWriter(fileWriter);
                                bufferedWriter.write(jsonStr);
                                bufferedWriter.close();
                            } else {
                                noNotif.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            noNotif.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}