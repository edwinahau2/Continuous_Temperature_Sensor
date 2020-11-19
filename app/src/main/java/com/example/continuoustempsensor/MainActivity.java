package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import com.github.mikephil.charting.data.LineData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    public static ArrayList<String> al = new ArrayList<>();
    private int currentSelectedItemId = R.id.home;
    private FragmentManager fragmentManager;
    public static LineData data;
    public static int x;
    public static double y;
    public static int i;
    BluetoothSocket mmSocket;
    BluetoothDevice mDevice;
    BluetoothAdapter mBlueAdapter;
    public static String deviceName;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    ConnectedThread btt = null;
    StringBuilder recDataString = new StringBuilder();
    ArrayList<Float> tempVals = new ArrayList<Float>();
    TextView temp;
    public static final int RESPONSE_MESSAGE = 10;
    public static int j;
    public static boolean hide;
    String temperature;
    InputStream mmInStream;
    public static int num;
    Handler mHandler;
    public static String symbol;
//    private Fragment fragment1 = new fragment_tab1();
//    private Fragment fragment2 = new fragment_tab2();
//    private Fragment fragment3 = new fragment_tab3();
//    final FragmentManager fm = getSupportFragmentManager();
//    Fragment active = new fragment_tab1();
//    private String temp;
    private SparseArray savedStateSparseArray = new SparseArray();
    public static final String SAVED_STATE_CONTAINER_KEY = "ContainerKey";
    public static final String SAVED_STATE_CURRENT_TAB_KEY = "CurrentTabKey";
//    private boolean hide;


    public static void addal(String my) {
        al.add(my);
    }

    public static void coordinate(int lastX, double lastY) {
        x = lastX;
        y = lastY;
    }

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        temp = findViewById(R.id.temp);
        if (savedInstanceState != null) {
            savedStateSparseArray = savedInstanceState.getSparseParcelableArray(SAVED_STATE_CONTAINER_KEY);
            currentSelectedItemId = savedInstanceState.getInt(SAVED_STATE_CURRENT_TAB_KEY);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String address = bundle.getString("address");
            mDevice = mBlueAdapter.getRemoteDevice(address);
            if (mmSocket == null || !mmSocket.isConnected()) {
                BluetoothSocket tmp;
                try {
                    tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    mmSocket = tmp;
                    mmSocket.connect();
                } catch (IOException e) {
                    try {
                        mmSocket.close();
                    } catch (IOException c) {
                        e.printStackTrace();
                    }
                }

                mHandler  = new Handler(Looper.getMainLooper()) {
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
                                        temperature = Double.toString(medianTemp);
                                        temp.setText(temperature);
//                                        key = 1;
//                                            retrieveJSON(tf, check, symbol, key);
//                                            mCallback.messageFromBt(tf, check, symbol, key);
                                    }
                                }
                                recDataString.delete(0, recDataString.length());
                                dataInPrint = "";
                            }
                        }
                    }
                };
                btt = new ConnectedThread(mmSocket);
                btt.start();
            }
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
//        bottomNavigationView.setSelectedItemId(R.id.home);
//        fm.beginTransaction().add(R.id.container3, fragment3, "3").hide(fragment3).addToBackStack(null).commit();
//        fm.beginTransaction().add(R.id.container2, fragment2, "2").hide(fragment2).addToBackStack(null).commit();
//        fm.beginTransaction().add(R.id.container1, fragment1, "1").addToBackStack(null).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);
        fragmentManager = getSupportFragmentManager();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.home:
                    Fragment fragment1 = new fragment_tab1();
                    openFragment(fragment1, fragment_tab1.TAG, item.getItemId());

//                    fm.beginTransaction().hide(active).show(fragment1).commit();
//                    active = fragment1;
//                    if (temp == null) {
//                        fm.beginTransaction().hide(active).show(fragment1).commit();
//                        active = fragment1;
//                    } else {
//                        fm.beginTransaction().hide(active).show(newFrag).commit();
//                        active = newFrag;
//                    }
                    return true;

                case R.id.Bt:
                    Fragment fragment3 = new fragment_tab3();
                    openFragment(fragment3, fragment_tab3.TAG, item.getItemId());
//                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    return true;

                case R.id.profile:
                    Fragment fragment2 = new fragment_tab2();
                    openFragment(fragment2, fragment_tab2.TAG, item.getItemId());
//                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    return true;
            }
            return false;
        }
    };

    public void openFragment(Fragment fragment, String TAG, int item) {
        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            saveFragmentState(item, TAG);
            createFragment(fragment, item, TAG);
        }
//        FragmentManager fm = getSupportFragmentManager();
//        if (fragment_tab1.TAG.equals(TAG)) {
//            fm.beginTransaction().hide(undesired).replace(R.id.container, fragment).addToBackStack(null).commit();
//        } else if (fragment_tab2.TAG.equals(TAG)) {
//            fm.beginTransaction().hide(undesired).replace(R.id.container2, fragment).addToBackStack(null).commit();
//        } else {
//            fm.beginTransaction().hide(undesired).replace(R.id.container3, fragment).addToBackStack(null).commit();
//        }
    }

    private void saveFragmentState(int item, String tag) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment != null) {
            savedStateSparseArray.put(currentSelectedItemId, getSupportFragmentManager().saveFragmentInstanceState(currentFragment));
        }
        currentSelectedItemId = item;
    }

    private void createFragment(Fragment fragment, int item, String tag) {
        fragment.setInitialSavedState((Fragment.SavedState) savedStateSparseArray.get(item));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, tag).commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = this.getSupportFragmentManager();
        List fmList = fm.getFragments();
        Iterable $receiver$iv = fmList;
        Iterator var2 = $receiver$iv.iterator();

        while (var2.hasNext()) {
            Object element$iv = var2.next();
            Fragment fragment = (Fragment) element$iv;
            if (fragment != null && fragment.isVisible()) {
                FragmentManager var6 = fragment.getChildFragmentManager();
                if (var6.getBackStackEntryCount() > 0) {
                    var6.popBackStack();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSparseParcelableArray("ContainerKey", savedStateSparseArray);
        outState.putInt("CurrentTabKey", currentSelectedItemId);
    }

    private class ConnectedThread extends Thread {
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
        }

        public void run() {
            BufferedReader br;
            br = new BufferedReader(new InputStreamReader(mmInStream));
            while (true) {
                try {
                    String resp = br.readLine();
                    Message msg = new Message();
                    msg.what = RESPONSE_MESSAGE;
                    msg.obj = resp;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //    public void onAttachFragment(@NonNull Fragment fragment) {
//        if (fragment instanceof fragment_tab3) {
//            fragment_tab3 headlinesFragment = (fragment_tab3) fragment;
//            headlinesFragment.setCallback(this);
//        }
//    }
//    @Override
//    public void messageFromBt(String sensor, Boolean b, String symbol, int key) {
//        temp = sensor + symbol;
//        temperature = temp;
//        hide = b;
//        num = key;
//        newFrag = new fragment_tab1();
//        Bundle args = new Bundle();
//        args.putString("temperature", temp);
//        args.putBoolean("inApp", b);
//        args.putInt("key", key);
//        newFrag.setArguments(args);
//        fm.beginTransaction().replace(R.id.container1, newFrag, "1").hide(newFrag).addToBackStack(null).commit();
//    }
}
