package com.example.continuoustempsensor;

import android.Manifest;
import android.annotation.SuppressLint;
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
        enable.setChecked(restoreNotifEnable());
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
        connect.setText("Not Connected");
        setButtonColor(false);
        notify = view.findViewById(R.id.notify);
        if (restoreNotifEnable()) {
            notify.setTextColor(Color.parseColor("#000000"));
        } else {
            notify.setTextColor(Color.parseColor("#ccc8c8"));
        }
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
                Intent unitIntent = new Intent("TEMP_UNIT_CHANGED");
                unitIntent.putExtra("unit", unit);
                getActivity().sendBroadcast(unitIntent);
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
            MainActivity.notifChecked = isChecked;
            saveNotifPref(isChecked);
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

    private void saveNotifData(String notifFreq, int indexSelected) {
        SharedPreferences preferences = requireContext().getApplicationContext().getSharedPreferences("notifPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("notifFreq", notifFreq);
        editor.putInt("indexSelected", indexSelected);
        editor.apply();
    }

    private void saveNotifPref(boolean notifCheck) {
        SharedPreferences preferences = requireContext().getApplicationContext().getSharedPreferences("notifPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notifChecked", notifCheck);
        editor.apply();
    }

    public static boolean restoreNotifEnable() {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("notifPref", Context.MODE_PRIVATE);
        return pref.getBoolean("notifChecked", true);
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

    public void setButtonColor(Boolean doit) {
        if (doit) {
            SpannableString spanString = new SpannableString("Connected to " + MainActivity.name);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            connect.setText(spanString.toString());
            connect.setTextColor(Color.parseColor("#FFFFFF"));
            connect.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners_for_connected));
        } else {
            connect.setText("Not Connected");
            connect.setTextColor(Color.parseColor("#656565"));
            connect.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_corners_for_not_connected));
        }
    }

    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (("BLUETOOTH_IS_CONNECTED").equals(action)) {
                setButtonColor(true);
            } else if (("BLUETOOTH_IS_DISCONNECTED").equals(action)) {
                setButtonColor(false);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        final IntentFilter btFilter = new IntentFilter();
        btFilter.addAction("BLUETOOTH_IS_CONNECTED");
        btFilter.addAction("BLUETOOTH_IS_DISCONNECTED");
        requireActivity().registerReceiver(btReceiver, btFilter);
    }
}