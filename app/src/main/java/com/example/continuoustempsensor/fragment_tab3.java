package com.example.continuoustempsensor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.JsonWriter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class fragment_tab3 extends Fragment implements AdapterView.OnItemSelectedListener {
    //    private Callback mCallback;
    private boolean clicked = false;
    BluetoothAdapter mBlueAdapter;
    private static final int REQUEST_CODE = 1;
    public static final int RESPONSE_MESSAGE = 10;
    private ToggleButton button;
    private CheckBox enable;
    Toast toast;
    private Spinner dropdown;
    private static final int RESULT_OK = -1;
    private int mLevel;
    private Button f, c, connect, tippers;
    private TextView response, notify;
    ImageView spinner;
    private static final int REQUEST_ENABLE_BT = 0;
//    TextView paired;
    public Handler imHandler;
    private String symbol = " °F";
    private boolean check;
    private boolean isImage = false;
    private ClipDrawable mClipDrawable;
    boolean uhh;
    String sensor;
    private SwitchCompat darkMode;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

//        button = view.findViewById(R.id.mBlueIv);
//        spinner = view.findViewById(R.id.progressBar);
//        spinner.setVisibility(View.GONE);
//        mClipDrawable = (ClipDrawable) spinner.getDrawable();
//        mClipDrawable.setLevel(0);
//        imHandler.post(animateImage);
        response = view.findViewById(R.id.response);
//        paired.setVisibility(View.GONE);
        f = view.findViewById(R.id.fahrenheit);
        c = view.findViewById(R.id.celsius);
        dropdown = view.findViewById(R.id.spinner);
        enable = view.findViewById(R.id.enable);
        tippers = view.findViewById(R.id.tippers);
//        hide = view.findViewById(R.id.hide);
//        hide.setChecked(restoreHide());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.dropdown_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
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

        f.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                f.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#309ae6")));
                c.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e0e0e0")));
                MainActivity.f = true;
            }
        });

        c.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                c.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#309ae6")));
                f.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e0e0e0")));
                MainActivity.f = false;
            }
        });

        enable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                notify.setTextColor(Color.parseColor("#000000"));
            } else {
                notify.setTextColor(Color.parseColor("#ccc8c8"));
            }
        });

//        hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                check = hide.isChecked();
//                //                    mCallback.messageFromBt(tf, true, symbol, key);
//                //                    mCallback.messageFromBt(tf, false, symbol, key);
//                saveHideData();
//            }
//        });

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
                button.setChecked(true);
            } else {
                toast = Toast.makeText(getActivity(), "Unable to turn on Bluetooth", Toast.LENGTH_SHORT);
                setToast();
                button.setChecked(false);
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
        String text = parent.getItemAtPosition(position).toString();
//        toast = Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT);
//        setToast();
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

    private void saveHideData() {
        SharedPreferences preferences = requireContext().getApplicationContext().getSharedPreferences("hidePref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hide", check);
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

    private Boolean restoreHide() {
        SharedPreferences prefs = requireContext().getApplicationContext().getSharedPreferences("hidePref", Context.MODE_PRIVATE);
        return prefs.getBoolean("hide", false);
    }

    public void setToast() {
        toast.setGravity(Gravity.BOTTOM, 0, 180);
        toast.show();
    }

//    public void setCallback(Callback callback) {
//        this.mCallback = callback;
//    }
//
//    public interface Callback {
//        void messageFromBt(String sensor, Boolean b, String symbol, int key);
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof Callback) {
//            mCallback = (Callback) context;
//        } else {
//            throw new RuntimeException(context.toString());
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mCallback = null;
//    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    connect.setText("Not Connected");
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
            } else {
                connect.setText("Not Connected");
            }
            uhh = false;
            saveNameData();
        } else {
            if (ConnectionActivity.daStatus != null) {
                connect.setText(ConnectionActivity.daStatus);
            } else {
                if (!AndroidService.spark) {
                    connect.setText("Not Connected");
                } else if (ConnectionActivity.sensor != null) {
                    sensor = ConnectionActivity.sensor;
                    connect.setText("Connected to " + sensor);
                    saveNameData();
                } else if (sensor != null) {
                    connect.setText("Connected to " + sensor);
                } else {
                    connect.setText("Connected to " + MainActivity.name);
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