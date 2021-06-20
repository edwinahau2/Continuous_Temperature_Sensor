package com.example.continuoustempsensor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

public class fragment_tab3 extends Fragment implements AdapterView.OnItemSelectedListener {

    private boolean clicked = false;
    BluetoothAdapter mBlueAdapter;
    private static final int REQUEST_CODE = 1;
    public static final int RESPONSE_MESSAGE = 10;
    Toast toast;
    private static final int RESULT_OK = -1;
    private int mLevel;
    private Button connect, tippers;
    private TextView response, notify;
    private static final int REQUEST_ENABLE_BT = 0;
    public Handler imHandler;
    private String symbol = " °F";
    private boolean check;
    private boolean isImage = false;
    private ClipDrawable mClipDrawable;
    private int key;
    boolean uhh;
    String sensor;
    private static String textTimeNotify;
    public static Context context;
    TabLayout tempTab;
    TabLayout.Tab selectTab;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        context = requireContext();
        response = view.findViewById(R.id.response);
        tempTab = view.findViewById(R.id.linear);
        selectTab = tempTab.getTabAt(restoreTempDisplay());
        selectTab.select();
        for (int i = 0; i < tempTab.getTabCount(); i++) {
            TabLayout.Tab tab = tempTab.getTabAt(i);
            if (tab != null) {
                TextView tabTextView = new TextView(requireContext());
                tab.setCustomView(tabTextView);
                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.setText(tab.getText());
                if (i == restoreTempDisplay()) {
                    tabTextView.setTypeface(Typeface.DEFAULT_BOLD);
                    tabTextView.setTextSize(23);
                    tabTextView.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
        }
        Spinner dropdown = view.findViewById(R.id.spinner);
        CheckBox enable = view.findViewById(R.id.enable);
        tippers = view.findViewById(R.id.tippers);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.dropdown_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        if (restoreNotifFreq() != null) {
            dropdown.setSelection(restoreNotifIndex());
        }
        dropdown.setOnItemSelectedListener(this);
        connect = view.findViewById(R.id.connect);
        if (!mBlueAdapter.isEnabled()) {
            connect.setText("Not Connected");
        }
        notify = view.findViewById(R.id.notify);

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        tempTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int itab = tab.getPosition();
                MainActivity.f = itab == 0;
                saveUnitPref(itab);
                TextView text = (TextView) tab.getCustomView();
                text.setTypeface(Typeface.DEFAULT_BOLD);
                text.setTextSize(23);
                text.setTextColor(Color.parseColor("#FFFFFF"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTypeface(Typeface.DEFAULT);
                text.setTextSize(16);
                text.setTextColor(Color.parseColor("#9e9e9e"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        f.setOnClickListener(v -> {
//            f.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#309ae6")));
//            c.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e0e0e0")));
//            MainActivity.f = true;
//        });
//
//        c.setOnClickListener(v -> {
//            c.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#309ae6")));
//            f.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e0e0e0")));
//            MainActivity.f = false;
//        });

        enable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                notify.setTextColor(Color.parseColor("#000000"));
            } else {
                notify.setTextColor(Color.parseColor("#ccc8c8"));
            }
        });

        connect.setOnClickListener(v -> {
            Intent connectActivity = new Intent(requireContext().getApplicationContext(), ConnectionActivity.class);
            startActivity(connectActivity);
        });

        tippers.setOnClickListener(v -> {
            Uri uriURL = Uri.parse("https://hub-tippers.ics.uci.edu/");
            Intent launch = new Intent(Intent.ACTION_VIEW, uriURL);
            startActivity(launch);
        });

        return view;

    }

    private void saveUnitPref(int itab) {
        SharedPreferences preferences = requireContext().getApplicationContext().getSharedPreferences("unitPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("tempDisplay", itab);
        editor.apply();
    }

    private int restoreTempDisplay() {
        SharedPreferences pref = requireContext().getApplicationContext().getSharedPreferences("unitPref", Context.MODE_PRIVATE);
        return pref.getInt("tempDisplay", 0);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            toast = Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT);
            setToast();
        }
        else {
            toast = Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT);
            setToast();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                toast = Toast.makeText(getActivity(), "Bluetooth is on", Toast.LENGTH_SHORT);
                setToast();
            } else {
                toast = Toast.makeText(getActivity(), "Unable to turn on Bluetooth", Toast.LENGTH_SHORT);
                setToast();
            }
        }
    }

    public void changeImageView(View view) {
        if (!isImage) {
            isImage = true;
            imHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 99) {
                        mLevel = mClipDrawable.getLevel() + 60;
                        if (mLevel >= 10000) {
                            mLevel = 0;
                        }
                        mClipDrawable.setLevel(mLevel);
                    }
                }
            };

