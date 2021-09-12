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
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

public class fragment_tab3 extends Fragment  {

    BluetoothAdapter mBlueAdapter;
    private static final int REQUEST_CODE = 1;
    Toast toast;
    private static final int RESULT_OK = -1;
    private Button connect;
    private TextView notify;
    private static final int REQUEST_ENABLE_BT = 0;
    String sensor;
    public static Context context;
    TabLayout tempTab;
    TabLayout.Tab selectTab;

    // TODO: add activities for the three buttons under general
    /* 2) how to use sensor and app --> video(s)
       3) downloadable option */

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        context = requireContext();
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
        Button tippers = view.findViewById(R.id.tippers);
        Button feedback = view.findViewById(R.id.bugs);
        Button tutorial = view.findViewById(R.id.tutorial);
        Button privacy = view.findViewById(R.id.consent);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.dropdown_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        if (restoreNotifFreq() != null) {
            dropdown.setSelection(restoreNotifIndex());
            String[] dropdownTimes = requireContext().getResources().getStringArray(R.array.dropdown_times);
            String freq = dropdownTimes[restoreNotifIndex()];
            if (freq.contains("min")) {
                MainActivity.notifFreq = 30;
            } else if (freq.contains("hour")) {
                MainActivity.notifFreq = Integer.parseInt(freq.replace(" hours", ""))*60;
            }
        } else {
            dropdown.setSelection(0);
            MainActivity.notifFreq = 30;
        }
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] dropdownTimes = requireContext().getResources().getStringArray(R.array.dropdown_times);
                String freq = dropdownTimes[position];
                String textTimeNotify = parent.getItemAtPosition(position).toString();
                saveNotifData(textTimeNotify, position);
                if (freq.contains("min")) {
                    MainActivity.notifFreq = 30;
                } else if (freq.contains("hour")) {
                    MainActivity.notifFreq = Integer.parseInt(freq.replace(" hours", ""))*60;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                String unit;
                if (itab == 0) {
                    unit = " °F";
                } else {
                    unit = " °C";
                }
                saveUnitPref(itab);
                MainActivity.saveTempUnit(unit, requireContext());
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

        feedback.setOnClickListener(v -> {
            Intent feedbackForm = new Intent(requireContext().getApplicationContext(), FeedbackActivity.class);
            startActivity(feedbackForm);
        });

        tutorial.setOnClickListener(v -> {
            Intent tutorialActivity = new Intent(requireContext().getApplicationContext(), TutorialActivity.class);
            startActivity(tutorialActivity);
        });

        privacy.setOnClickListener(v -> {
            Intent privacyForms = new Intent(requireContext().getApplicationContext(), PrivacyFormsActivity.class);
            startActivity(privacyForms);
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
            } else {
                toast = Toast.makeText(getActivity(), "Unable to turn on Bluetooth", Toast.LENGTH_SHORT);
                setToast();
            }
        }
    }

    private void saveNameData() {
        SharedPreferences preferences = requireContext().getApplicationContext().getSharedPreferences("namePref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device", MainActivity.name);
        editor.putString("address", MainActivity.address);
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

    public static String restoreNotifFreq() {
        SharedPreferences prefs = context.getSharedPreferences("notifPref", Context.MODE_PRIVATE);
        return prefs.getString("notifFreq", null);
    }

    public static int restoreNotifIndex() {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("notifPref", Context.MODE_PRIVATE);
        return prefs.getInt("indexSelected", 0);
    }

    private void saveUnitPref(int itab) {
        SharedPreferences preferences = requireContext().getSharedPreferences("unitPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("tempDisplay", itab);
        editor.apply();
    }

    private int restoreTempDisplay() {
        SharedPreferences pref = requireContext().getSharedPreferences("unitPref", Context.MODE_PRIVATE);
        return pref.getInt("tempDisplay", 0);
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
                    connect.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners_for_not_connected));
                    setButtonColor(false);
                    SpannableString spanString = new SpannableString("Not Connected");
                    spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
                    connect.setText(spanString);
                }
            }
        }
    };

    public void setButtonColor(Boolean doit) {
        if (doit) {
            connect.setTextColor(Color.parseColor("#FFFFFF"));
            connect.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners_for_connected));
        } else {
            connect.setTextColor(Color.parseColor("#656565"));
            connect.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners_for_not_connected));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sensor = restoreNameData();
        if (!MainActivity.spark) {
            connect.setText("Not Connected");
            setButtonColor(false);
        } else if (sensor != null) {
            setButtonColor(true);
            sensor = ConnectionActivity.restoreNameData(requireContext());
            SpannableString spanString = new SpannableString("Connected to " + sensor);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            connect.setText(spanString);
            saveNameData();
        }

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        requireActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        requireActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}