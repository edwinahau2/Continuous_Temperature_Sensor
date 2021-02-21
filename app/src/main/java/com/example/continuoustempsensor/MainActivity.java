package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

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
import com.marcinmoskala.arcseekbar.ArcSeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> al = new ArrayList<>();
    File file;
    FileReader fileReader = null;
    FileWriter fileWriter = null;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;
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
    LineChart mChart;
    public static String address;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("h:mm:ss a");
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
    SwipeFlingAdapterView flingContainer;
    private TextView counter;
    private ArrayAdapter<String> arrayAdapter;
    boolean plotData = false;
    String num;
    int number;
    ArcSeekBar arcSeekBar;


    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arcSeekBar = findViewById(R.id.seekbar);
        int[] intArray = getResources().getIntArray(R.array.progressGradientColors);
        arcSeekBar.setProgressBackgroundGradient(intArray);
        arcSeekBar.setProgressGradient(intArray);
        String FILE_NAME = "temp.json";
        file = new File(this.getFilesDir(), FILE_NAME);
        Bundle bundle = getIntent().getExtras();
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        temp = findViewById(R.id.temp);
        temperature = "97.5";
        temp.setText(temperature);
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

        temp.setVisibility(View.VISIBLE);
        flingContainer = findViewById(R.id.frame);
        counter = findViewById(R.id.counter);
        al = restoreArrayData();
        number = al.size();
//        if (restoreNumData() == -1) {
//            number = al.size();
//        } else {
//            number = restoreNumData();
//        }
        counter.setText(String.valueOf(number));
        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, al);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
                saveArrayData();
            }

            @Override
            public void onLeftCardExit(Object o) {
                number--;
                num = String.valueOf(number);
                counter.setText(num);
                saveArrayData();
            }

            @Override
            public void onRightCardExit(Object o) {
                number--;
                num = String.valueOf(number);
                counter.setText(num);
                saveArrayData();
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
            }

            @Override
            public void onScroll(float v) {
            }
        });
        if (bundle != null) {
            address = bundle.getString("address");
            name = bundle.getString("name");
            Intent intent = new Intent(this, AndroidService.class);
            intent.putExtra("address", address);
//            startService(intent);
            startConnection();
            savePrefsData();
        } else if (mBlueAdapter.isEnabled()) {
            address = restoreAddressData();
            name = restoreNameData();
            Intent intent = new Intent(this, AndroidService.class);
            intent.putExtra("address", address);
//            startService(intent);
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
                        today.put("Sun.2021.02.07", obj);
                        break;
                    case(1):
                        today.put("Mon.2021.02.08", obj);
                         break;
                    case(2):
                        today.put("Tue.2021.02.09", obj);
                        break;
                    case(3):
                        today.put("Wed.2021.02.10", obj);
                        break;
                    case(4):
                        today.put("Thu.2021.02.11", obj);
                        break;
                    case(5):
                        today.put("Fri.2021.02.12", obj);
                        break;
                    case(6):
                        today.put("Sat.2021.02.13", obj);
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        fm.beginTransaction().add(R.id.container3, fragment3, "3").hide(fragment3).addToBackStack(null).commit();
        fm.beginTransaction().add(R.id.container2, fragment2, "2").hide(fragment2).addToBackStack(null).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);
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
                    arcSeekBar.setVisibility(View.VISIBLE);
//                    temp.setText(temperature);
                    if (restoreHide()) {
                        flingContainer.setVisibility(View.INVISIBLE);
                        counter.setVisibility(View.INVISIBLE);
                    } else {
                        flingContainer.setVisibility(View.VISIBLE);
                        counter.setVisibility(View.VISIBLE);
                    }
                    return true;

                case R.id.Bt:
                    if (active != null) {
                        fm.beginTransaction().hide(active).show(fragment3).commit();
                    } else {
                        fm.beginTransaction().show(fragment3).commit();
                    }
                    temp.setVisibility(View.INVISIBLE);
                    mChart.setVisibility(View.INVISIBLE);
                    flingContainer.setVisibility(View.INVISIBLE);
                    counter.setVisibility(View.INVISIBLE);
                    arcSeekBar.setVisibility(View.INVISIBLE);
                    temp.setText(temperature);
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
                    flingContainer.setVisibility(View.INVISIBLE);
                    counter.setVisibility(View.INVISIBLE);
                    arcSeekBar.setVisibility(View.INVISIBLE);
                    temp.setText(temperature);
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

    private void saveArrayData() {
        Set<String> set = new HashSet<>(al);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("arrayPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("array", set);
        editor.putString("number", num);
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

    private ArrayList<String> restoreArrayData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("arrayPrefs", MODE_PRIVATE);
        Set<String> set = pref.getStringSet("array", null);
        if (set == null) {
            Set<String> newSet = new HashSet<>();
            newSet.add("Notifications");
            newSet.add("my");
            newSet.add("name");
            newSet.add("is");
            newSet.add("Aryan");
            newSet.add("Agarwal");
            return new ArrayList<>(newSet);
        } else {
            return new ArrayList<>(set);
        }
    }

    private Boolean restoreHide() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("hidePref", MODE_PRIVATE);
        return prefs.getBoolean("hide", false);
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
        if (temperature.length() != 0) {
            float y = Float.parseFloat(temperature);
            if (y <= 95) {
                arcSeekBar.setProgress(0);
            } else if (y >= 103.8) {
                arcSeekBar.setProgress(100);
            } else if (y > 95 && y <= 97.5) {
                float z = 10 * y - 950;
                arcSeekBar.setProgress(Math.round(z));
            } else if (y >= 97.6 && y <= 98.9) {
                if (y*10 % 2 == 0) {
                    float z = 20 * y - 1926;
                    arcSeekBar.setProgress(Math.round(z));
                } else {
                    float z = (float) (20 * (y - 0.1) - 1926);
                    arcSeekBar.setProgress(Math.round(z));
                }
            } else {
                float z = 10 * y - 938;
                arcSeekBar.setProgress(Math.round(z));
            }
        }
    }
}