            final CountDownTimer timer = new CountDownTimer(Integer.MAX_VALUE, 10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (mClipDrawable.getLevel() >= 10000) {
                        this.onFinish();
                        mLevel = 0;
                    } else if (clicked) {
                        mLevel = 0;
                    } else {
                        imHandler.sendEmptyMessage(99);
                    }
                }

                @Override
                public void onFinish() {
                    isImage = false;
                }
            };
            timer.start();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        textTimeNotify = parent.getItemAtPosition(position).toString();
        saveNotifData(textTimeNotify, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void saveNameData() {
        SharedPreferences preferences = requireContext().getApplicationContext().getSharedPreferences("namePref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device", ConnectionActivity.sensor);
        editor.putString("address", ConnectionActivity.addy);
        editor.putBoolean("uhh", uhh);
        editor.apply();
    }

    private void saveNotifData(String notifFreq, int indexSelected) {
        SharedPreferences preferences = requireContext().getApplicationContext().getSharedPreferences("notifPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("notifFreq", notifFreq);
        editor.putInt("indexSelected", indexSelected);
        editor.apply();
    }

    private String restoreNameData() {
        SharedPreferences pref = requireContext().getApplicationContext().getSharedPreferences("namePref", Context.MODE_PRIVATE);
        return pref.getString("device", null);
    }

    private String restoreTheAddy() {
        SharedPreferences pref = requireContext().getApplicationContext().getSharedPreferences("namePref", Context.MODE_PRIVATE);
        return pref.getString("address", null);
    }

    private Boolean restoreBool() {
        SharedPreferences pref = requireContext().getApplicationContext().getSharedPreferences("namePref", Context.MODE_PRIVATE);
        return pref.getBoolean("uhh", true);
    }

    public static String restoreNotifFreq() {
        SharedPreferences prefs = context.getSharedPreferences("notifPref", Context.MODE_PRIVATE);
        return prefs.getString("notifFreq", null);
    }

    private int restoreNotifIndex() {
        SharedPreferences prefs = requireContext().getApplicationContext().getSharedPreferences("notifPref", Context.MODE_PRIVATE);
        return prefs.getInt("indexSelected", -1);
    }

    public void setToast() {
        toast.setGravity(Gravity.BOTTOM, 0, 180);
        toast.show();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    connect.setText("Not Connected");
                    connect.setTextColor(Color.parseColor("#656565"));
                    connect.setBackgroundColor(Color.parseColor("#e3e3e3"));
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uhh = restoreBool();
        sensor = restoreNameData();
        if (uhh) {
            if (AndroidService.spark) {
                connect.setText("Connected to " + MainActivity.name);
                connect.setTextColor(Color.parseColor("#FFFFFF"));
                connect.setBackgroundColor(Color.parseColor("#4e95d4"));
            } else {
                connect.setText("Not Connected");
                connect.setTextColor(Color.parseColor("#656565"));
                connect.setBackgroundColor(Color.parseColor("#e3e3e3"));
            }
            uhh = false;
            saveNameData();
        } else {
            if (ConnectionActivity.daStatus != null) {
                connect.setText(ConnectionActivity.daStatus);
                connect.setTextColor(Color.parseColor("#FFFFFF"));
                connect.setBackgroundColor(Color.parseColor("#4e95d4"));
            } else {
                if (!AndroidService.spark) {
                    connect.setText("Not Connected");
                    connect.setTextColor(Color.parseColor("#656565"));
                    connect.setBackgroundColor(Color.parseColor("#e3e3e3"));
                } else if (ConnectionActivity.sensor != null) {
                    sensor = ConnectionActivity.sensor;
                    connect.setText("Connected to " + sensor);
                    connect.setTextColor(Color.parseColor("#FFFFFF"));
                    connect.setBackgroundColor(Color.parseColor("#4e95d4"));
                    saveNameData();
                } else if (sensor != null) {
                    connect.setText("Connected to " + sensor);
                    connect.setTextColor(Color.parseColor("#FFFFFF"));
                    connect.setBackgroundColor(Color.parseColor("#4e95d4"));
                } else {
                    connect.setText("Connected to " + MainActivity.name);
                    connect.setTextColor(Color.parseColor("#FFFFFF"));
                    connect.setBackgroundColor(Color.parseColor("#4e95d4"));
                }
            }
        }

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        requireActivity().registerReceiver(mReceiver, intentFilter);

//        if (MainActivity.address == null) {
//            MainActivity.address = restoreTheAddy();
//        }

    }

    @Override
    public void onStop() {
        super.onStop();
//        if (!mBlueAdapter.isEnabled()) {
//            connect.setText("Not Connected");
//        }

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        requireActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}