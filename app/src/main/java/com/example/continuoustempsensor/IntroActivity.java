package com.example.continuoustempsensor;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
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

import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class IntroActivity extends AppCompatActivity implements BtAdapter.OnDeviceListener {

    private CustomViewPager screenPager;
    IntroViewPageAdapter introViewPageAdapter;
    public final String TAG = "BtConnection";
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
    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning = false;
    private Handler handler = new Handler();
    private static final long SCAN_PERIOD = 20000;
    List<String> listOfAddress = new ArrayList<>();
    AndroidService mService;

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
        bluetoothLeScanner = mBlueAdapter.getBluetoothLeScanner();
        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.setCancelable(true);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Welcome", "Click Next to continue \n on your journey with TEGG", R.drawable.samueli, R.drawable.tippers_logo, 0));
        mList.add(new ScreenItem("Terms and Conditions", "", 0, 0, 1));
        mList.add(new ScreenItem("Set Up Your TEGG", "Please watch the video to learn \n how to start using your device", 0, 0, 2));
        mList.add(new ScreenItem("App Navigation", "", 0, 0, 3));
        mList.add(new ScreenItem("Connect", "Please turn on your Bluetooth", R.drawable.ic_connect, 0, 4));
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
            } else if (position == 3) {
                position++;
                addDotsIndicator(position);
                screenPager.setCurrentItem(position);
                btnNext.setVisibility(View.INVISIBLE);
                if (position == 4) {
                    IntroViewPageAdapter.button.setOnClickListener(v3 -> {
                        if (ContextCompat.checkSelfPermission(IntroViewPageAdapter.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) IntroViewPageAdapter.mContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
                        }

                        if (ContextCompat.checkSelfPermission(IntroViewPageAdapter.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) IntroViewPageAdapter.mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                        }
                        if (!mBlueAdapter.isEnabled()) {
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(intent, REQUEST_ENABLE_BT);
                        } else {
                            myDialog.setContentView(R.layout.recycler);
                            find = myDialog.findViewById(R.id.searching);
                            find.setOnClickListener(v2 -> {
                                if (!scanning) {
                                    handler.postDelayed(() -> {
                                        scanning = false;
                                        bluetoothLeScanner.stopScan(leScanCallback);
                                        find.setText("Find Your Device");
                                    }, SCAN_PERIOD);
                                    scanning = true;
                                    bluetoothLeScanner.startScan(leScanCallback);
                                    toast = Toast.makeText(getBaseContext(), "Make sure your device is on", Toast.LENGTH_SHORT);
                                    setToast();
                                    find.setText("Cancel");
                                } else {
                                    scanning = false;
                                    bluetoothLeScanner.stopScan(leScanCallback);
                                    find.setText("Find Your Device");
                                }
                            });
                            ImageView ret = myDialog.findViewById(R.id.back_Arrow);
                            ret.setOnClickListener(v1 -> myDialog.dismiss());
                            Window window = myDialog.getWindow();
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                            btRecycle = myDialog.findViewById(R.id.listOfBt);
                            mData = new ArrayList<>();
                            btRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            btAdapter = new BtAdapter(IntroViewPageAdapter.mContext, mData, (BtAdapter.OnDeviceListener) IntroViewPageAdapter.mContext);
                            btRecycle.setAdapter(btAdapter);
                            myDialog.show();
                        }
                    });
                }
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
                handler.postDelayed(() -> {
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }, SCAN_PERIOD);
                scanning = true;
                bluetoothLeScanner.startScan(leScanCallback);
                find.setText("Cancel");
                toast = Toast.makeText(getBaseContext(), "Make sure your device is on", Toast.LENGTH_SHORT);
                setToast();
            } else {
                toast = Toast.makeText(this, "Unable to turn on Bluetooth", Toast.LENGTH_SHORT);
                setToast();
            }
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = mBlueAdapter.getRemoteDevice(result.getDevice().getAddress());
            String deviceAddress = result.getDevice().getAddress();
            if (device.getName() != null) {
                if (!listOfAddress.contains(deviceAddress)) {
                    mData.add(new BtDevice(device.getName() + ":" + deviceAddress));
                    listOfAddress.add(deviceAddress);
                }
            }
            Set<BtDevice> hashSet = new LinkedHashSet<>(mData);
            mData.clear();
            mData.addAll(hashSet);
            btAdapter.notifyDataSetChanged();
            btAdapter = new BtAdapter(getApplicationContext(), mData, (BtAdapter.OnDeviceListener) IntroViewPageAdapter.mContext);
            btRecycle.setAdapter(btAdapter);
            btRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    };

    public void setToast() {
        toast.setGravity(Gravity.BOTTOM, 0, 100);
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
    }

    @Override
    public void onDeviceClick(int position) {
        correct = mData.get(position).getDevice();
        addy = mData.get(position).getAddress();
//        BluetoothDevice mDevice = mBlueAdapter.getRemoteDevice(addy);
        ShowPopUp();
        mBlueAdapter.cancelDiscovery();
        find.setText("Find Your Device");
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

            otherDialog.dismiss();
            savePrefsData();
            finish();
        });
        otherDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}