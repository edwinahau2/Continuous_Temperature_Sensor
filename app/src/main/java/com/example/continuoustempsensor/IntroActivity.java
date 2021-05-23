package com.example.continuoustempsensor;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntroActivity extends AppCompatActivity implements BtAdapter.OnDeviceListener {

    private CustomViewPager screenPager;
    IntroViewPageAdapter introViewPageAdapter;
    public static Button btnNext;
    int position;
    public static Button btnBack;
    Animation btnAnim;
    public static boolean open;
    public static LinearLayout dotIndicators;
    TextView[] mDots;
    int tracker = 5;
    boolean load = false;
    Dialog myDialog;
    Dialog otherDialog;
    BtAdapter btAdapter;
    List<BtDevice> mData;
    private BluetoothAdapter mBlueAdapter;
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_ENABLE_BT = 0;
    RecyclerView btRecycle;
    Toast toast;
    String correct;
    String addy;
    Button find;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();
        }
        setContentView(R.layout.activity_intro);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setVisibility(View.INVISIBLE);
        dotIndicators = findViewById(R.id.dotIndicators);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.setCancelable(true);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Welcome", "", R.drawable.ic_gears, R.drawable.ic_temp, 0));
        mList.add(new ScreenItem("Terms of Service", "", 0, 0, 1));
        mList.add(new ScreenItem("How To Sensor", "", 0, 0, 2));
        mList.add(new ScreenItem("App Navigation", "", 0, 0, 3));
        mList.add(new ScreenItem("Connect", "", R.drawable.ic_gears, R.drawable.ic_temp, 4));
        mDots = new TextView[tracker];
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(Color.parseColor("#cccccc"));

            dotIndicators.addView(mDots[i]);
        }
        mDots[0].setTextColor(Color.parseColor("#00B0F0"));
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPageAdapter = new IntroViewPageAdapter(this, mList);
        screenPager.setAdapter(introViewPageAdapter);
