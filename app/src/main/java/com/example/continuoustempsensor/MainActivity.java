package com.example.continuoustempsensor;

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
import android.os.PersistableBundle;
import android.util.SparseArray;
import android.view.MenuItem;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static ArrayList<String> al = new ArrayList<>();
    private int currentSelectedItemId = R.id.home;
    private FragmentManager fragmentManager;
    public static LineData data;
    public static int x;
    public static double y;
    public static int i;
    public static BluetoothSocket mmSocket;
    public static BluetoothDevice mDevice;
    public static String deviceName;
    public static int j;
    public static boolean hide;
    public static String temperature;
    public static InputStream mmInStream;
    public static int num;
    public static Handler mHandler;
    public static String symbol;
//    private Fragment fragment1 = new fragment_tab1();
//    private Fragment fragment2 = new fragment_tab2();
//    private Fragment fragment3 = new fragment_tab3();
//    final FragmentManager fm = getSupportFragmentManager();
//    Fragment active = new fragment_tab1();
    private String temp;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            savedStateSparseArray = savedInstanceState.getSparseParcelableArray(SAVED_STATE_CONTAINER_KEY);
            currentSelectedItemId = savedInstanceState.getInt(SAVED_STATE_CURRENT_TAB_KEY);
        }
        setContentView(R.layout.activity_main);
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
//
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
