package com.example.continuoustempsensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPageAdapter introViewPageAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), bluetoothActivity.class);
            startActivity(mainActivity);
            finish();
        }
        setContentView(R.layout.activity_intro);
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Welcome", "", R.drawable.ic_gears));
        mList.add(new ScreenItem("Fast Delivery", "", R.drawable.ic_temp));
        mList.add(new ScreenItem("Easy Payment","", R.drawable.ic_history));
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPageAdapter = new IntroViewPageAdapter(this,mList);
        screenPager.setAdapter(introViewPageAdapter);
        tabIndicator.setupWithViewPager(screenPager);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size()-1) {
                    loadLastScreen();
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.INVISIBLE);
                    tabIndicator.setVisibility(View.VISIBLE);
                }
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loadLastScreen();
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnGetStarted.setVisibility(View.INVISIBLE);
                    tabIndicator.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), bluetoothActivity.class);
                startActivity(mainActivity);

                savePrefsData();
                finish();
            }
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return pref.getBoolean("isIntroOpen", false);
    }

    private void savePrefsData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isIntroOpen", true);
        editor.apply();
    }

    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);
    }
}