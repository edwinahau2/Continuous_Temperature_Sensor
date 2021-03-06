package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import com.blure.complexview.ComplexView;
import com.blure.complexview.Shadow;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import kotlinx.coroutines.Job;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> al = new ArrayList<>();
    File file;
    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;;
    private static final String TAG = "MainActivity";
//    private JSONObject reading = new JSONObject();
//    private JSONObject today = new JSONObject();
//    private JSONObject obj = new JSONObject();
    public static String name;
    int i = 0;
    public static boolean f = true;
    String unit;
//    static BluetoothSocket mmSocket;
//    BluetoothDevice mDevice;
    BluetoothAdapter mBlueAdapter;
    ArrayList<String> time = new ArrayList<>();
//    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//    ConnectedThread btt = null;
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
    String temperature;
//    static InputStream mmInStream;
//    static Handler mHandler;
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

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getLegend().setEnabled(false);
        mChart.setDrawBorders(false);
        mChart.invalidate();

        temperature = "100.6";
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
        try {
            for (int r = 0; r < 7; r++) {
                JSONObject obj = new JSONObject();
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
                        reading.put("temperature", "97.8");
                    }
                    reading.put("hour", "1:30");
                    reading.put("unit", "°F");
                    obj.put(key, reading);
                }
                JSONObject today = new JSONObject();
                switch(r) {
                    case(0):
                        today.put("Sun.2021.03.07", obj);
                        break;
                    case(1):
                        today.put("Mon.2021.03.08", obj);
                         break;
                    case(2):
                        today.put("Tue.2021.03.09", obj);
                        break;
                    case(3):
                        today.put("Wed.2021.03.10", obj);
                        break;
                    case(4):
                        today.put("Thu.2021.03.11", obj);
                        break;
                    case(5):
                        today.put("Fri.2021.03.12", obj);
                        break;
                    case(6):
                        today.put("Sat.2021.03.13", obj);
                        break;
                }
//                today.put("Sun.2021.02.07", obj);
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
//            temp.setTextSize((float) (height*0.04));
        } else if (num == 1) {
            shadow.setShadow(new Shadow(4, 100, "#00B050", GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
            ring.setColor(Color.parseColor("#00B050"));
            temp.setText(temperature + " " + unit);
//            temp.setTextSize((float) (height * 0.04));
        } else if (num == 2) {
            shadow.setShadow(new Shadow(4, 100, "#FB710B", GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
            ring.setColor(Color.parseColor("#FB710B"));
            temp.setText(temperature + " " + unit);
//            temp.setTextSize((float) (height*0.04));
        } else if (num == 3) {
            shadow.setShadow(new Shadow(4, 100, "#FF0000", GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
            ring.setColor(Color.parseColor("#FF0000"));
            temp.setText(temperature + " " + unit);
//            temp.setTextSize((float) (height*0.04));
        } else {
            shadow.setShadow(new Shadow(4, 100, "#00B0F0", GradientDrawable.RECTANGLE, radii, Shadow.Position.CENTER));
            ring.setColor(Color.parseColor("#00B0F0"));
            temp.setText(temperature + " " + unit);
//            temp.setTextSize((float) (height*0.04));
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
//        if (mmSocket == null || !mmSocket.isConnected()) {
//            BluetoothSocket tmp;
//            try {
//                tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                mmSocket = tmp;
//                mmSocket.connect();
//            } catch (IOException e) {
//                try {
//                    mmSocket.close();
//                } catch (IOException c) {
//                    e.printStackTrace();
//                }
//            }

//            btt = new ConnectedThread(mmSocket);
//            btt.start();

            AndroidService.mHandler  = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == RESPONSE_MESSAGE) {
                        String readMessage = (String) msg.obj;
                        recDataString.append(readMessage);
                        int endOfLineIndex = recDataString.indexOf("~");
                        if (endOfLineIndex > 0) {
                            String dataInPrint = recDataString.substring(0, endOfLineIndex);

                            if (recDataString.charAt(0) == '#') {
                                String sensor = recDataString.substring(1, endOfLineIndex);
                                float sensorVal =  Float.parseFloat(sensor);
                                tempVals.add(sensorVal);

                                boolean legit = true;
                                if (tempVals.size()>60){
                                    double min = Collections.min(tempVals);
                                    double max = Collections.max(tempVals);
                                    double total =0;
                                    for(int i=0;i<tempVals.size();i++)
                                    {
                                        total+=tempVals.get(i);
                                    }
                                    double mean = total/tempVals.size();
                                    double total2 =0;
                                    for (int i=0;i<tempVals.size();i++)
                                    {
                                        total2 += Math.pow((i - mean), 2);
                                    }
                                    double std = Math.sqrt( total2 / ( tempVals.size() - 1 ) );
                                    double gLower = (mean - min)/std;
                                    double gUpper = (max-mean)/std;
                                    if(gLower > 3.0269 || gUpper >3.0369){
                                        // There's an outlier
                                        legit = false;
                                    }
                                    if(std*std > 0.50){
                                        //Too much variance
                                        legit =false;
                                    }
                                }
                                if(legit) {
                                    Collections.sort(tempVals);
                                    double medianTemp;
                                    if (tempVals.size() % 2 == 0)
                                    {
                                        medianTemp = ((double) Math.round(((tempVals.get(tempVals.size()/2) + (double)tempVals.get(tempVals.size()/2 - 1))/2) * 10) / 10.0);
                                    }
                                    else {
                                        medianTemp = (double) Math.round((tempVals.get(tempVals.size()/2) * 10)/10.0);
                                    }
                                    if (!f) {
                                        medianTemp = (double) Math.round((medianTemp - 32) * 5 / 9.0);
                                    }
                                    temperature = Double.toString(medianTemp);
//                                    temp.setText(temperature);
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
                                }
                            }
                            recDataString.delete(0, recDataString.length());
                            dataInPrint = "";
                        }
                    }
                }
            };
        }
//    }

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
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            float y = Float.parseFloat(temperature);
            data.addEntry(new Entry(set.getEntryCount(), y), 0);
            XAxis xl = mChart.getXAxis();
            xl.setValueFormatter(new IndexAxisValueFormatter(time));
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(6);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, null);
        set.setDrawCircles(true);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

//    protected static class ConnectedThread extends Thread {
//        public ConnectedThread(BluetoothSocket socket) {
//            InputStream tmpIn = null;
//            try {
//                tmpIn = socket.getInputStream();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mmInStream = tmpIn;
//        }
//
//        public void run() {
//            BufferedReader br;
//            br = new BufferedReader(new InputStreamReader(mmInStream));
//            while (true) {
//                try {
//                    String resp = br.readLine();
//                    Message msg = new Message();
//                    msg.what = RESPONSE_MESSAGE;
//                    msg.obj = resp;
//                    mHandler.sendMessage(msg);
//                } catch (IOException e) {
//                    break;
//                }
//            }
//        }
//
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
            if (y <= 98.99 || y <= 37.2) {
                num = 1;
            } else if ((y < 100.4 && y >= 99) || (y < 38 && y > 37.2)) {
                num = 2;
            } else if ((y <= 103 && y >= 100.4) || (y <= 39.4 && y >= 38)) {
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