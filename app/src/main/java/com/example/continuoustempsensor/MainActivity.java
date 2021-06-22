package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.blure.complexview.ComplexView;
import com.blure.complexview.Shadow;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> al = new ArrayList<>();
    File file;
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;;
    private static final String TAG = "MainActivityCounter";
    public static String name;
    int i = 0;
    public static boolean f = true;
    String unit = "°F";
    BluetoothAdapter mBlueAdapter;
    ArrayList<String> time = new ArrayList<>();
    StringBuilder recDataString = new StringBuilder();
    ArrayList<Float> tempVals = new ArrayList<Float>();
    TextView temp;
    public static LineChart mChart;
    public static String address;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("h:mm a");
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat date = new SimpleDateFormat("EEE.yyyy.MM.dd");
    public static String jsonDate = date.format(Calendar.getInstance().getTime());
    public static final int RESPONSE_MESSAGE = 10;
    String temperature = "101.2";
    private Fragment fragment2 = new fragment_tab2();
    private Fragment fragment3 = new fragment_tab3();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active;
    private ArrayAdapter<String> arrayAdapter;
    boolean plotData = false;
    ImageView btSym;
    TextView btStat;
    ImageView notif;
    ComplexView shadow, ring, white;
    ViewGroup vg;
    int initMin, initHour = 0;
    Boolean firstNotif = true;
    ArrayList<Float> G = new ArrayList<>();


    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String FILE_NAME = "temp.json";
        file = new File(this.getFilesDir(), FILE_NAME);
        vg = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(vg);
        shadow = findViewById(R.id.complex);
        ring = findViewById(R.id.ring);
        white = findViewById(R.id.white);
        temp = findViewById(R.id.temp);
        JSONObject json = notifActivity.mainObj;
        btSym = findViewById(R.id.btSym);
        btStat = findViewById(R.id.btStat);
        notif = findViewById(R.id.notif);
        Bundle bundle = getIntent().getExtras();
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        mChart = findViewById(R.id.sparkView);
        mChart.setVisibility(View.VISIBLE);
        mChart.setDescription(null);
        mChart.setTouchEnabled(true);
        mChart.setExtraBottomOffset(10f);
        mChart.setBackgroundColor(Color.TRANSPARENT);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        mChart.setMaxHighlightDistance(20);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setDrawGridLines(false);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setLabelCount(4, true);

        LimitLine limitLine = new LimitLine(100.4f, null);
        limitLine.setLineColor(Color.RED);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.removeAllLimitLines();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(104f);
        leftAxis.setAxisMinimum(98f);
        leftAxis.setEnabled(true);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getLegend().setEnabled(false);
        mChart.setDrawBorders(false);
        mChart.invalidate();

//        temperature = "101.3";
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, al);
        if (bundle != null) {
            address = bundle.getString("address");
            name = bundle.getString("name");
            Intent intent = new Intent(this, AndroidService.class);
            intent.putExtra("address", address);
            startService(intent);
            startConnection();
            savePrefsData();
        } else if (mBlueAdapter.isEnabled()) {
            address = restoreAddressData();
            name = restoreNameData();
            Intent intent = new Intent(this, AndroidService.class);
            intent.putExtra("address", address);
            startService(intent);
            startConnection();
        }

        /*Intent intent = getIntent();
        if (intent.hasExtra("message")) {
            firstNotif = true;
            JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.cancel(123); //job
            Log.d(TAG, "Job Cancelled");
        }*/
