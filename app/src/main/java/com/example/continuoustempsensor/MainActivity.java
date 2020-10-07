package com.example.continuoustempsensor;

import android.os.Bundle;

import com.github.mikephil.charting.data.LineData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements fragment_tab3.Callback {

    public static ArrayList<String> al = new ArrayList<>();
    public static LineData data;
    public static int x;
    public static double y;
    public static int i;
    public static int j;
    private Fragment fragment1 = new fragment_tab1();
    private Fragment fragment2 = new fragment_tab2();
    private Fragment fragment3 = new fragment_tab3();
    private fragment_tab1 newFrag = new fragment_tab1();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    private String temp;
    private boolean hide;

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
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        bottomNavigationView.setSelectedItemId(R.id.home);
        fm.beginTransaction().add(R.id.container3, fragment3, "3").hide(fragment3).addToBackStack(null).commit();
        fm.beginTransaction().add(R.id.container2, fragment2, "2").hide(fragment2).addToBackStack(null).commit();
        fm.beginTransaction().add(R.id.container1, fragment1, "1").addToBackStack(null).commit();
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.home:
                    if (temp == null) {
                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;
                    } else {
                        fm.beginTransaction().hide(active).show(newFrag).commit();
                        active = newFrag;
                    }
                    return true;

                case R.id.Bt:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;

                case R.id.profile:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;
            }
            return false;
        }
    };

    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof fragment_tab3) {
            fragment_tab3 headlinesFragment = (fragment_tab3) fragment;
            headlinesFragment.setCallback(this);
        }
    }

    @Override
    public void messageFromBt(String sensor, Boolean b, String symbol) {
        temp = sensor + symbol;
        hide = b;
        newFrag = new fragment_tab1();
        Bundle args = new Bundle();
        args.putString("temperature", temp);
        args.putBoolean("inApp", hide);
        newFrag.setArguments(args);
        fm.beginTransaction().replace(R.id.container1, newFrag, "1").hide(newFrag).addToBackStack(null).commit();
    }
}