//        screenPager.addOnPageChangeListener(viewListener);
        btnNext.setOnClickListener(v -> {
            btnBack.setVisibility(View.VISIBLE);
            position = screenPager.getCurrentItem();
            if (position < 2) {
                position++;
                addDotsIndicator(position);
                screenPager.setCurrentItem(position);
            } else if (position == 2) {
                if (!load) {
                    position++;
                    addDotsIndicator(position);
                    screenPager.setCurrentItem(position);
                    loadLastScreen();
                }
            } else if (position == 3){
                    position++;
                    addDotsIndicator(position);
                    screenPager.setCurrentItem(position);
                    btnNext.setVisibility(View.INVISIBLE);
                    if (ContextCompat.checkSelfPermission(IntroViewPageAdapter.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) IntroViewPageAdapter.mContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
                    }

                    if (ContextCompat.checkSelfPermission(IntroViewPageAdapter.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) IntroViewPageAdapter.mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                    }
                    if (position == 4) {
                        IntroViewPageAdapter.button.setOnClickListener(v3 -> {
                            if (!mBlueAdapter.isEnabled()) {
                                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(intent, REQUEST_ENABLE_BT);
                            } else {
                                myDialog.setContentView(R.layout.recycler);
                                find = myDialog.findViewById(R.id.searching);
                                find.setOnClickListener(v2 -> {
                                    if (mBlueAdapter.isDiscovering() || find.getText().equals("Cancel")) {
                                        mBlueAdapter.cancelDiscovery();
                                        find.setText("Find Your Device");
                                    } else {
                                        mBlueAdapter.startDiscovery();
                                        find.setText("Cancel");
                                        Toast.makeText(IntroViewPageAdapter.mContext, "Make sure your device is on", Toast.LENGTH_SHORT).show();
                                        mData.clear();
                                        findPairedDevices();
                                    }
                                });
                                ImageView ret = myDialog.findViewById(R.id.back_Arrow);
                                ret.setOnClickListener(v1 -> myDialog.dismiss());
                                Window window = myDialog.getWindow();
                                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                                btRecycle = myDialog.findViewById(R.id.listOfBt);
                                mData = new ArrayList<>();
//                                mData.add(new BtDevice("HC-06:1234"));
                                //mData.add(new BtDevice("HC-06:1234"));
                                btRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                btAdapter = new BtAdapter(IntroViewPageAdapter.mContext, mData, (BtAdapter.OnDeviceListener) IntroViewPageAdapter.mContext);
                                btRecycle.setAdapter(btAdapter);
                                myDialog.show();
                            }
                        });
                    }
                }
//                Intent mainActivity = new Intent(getApplicationContext(), bluetoothActivity.class);
//                startActivity(mainActivity);
//                savePrefsData();
//                finish();

            if (position == 1) {
                IntroViewPageAdapter.terms.setChecked(restoreCheckData());
                if (restoreCheckData()) {
                    btnNext.setTextColor(Color.parseColor("#4c84ff"));
                    btnNext.setEnabled(true);
                } else {
                    btnNext.setTextColor(Color.parseColor("#b5cbff"));
                    btnNext.setEnabled(false);
                }
                IntroViewPageAdapter.terms.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    saveCheckData(isChecked);
                    if (isChecked) {
                        btnNext.setTextColor(Color.parseColor("#4c84ff"));
                        btnNext.setEnabled(true);
                    } else {
                        btnNext.setTextColor(Color.parseColor("#b5cbff"));
                        btnNext.setEnabled(false);
                    }
                });
            } else {
                btnNext.setTextColor(Color.parseColor("#4c84ff"));
            }

            if (position == 4) {
                IntroViewPageAdapter.button.setOnClickListener(v3 -> {
                    if (!mBlueAdapter.isEnabled()) {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BT);
                    } else {
                        myDialog.setContentView(R.layout.recycler);
                        find = myDialog.findViewById(R.id.searching);
                        find.setOnClickListener(v2 -> {
                            if (mBlueAdapter.isDiscovering() || find.getText().equals("Cancel")) {
                                mBlueAdapter.cancelDiscovery();
                                find.setText("Find Your Device");
                            } else {
                                mBlueAdapter.startDiscovery();
                                find.setText("Cancel");
                                Toast.makeText(IntroViewPageAdapter.mContext, "Make sure your device is on", Toast.LENGTH_SHORT).show();
                                mData.clear();
                                findPairedDevices();
                            }
                        });
                        ImageView ret = myDialog.findViewById(R.id.back_Arrow);
                        ret.setOnClickListener(v1 -> myDialog.dismiss());
                        Window window = myDialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        btRecycle = myDialog.findViewById(R.id.listOfBt);
                        mData = new ArrayList<>();
                        mData.add(new BtDevice("HC-06:1234"));
                        btRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        btAdapter = new BtAdapter(this, mData, this);
                        btRecycle.setAdapter(btAdapter);
                        myDialog.show();
                    }
                });
            }
        });

        btnBack.setOnClickListener(v -> {
            position = screenPager.getCurrentItem();
            btnNext.setVisibility(View.VISIBLE);
            if (position > 0 || position == tracker-1) {
                load = false;
                position--;
                if (position == 0) {
                    btnBack.setVisibility(View.INVISIBLE);
                } else if (position == 3) {
                    IntroViewPageAdapter.start.setVisibility(View.INVISIBLE);
                    IntroViewPageAdapter.learnApp.setVisibility(View.VISIBLE);
                    btnNext.setText("Skip");
                } else {
                    btnNext.setText("Next");
                }

                if (position == 1) {
                    IntroViewPageAdapter.terms.setChecked(restoreCheckData());
                    if (restoreCheckData()) {
                        btnNext.setTextColor(Color.parseColor("#4c84ff"));
                        btnNext.setEnabled(true);
                    } else {
                        btnNext.setTextColor(Color.parseColor("#b5cbff"));
                        btnNext.setEnabled(false);
                    }
                    IntroViewPageAdapter.terms.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        saveCheckData(isChecked);
                        if (isChecked) {
                            btnNext.setTextColor(Color.parseColor("#4c84ff"));
                            btnNext.setEnabled(true);
                        } else {
                            btnNext.setTextColor(Color.parseColor("#b5cbff"));
                            btnNext.setEnabled(false);
                        }
                    });
                } else {
                    btnNext.setTextColor(Color.parseColor("#4c84ff"));
                }

                btnNext.setTextColor(Color.parseColor("#4c84ff"));
                btnNext.setEnabled(true);
                addDotsIndicator(position);
                screenPager.setCurrentItem(position);
            } 
        });

