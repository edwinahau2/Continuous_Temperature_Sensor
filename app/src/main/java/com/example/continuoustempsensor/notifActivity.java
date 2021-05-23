package com.example.continuoustempsensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;

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
import java.util.ArrayList;
import java.util.Iterator;

public class notifActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NotificationRecyclerViewAdapter mAdapter;
    ArrayList<NotifItem> stringArrayList = new ArrayList<>();
    ConstraintLayout notifConstraintLayout;
    File file;
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;
    String delete;
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    int img;
    public static JSONObject mainObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);
        recyclerView = findViewById(R.id.notif_list);
        notifConstraintLayout = findViewById(R.id.notif_constraint);
        String FILE_NAME = "notif.json";
        file = new File(this.getFilesDir(), FILE_NAME);
        try {
            populateJSON();
            populateRecyclerView();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        enableSwipeToDeleteAndUndo();
    }

    private void populateJSON() throws IOException, JSONException {
        String idx;
        if (mainObj.length() != 0) {
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
            JSONArray jsonArray = new JSONArray();
            JSONObject jText = new JSONObject();
            JSONObject jTime = new JSONObject();
            JSONObject jColor = new JSONObject();
            jText.put("notifText", "Testing 4th Text"); //change
            jTime.put("notifTime", "Testing 4th Time"); //change
            jColor.put("notifColor", 0); //change
            idx = "Notif 4"; //change
            jsonArray.put(jText);
            jsonArray.put(jTime);
            jsonArray.put(jColor);
            jsonObject.put(idx, jsonArray);
            String jsonStr = jsonObject.toString();
            fileWriter = new FileWriter(file, false);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonStr);
            bufferedWriter.close();
        } else {
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jText = new JSONObject();
                    JSONObject jTime = new JSONObject();
                    JSONObject jColor = new JSONObject();
                    jText.put("notifText", "Testing 1st Text"); //change
                    jTime.put("notifTime", "Testing 1st Time"); //change
                    jColor.put("notifColor", 0); //change
                    idx = "Notif 1"; //change
                    jsonArray.put(jText);
                    jsonArray.put(jTime);
                    jsonArray.put(jColor);
                    mainObj.put(idx, jsonArray);
                } else if (i == 1) {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jText = new JSONObject();
                    JSONObject jTime = new JSONObject();
                    JSONObject jColor = new JSONObject();
                    jText.put("notifText", "Testing 2nd Text"); //change
                    jTime.put("notifTime", "Testing 2nd Time"); //change
                    jColor.put("notifColor", 1); //change
                    idx = "Notif 2"; //change
                    jsonArray.put(jText);
                    jsonArray.put(jTime);
                    jsonArray.put(jColor);
                    mainObj.put(idx, jsonArray);
                } else {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jText = new JSONObject();
                    JSONObject jTime = new JSONObject();
                    JSONObject jColor = new JSONObject();
                    jText.put("notifText", "Testing 3rd Text"); //change
                    jTime.put("notifTime", "Testing 3rd Time"); //change
                    jColor.put("notifColor", 2); //change
                    idx = "Notif 3"; //change
                    jsonArray.put(jText);
                    jsonArray.put(jTime);
                    jsonArray.put(jColor);
                    mainObj.put(idx, jsonArray);
                }
            }
            String jsonStr = mainObj.toString();
            fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonStr);
            bufferedWriter.close();
        }
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
                int notifColor = nColor.getInt("notifColor");
                if (notifColor == 0) {
                    img = R.drawable.play_arrow; //change
                } else if (notifColor == 1) {
                    img = R.drawable.clear; //change
                } else {
                    img = R.drawable.ic_b1; //change
                }
                stringArrayList.add(new NotifItem(img, notifText, notifTime + " | Swipe to Dismiss"));
            }
        }
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
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}