//        ComponentName componentName = new ComponentName(getApplicationContext(), TestJobService.class);
//        PersistableBundle bun = new PersistableBundle();
//        bun.putString("address", address);
//        JobInfo jobInfo = new JobInfo.Builder(101, componentName)
//                .setExtras(bun)
//                .setPersisted(false)
//                .setRequiresCharging(false)
//                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
//                .setBackoffCriteria(TimeUnit.MINUTES.toMillis(1), JobInfo.BACKOFF_POLICY_LINEAR)
//                .build();
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        jobScheduler.schedule(jobInfo);

        String[] hours = {"1:30", "1:35", "1:40", "1:45"};
        try {
            for (int r = 0; r < 7; r++) {
                JSONObject obj = new JSONObject();
                JSONObject today = new JSONObject();
                switch(r) {
                    case(0):
                        for (int p = 0; p < 4; p++) {
                            String key = "time" + p;
                            JSONObject reading = new JSONObject();
                            if (p == 0) {
                                reading.put("temperature", "98.6");
                            } else if (p == 1) {
                                reading.put("temperature", "96.7");
                            } else if (p == 2) {
                                reading.put("temperature", "99.0");
                            } else {
                                reading.put("temperature", "103.5");
                            } // avg: 99.4
                            reading.put("hour", hours[p]);
                            reading.put("unit", "°F");
                            obj.put(key, reading);
                        }
                        today.put("Sun.2021.05.16", obj);
                        break;
                    case(1):
                        for (int p = 0; p < 4; p++) {
                            String key = "time" + p;
                            JSONObject reading = new JSONObject();
                            if (p == 0) {
                                reading.put("temperature", "99.6");
                            } else if (p == 1) {
                                reading.put("temperature", "96.7");
                            } else if (p == 2) {
                                reading.put("temperature", "102.0");
                            } else {
                                reading.put("temperature", "103.5");
                            } // avg: 100.5
                            reading.put("hour", hours[p]);
                            reading.put("unit", "°F");
                            obj.put(key, reading);
                        }
                        today.put("Mon.2021.05.17", obj);
                         break;
                    case(2):
                        for (int p = 0; p < 4; p++) {
                            String key = "time" + p;
                            JSONObject reading = new JSONObject();
                            if (p == 0) {
                                reading.put("temperature", "108.6");
                            } else if (p == 1) {
                                reading.put("temperature", "103.7");
                            } else if (p == 2) {
                                reading.put("temperature", "102.0");
                            } else {
                                reading.put("temperature", "99.5");
                            } // 103.5
                            reading.put("hour", hours[p]);
                            reading.put("unit", "°F");
                            obj.put(key, reading);
                        }
                        today.put("Tue.2021.05.18", obj);
                        break;
                    case(3):
                        for (int p = 0; p < 4; p++) {
                            String key = "time" + p;
                            JSONObject reading = new JSONObject();
                            if (p == 0) {
                                reading.put("temperature", "100.6");
                            } else if (p == 1) {
                                reading.put("temperature", "98.7");
                            } else if (p == 2) {
                                reading.put("temperature", "95.0");
                            } else {
                                reading.put("temperature", "97.5");
                            }
                            reading.put("hour", hours[p]);
                            reading.put("unit", "°F");
                            obj.put(key, reading);
                        } // 97.9
                        today.put("Wed.2021.05.19", obj);
                        break;
                    case(4):
                        for (int p = 0; p < 4; p++) {
                            String key = "time" + p;
                            JSONObject reading = new JSONObject();
                            if (p == 0) {
                                reading.put("temperature", "100.6");
                            } else if (p == 1) {
                                reading.put("temperature", "101.7");
                            } else if (p == 2) {
                                reading.put("temperature", "100.0");
                            } else {
                                reading.put("temperature", "102.5");
                            }
                            reading.put("hour", hours[p]);
                            reading.put("unit", "°F");
                            obj.put(key, reading);
                        } // 101.2
                        today.put("Thu.2021.05.20", obj);
                        break;
                    case(5):
                        for (int p = 0; p < 4; p++) {
                            String key = "time" + p;
                            JSONObject reading = new JSONObject();
                            if (p == 0) {
                                reading.put("temperature", "98.6");
                            } else if (p == 1) {
                                reading.put("temperature", "96.7");
                            } else if (p == 2) {
                                reading.put("temperature", "99.0");
                            } else {
                                reading.put("temperature", "98.5");
                            }
                            reading.put("hour", hours[p]);
                            reading.put("unit", "°F");
                            obj.put(key, reading);
                        } // 98.2
                        today.put("Fri.2021.05.21", obj);
                        break;
                    case(6):
                        for (int p = 0; p < 4; p++) {
                            String key = "time" + p;
                            JSONObject reading = new JSONObject();
                            if (p == 0) {
                                reading.put("temperature", "98.6");
                            } else if (p == 1) {
                                reading.put("temperature", "105.7");
                            } else if (p == 2) {
                                reading.put("temperature", "99.0");
                            } else {
                                reading.put("temperature", "103.5");
                            }
                            reading.put("hour", hours[p]);
                            reading.put("unit", "°F");
                            obj.put(key, reading);
                        } // 101.7
                        today.put("Sat.2021.05.22", obj);
                        break;
                }
                String userString = today.toString();
                fileWriter = new FileWriter(file, true);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(userString);
                bufferedWriter.close();
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        notif.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), notifActivity.class);
            startActivity(intent);
        });

        btSym.setOnClickListener(v -> {
            Intent connectActivity = new Intent(getApplicationContext(), ConnectionActivity.class);
            startActivity(connectActivity);
        });

        btStat.setOnClickListener(v -> {
            Intent connectActivity = new Intent(getApplicationContext(), ConnectionActivity.class);
            startActivity(connectActivity);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        fm.beginTransaction().add(R.id.container3, fragment3, "3").hide(fragment3).addToBackStack(null).commit();
        fm.beginTransaction().add(R.id.container2, fragment2, "2").hide(fragment2).addToBackStack(null).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void tempDisplay(int num) {
        float[] radii = {250, 250, 250, 250, 250, 250, 250, 250};
        if (num == 0) {
            shadow.setShadow(new Shadow(4, 100, "#00B0F0", GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
            ring.setColor(Color.parseColor("#00B0F0"));
            temperature = "--";
            temp.setText(temperature);
            temp.setTextSize(44);
        } else if (num == 1) {
            shadow.setShadow(new Shadow(4, 100, "#00B050", GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
            ring.setColor(Color.parseColor("#00B050"));
            temp.setText(temperature + " " + unit);
            temp.setTextSize(44);
            temp.setTextColor(Color.parseColor("#000000"));
        } else if (num == 2) {
            shadow.setShadow(new Shadow(4, 100, "#FB710B", GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
            ring.setColor(Color.parseColor("#FB710B"));
            temp.setText(temperature + " " + unit);
            temp.setTextSize(44);
            temp.setTextColor(Color.parseColor("#000000"));
        } else if (num == 3) {
            shadow.setShadow(new Shadow(4, 100, "#FF0000", GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
            ring.setColor(Color.parseColor("#FF0000"));
            temp.setText(temperature + " " + unit);
            temp.setTextSize(40);
            temp.setTextColor(Color.parseColor("#000000"));
        } else {
            shadow.setShadow(new Shadow(4, 100, "#00B0F0", GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
            ring.setColor(Color.parseColor("#00B0F0"));
            temp.setText(temperature + " " + unit);
            temp.setTextSize(44);
            temp.setTextColor(Color.parseColor("#000000"));
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.home:
                    if (active != null) {
                        fm.beginTransaction().hide(active).commit();
                    }
                    temp.setVisibility(View.VISIBLE);
                    mChart.setVisibility(View.VISIBLE);
                    shadow.setVisibility(View.VISIBLE);
                    ring.setVisibility(View.VISIBLE);
                    white.setVisibility(View.VISIBLE);
                    btStat.setVisibility(View.VISIBLE);
                    btSym.setVisibility(View.VISIBLE);
                    notif.setVisibility(View.VISIBLE);
//                    temp.setText(temperature);
                    return true;

                case R.id.Bt:
                    if (active != null) {
                        fm.beginTransaction().hide(active).show(fragment3).commit();
                    } else {
                        fm.beginTransaction().show(fragment3).commit();
                    }
                    temp.setVisibility(View.INVISIBLE);
                    mChart.setVisibility(View.INVISIBLE);
                    shadow.setVisibility(View.INVISIBLE);
                    ring.setVisibility(View.INVISIBLE);
                    white.setVisibility(View.INVISIBLE);
                    btStat.setVisibility(View.INVISIBLE);
                    btSym.setVisibility(View.INVISIBLE);
                    notif.setVisibility(View.INVISIBLE);
                    active = fragment3;
                    return true;

                case R.id.profile:
                    if (active != null) {
                        fm.beginTransaction().hide(active).show(fragment2).commit();
                    } else {
                        fm.beginTransaction().show(fragment2).commit();
                    }
                    temp.setVisibility(View.INVISIBLE);
                    mChart.setVisibility(View.INVISIBLE);
                    shadow.setVisibility(View.INVISIBLE);
                    ring.setVisibility(View.INVISIBLE);
                    white.setVisibility(View.INVISIBLE);
                    btStat.setVisibility(View.INVISIBLE);
                    btSym.setVisibility(View.INVISIBLE);
                    notif.setVisibility(View.INVISIBLE);
                    active = fragment2;
                    return true;
            }
            return false;
        }
    };

    private void startConnection() {

        AndroidService.mHandler  = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == RESPONSE_MESSAGE) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("~");
                    if (endOfLineIndex > 0) {
                        boolean legit = false;
                        if (recDataString.charAt(0) == '#') {
                            String sensor = recDataString.substring(1, endOfLineIndex);
                            float sensorVal =  Float.parseFloat(sensor);
                            tempVals.add(sensorVal);
                            int N = tempVals.size();
                            Log.d(TAG, "tempVal size: " + N);
                            if (N >= 5){ // 5 minutes -- 30 data points for 5 min (originally N >= 30)
                                double total =0;
                                for(int i=0;i<N;i++) {
                                    total += tempVals.get(i);
                                }
                                double mean = total/N;
                                double total2 = 0;
                                for (int i=0;i<N; i++) {
                                    total2 += Math.pow((tempVals.get(i) - mean), 2);
                                }
                                double std = Math.sqrt(total2 / (N - 1));
                                if (std*std < 0.2) {
                                    for (int i = 0; i < N; i++) {
                                        double Gstat = Math.abs(tempVals.get(i) - mean) / std;
                                        if (Gstat < 2.75) {
                                            G.add(tempVals.get(i));
                                        }
                                    }
                                    legit = !G.isEmpty();
                                }
                                if (legit) {
                                    Collections.sort(tempVals);
                                    double medianTemp;
                                    if (N % 2 == 0) {
                                        medianTemp = (G.get(G.size()/2) + G.get((G.size()/2) - 1)) / 2.0;
                                    } else {
                                        medianTemp = (G.get(G.size()/2)) / 1.0;
                                    }
                                    if (!f) {
                                        medianTemp = (double) Math.round((medianTemp - 32) * 5 / 9.0);
                                    }
                                    DecimalFormat df = new DecimalFormat("#.#");
                                    temperature = df.format(medianTemp);
                                    onResume();
                                    plotData = true;
                                    new Thread(() -> {
                                        while (plotData) {
                                            runOnUiThread(() -> {
                                                String clock = format.format(Calendar.getInstance().getTime());
                                                time.add(clock);
                                                addEntry(temperature);
                                                plotData = false;
                                                if (f) {
                                                    unit = "°F";
                                                } else {
                                                    unit = "°C";
                                                }
                                            writeJSON(temperature, clock, i, unit);
                                            i++;
                                            });
                                            try {
                                                Thread.sleep(5000);
                                            } catch (InterruptedException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                //only applies when user has not force closed the app
                                    //medianTemp = 101;
                                    if (medianTemp >= 0) {
                                        if (medianTemp >= 100.3) {// more urgent -- red
                                            if (firstNotif) {// send first notif
                                                firstNotif = false;
                                                scheduleJob(5); //notif sent in urgentNotifJob class
                                            } else {// buffer for next urgent notification -- Job Scheduler
                                                Toast.makeText(getApplicationContext(), String.valueOf(initMin), Toast.LENGTH_SHORT).show(); //for me to see if it works
                                                //check if notif clicked -> if clicked then will cancel the buffer

                                                //HERE!!!

                                            }
                                        } else{//not urgent normal notification -- temp greater than 0 but less than 100.3
                                            // json write to notif file w/ nonurgent level
                                            // textTimeNotify time
                                            // normal notifictation interval check
                                            NotificationReceiver.sendNotification(getApplicationContext(), 2); // NOT URGENT notif
                                        }
                                    }
                                }
                                G.clear();
                                tempVals.clear();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "nan", Toast.LENGTH_SHORT).show();
                    }
                    recDataString.delete(0, recDataString.length());
                }
            }
        };
    }
    public void scheduleJob(int timeBackoff){
            ComponentName componentName = new ComponentName(this, urgentNotifJob.class);
            JobInfo info = new JobInfo.Builder(123, componentName)
                    .setPersisted(true) // will continue job id device reboots
                    .setPeriodic(15 * 60 * 1000) //15 min minimum
                    .setBackoffCriteria(TimeUnit.MINUTES.toMillis(timeBackoff), JobInfo.BACKOFF_POLICY_LINEAR)
                    .setRequiresCharging(false)
                    .build();

        JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "Job scheduled");
        } else{
            Log.d(TAG, "Job scheduling failed");
        }

    }

    public void cancelJob(View v){
        JobScheduler scheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123); //jobID is to identify the job you are passing through
        Log.d(TAG, "Job cancelled");
    }

    private void writeJSON(String temperature, String clock, int i, String unit) {
        try {
            String index = String.valueOf(i);
            String key = "time" + index;
            JSONObject reading = new JSONObject();
            reading.put("temperature", temperature);
            reading.put("hour", clock);
            reading.put("unit", unit);
            JSONObject obj = new JSONObject();
            obj.put(key, reading);
            JSONObject today = new JSONObject();
            today.put(jsonDate, obj);
            String userString = today.toString();
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(userString);
            bufferedWriter.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addEntry(String temperature) {
        LineData data = mChart.getData();
        if (data != null) {
            MyLineDataSet mySet = (MyLineDataSet) data.getDataSetByIndex(0);
            if (mySet == null) {
                mySet = createSet();
                data.addDataSet(mySet);
            }
            float y = Float.parseFloat(temperature);
            data.addEntry(new Entry(mySet.getEntryCount(), y), 0);
            XAxis xl = mChart.getXAxis();
            xl.setValueFormatter(new IndexAxisValueFormatter(time));
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(6);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private MyLineDataSet createSet() {
        MyLineDataSet mySet = new MyLineDataSet(null, null);
        mySet.setDrawCircles(false);
        mySet.setDrawValues(false);
        mySet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mySet.setLineWidth(3f);
        mySet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        mySet.setCubicIntensity(0.2f);
        mySet.setColors(ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.green), ContextCompat.getColor(this, R.color.yellow));
//        LineDataSet set = new LineDataSet(null, null);
//        set.setDrawCircles(true);
//        set.setFillAlpha(100);
//        set.setFillColor(ColorTemplate.getHoloBlue());
//        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setLineWidth(3f);
//        set.setColor(Color.MAGENTA);
//        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        set.setCubicIntensity(0.2f);
        return mySet;
    }

    private void savePrefsData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("devicePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device", name);
        editor.putString("address", address);
        editor.apply();
    }

    private String restoreNameData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("devicePrefs", MODE_PRIVATE);
        return pref.getString("device", null);
    }

    private String restoreAddressData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("devicePrefs", MODE_PRIVATE);
        return pref.getString("address", null);
    }

    private int restoreNumData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("arrayPrefs", MODE_PRIVATE);
        return Integer.parseInt(pref.getString("number", String.valueOf(-1)));
    }

    public static void saveIdx(int idx, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("jsonIdx", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("idx", idx);
        editor.apply();
    }

    public static String restoreIdx(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("jsonIdx", MODE_PRIVATE);
        int idx = preferences.getInt("idx", 0) + 1;
        return "Notif " + idx;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AndroidService.spark) {
            btStat.setText("Connected");
            btSym.setBackgroundResource(R.drawable.ic_b1);
        } else {
            btStat.setText("Not Connected");
            btSym.setBackgroundResource(R.drawable.ic_b2);
        }
        int num;
        if (temperature.length() != 0) {
            float y = Float.parseFloat(temperature);
            if (y <= 99.9 || y <= 37.7) {
                num = 1;
            } else if ((y <= 100.4 && y >= 100) || (y <= 38 && y >= 37.8)) {
                num = 2;
            } else if ((y > 100.4 && y <= 102.9) || (y > 38 && y <= 39.4)) {
                num = 3;
            } else {
                num = 4;
            }
        } else {
            num = 0;
        }
        tempDisplay(num);
    }
}