//        btnGetStarted.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                position = screenPager.getCurrentItem();
//                if (position < mList.size()) {
//                    position++;
//                    screenPager.setCurrentItem(position);
//                }
//
//                if (position == mList.size()-1) {
//                    loadLastScreen();
//                Intent mainActivity = new Intent(getApplicationContext(), bluetoothActivity.class);
//                startActivity(mainActivity);
//
//                savePrefsData();
//                finish();
//            }
//        });
    }

    private void addDotsIndicator(int i) {
        if (mDots.length > 0) {
            mDots[i].setTextColor(Color.parseColor("#00B0F0"));
            for (int j = 0; j < mDots.length; j++) {
                if (j != i && j != 5) {
                    mDots[j].setTextColor(Color.parseColor("#cccccc"));
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                mBlueAdapter.startDiscovery();
                find.setText("Cancel");
                findPairedDevices();
            } else {
                toast = Toast.makeText(this, "Unable to turn on Bluetooth", Toast.LENGTH_SHORT);
                setToast();
                mBlueAdapter.cancelDiscovery();
            }
        }
    }

    private void findPairedDevices() {
        Set<BluetoothDevice> bluetoothSet = mBlueAdapter.getBondedDevices();
        if (bluetoothSet.size() > 0) {
            for (BluetoothDevice ignored : bluetoothSet) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                this.registerReceiver(receiver, filter);
                IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                this.registerReceiver(receiver, filter1);
                IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                this.registerReceiver(receiver, filter2);
            }
        } else {
            Toast.makeText(this, "No Devices Found", Toast.LENGTH_SHORT).show();
            mBlueAdapter.cancelDiscovery();
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceAddress = device.getAddress();
                if (device.getName() == null) {
                    mData.add(new BtDevice("Unknown Device:" + deviceAddress));
                }
                else {
                    mData.add(new BtDevice(device.getName() + ":" + deviceAddress));
                }
                HashSet<BtDevice> hashSet = new HashSet<>(mData);
                mData.clear();
                mData.addAll(hashSet);
                btAdapter.notifyDataSetChanged();
                btAdapter = new BtAdapter(context, mData, (BtAdapter.OnDeviceListener) context);
                btRecycle.setAdapter(btAdapter);
                btRecycle.setLayoutManager(new LinearLayoutManager(context));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()) && mBlueAdapter.isEnabled()) {
                find.setText("Find Your Device");
            }
        }
    };

    public void setToast() {
        toast.setGravity(Gravity.BOTTOM, 0, 180);
        toast.show();
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        open = pref.getBoolean("isIntroOpen", false);
        return open;
    }

    private void savePrefsData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isIntroOpen", true);
        editor.apply();
    }

    private void saveCheckData(boolean chek) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("agree", chek);
        editor.apply();
    }

    private boolean restoreCheckData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean noAgree = pref.getBoolean("agree", false);
        return noAgree;
    }

    private void loadLastScreen() {
        load = true;
        btnNext.setText("Skip");
        btnBack.setVisibility(View.VISIBLE);
//        btnGetStarted.setAnimation(btnAnim);
    }

    @Override
    public void onDeviceClick(int position) {
        correct = mData.get(position).getDevice();
        ShowPopUp();
        mBlueAdapter.cancelDiscovery();
        find.setText("Find Your Device");
        addy = mData.get(position).getAddress();
    }

    public void ShowPopUp() {
        otherDialog = new Dialog(this);
        otherDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otherDialog.setCanceledOnTouchOutside(true);
        otherDialog.setCancelable(true);
        otherDialog.setContentView(R.layout.popup);
        Button yes = otherDialog.findViewById(R.id.ok);
        Button no = otherDialog.findViewById(R.id.no);
        TextView connect = otherDialog.findViewById(R.id.connection);
        connect.setText("Connect to " + correct + "?");
        no.setOnClickListener(v -> otherDialog.dismiss());
        yes.setOnClickListener(v -> {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("address", addy);
            bundle.putString("name", correct);
            mainActivity.putExtras(bundle);
            startActivity(mainActivity);

            savePrefsData();
            finish();
        });
        otherDialog.show();
    }
}