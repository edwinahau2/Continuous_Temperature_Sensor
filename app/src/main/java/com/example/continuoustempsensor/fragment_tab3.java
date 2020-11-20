package com.example.continuoustempsensor;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
    public static final String TAG = "THREE";
    //    private Callback mCallback;
    private boolean clicked = false;
    private static final int REQUEST_CODE = 1;
    public static final int RESPONSE_MESSAGE = 10;
    private StringBuilder recDataString = new StringBuilder();
    private ToggleButton button;
    private CheckBox enable, hide;
    Toast toast;
    private Spinner dropdown;
    private static final int RESULT_OK = -1;
    ArrayList<Float> tempVals = new ArrayList<Float>();
    private int mLevel;
    private Button buttonFind, f, c, connect;
    private BluetoothAdapter mBlueAdapter;
    private TextView response, notify;
    ImageView spinner;
    private static final int REQUEST_ENABLE_BT = 0;
//    TextView paired;
    ListView scanListView;
    ArrayList scanDeviceList;
    ArrayAdapter<String> mDeviceListAdapter;
    BluetoothSocket mmSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//    public Handler mHandler;
    public Handler imHandler;
    ConnectedThread btt = null;
//    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private String tf;
    private String symbol = " °F";
    private boolean check;
    private boolean isImage = false;
    private ClipDrawable mClipDrawable;
    private int key;
    boolean yes = true;
//    private OutputStream out;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        button = view.findViewById(R.id.mBlueIv);
        spinner = view.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        mClipDrawable = (ClipDrawable) spinner.getDrawable();
        mClipDrawable.setLevel(0);
//        imHandler.post(animateImage);
        response = view.findViewById(R.id.response);
        scanListView = view.findViewById(R.id.scan);
//        paired.setVisibility(View.GONE);
        buttonFind = view.findViewById(R.id.pairedBtn);
        f = view.findViewById(R.id.fahrenheit);
        c = view.findViewById(R.id.celsius);
        dropdown = view.findViewById(R.id.spinner);
        enable = view.findViewById(R.id.enable);
        hide = view.findViewById(R.id.hide);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.dropdown_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
        connect = view.findViewById(R.id.connect);
        notify = view.findViewById(R.id.notify);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        scanDeviceList = new ArrayList();
        mDeviceListAdapter = new ArrayAdapter<String>(requireActivity().getApplicationContext(), android.R.layout.simple_list_item_1, scanDeviceList);
        scanListView.setAdapter(mDeviceListAdapter);
        if (mBlueAdapter == null) {
            final AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("Bluetooth is not available on this device.");
            alertDialog.setCancelable(true);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
        }
        else {
            if (mBlueAdapter.isEnabled()) {
                button.setChecked(true);
            } else {
                button.setChecked(false);
            }
        }

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
                tf = String.valueOf(Math.random() * 10);
                symbol = " °F";
                retrieveJSON(tf, check, symbol, key);

                File file = new File(requireContext().getFilesDir(), "temp.json");
                try {
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        stringBuilder.append(line).append("\n");
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();

                    String num = stringBuilder.toString();

                    JSONObject jsonObject = new JSONObject(num);
//                    tf = jsonObject.getString("temperature");
//                    check = jsonObject.getBoolean("check");
//                    key = jsonObject.getInt("key");
//                    response.setText(tf + symbol);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
//                mCallback.messageFromBt(tf, check, symbol, key);
            }
        });

        c.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                c.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#309ae6")));
                f.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e0e0e0")));
                tf = "6";
                symbol = " °C";
                retrieveJSON(tf, check, symbol, key);

                File file = new File(requireContext().getFilesDir(), "temp.json");
                try {
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        stringBuilder.append(line).append("\n");
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();

                    String num = stringBuilder.toString();

                    JSONObject jsonObject = new JSONObject(num);
//                    tf = jsonObject.getString("temperature");
//                    check = jsonObject.getBoolean("check");
//                    key = jsonObject.getInt("key");
//                    response.setText(tf + symbol);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
//                mCallback.messageFromBt(tf, check, symbol, key);
            }
        });

        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notify.setTextColor(Color.parseColor("#000000"));
                    retrieveJSON(tf, check, symbol, key);
                } else {
                    notify.setTextColor(Color.parseColor("#ccc8c8"));
                    retrieveJSON(tf, check, symbol, key);
                }
            }
        });

        hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check = hide.isChecked();
                if (check) {
                    retrieveJSON(tf, true, symbol, key);
//                    mCallback.messageFromBt(tf, true, symbol, key);
                } else {
                    retrieveJSON(tf, false, symbol, key);
//                    mCallback.messageFromBt(tf, false, symbol, key);
                }
            }
        });


        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    button.setChecked(true);
                    if (!mBlueAdapter.isEnabled()){
                        toast = Toast.makeText(getActivity(), "Turning on Bluetooth...", Toast.LENGTH_SHORT);
                        setToast();
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BT);

                    }
                    else {
                        toast = Toast.makeText(getActivity(), "Bluetooth is already on", Toast.LENGTH_SHORT);
                        setToast();
                    }
                }
                else {
                    if (mBlueAdapter.isEnabled()){
                        toast = Toast.makeText(getActivity(), "Turning Bluetooth Off...", Toast.LENGTH_SHORT);
                        setToast();
                        mBlueAdapter.disable();
                        buttonFind.setText("Find Devices");
                        connect.setText("Not Connected");
                        spinner.setVisibility(View.GONE);
                    }
                }
            }
        });

        buttonFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!mBlueAdapter.isEnabled()) {
                    toast = Toast.makeText(getActivity(), "Turn on Bluetooth to find devices", Toast.LENGTH_SHORT);
                    setToast();
                    clicked = false;
                }
                else {
                    if (mBlueAdapter.isDiscovering()) {
                        mBlueAdapter.cancelDiscovery();
                        buttonFind.setText("Find Devices");
                        clicked = true;
                    }
                    else {
                        mDeviceListAdapter.clear();
                        mBlueAdapter.startDiscovery();
                        buttonFind.setText("Cancel");
                        findPairedDevices();
                        clicked = false;
                    }
                }
            }
        });
        scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                spinner.setVisibility(View.GONE);
                mBlueAdapter.cancelDiscovery();
                String deviceName = scanListView.getAdapter().getItem(position).toString();
                int i = deviceName.indexOf(":");
                String deviceAddress = deviceName.substring(i + 2);
                MainActivity.deviceName = deviceName.substring(0, i);
//                MainActivity.mDevice = mBlueAdapter.getRemoteDevice(deviceAddress);
//                if (MainActivity.mmSocket == null || !MainActivity.mmSocket.isConnected()) {
//                    BluetoothSocket tmp;
//                    try {
//                        tmp = MainActivity.mDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                        MainActivity.mmSocket = tmp;
//                        MainActivity.mmSocket.connect();
//                        connect.setText(MainActivity.deviceName);
//                    } catch (IOException e) {
//                        try {
//                            MainActivity.mmSocket.close();
//                        } catch (IOException c) {
//                        }
//                    }
//
//                    MainActivity.mHandler = new Handler(Looper.getMainLooper()) {
//                        @Override
//                        public void handleMessage(@NonNull Message msg) {
//                            super.handleMessage(msg);
//                            if (msg.what == RESPONSE_MESSAGE) {
//                                String readMessage = (String) msg.obj;
//                                recDataString.append(readMessage);
//                                int endOfLineIndex = recDataString.indexOf("~");
//                                if (endOfLineIndex > 0) {
//                                    String dataInPrint = recDataString.substring(0, endOfLineIndex);
//
//                                    if (recDataString.charAt(0) == '#') {
//                                        String sensor = recDataString.substring(1, endOfLineIndex);
//                                        float sensorVal =  Float.parseFloat(sensor);
//                                        tempVals.add(sensorVal);
//
//                                        boolean legit = true;
//                                        if (tempVals.size()>60){
//                                            double min = Collections.min(tempVals);
//                                            double max = Collections.max(tempVals);
//                                            double total =0;
//                                            for(int i=0;i<tempVals.size();i++)
//                                            {
//                                                total+=tempVals.get(i);
//                                            }
//                                            double mean = total/tempVals.size();
//                                            double total2 =0;
//                                            for (int i=0;i<tempVals.size();i++)
//                                            {
//                                                total2 += Math.pow((i - mean), 2);
//                                            }
//                                            double std = Math.sqrt( total2 / ( tempVals.size() - 1 ) );
//                                            double gLower = (mean - min)/std;
//                                            double gUpper = (max-mean)/std;
//                                                if(gLower > 3.0269 || gUpper >3.0369){
//                                                    // There's an outlier
//                                                    legit = false;
//                                                }
//                                                if(std*std > 0.50){
//                                                    //Too much variance
//                                                    legit =false;
//                                                }
//                                            }
//                                        if(legit) {
//                                            Collections.sort(tempVals);
//                                            double medianTemp;
//                                            if (tempVals.size() % 2 == 0)
//                                            {
//                                                medianTemp = ((double) Math.round(((tempVals.get(tempVals.size()/2) + (double)tempVals.get(tempVals.size()/2 - 1))/2) * 10) / 10.0);
//                                            }
//                                            else {
//                                                medianTemp = (double) Math.round((tempVals.get(tempVals.size()/2) * 10)/10.0);
//                                            }
//                                            tf = Double.toString(medianTemp);
//                                            key = 1;
//                                            MainActivity.temperature = tf;
////                                            retrieveJSON(tf, check, symbol, key);
////                                            mCallback.messageFromBt(tf, check, symbol, key);
//                                        }
//                                    }
//                                    recDataString.delete(0, recDataString.length());
//                                    dataInPrint = "";
//                                }
//                            }
//                        }
//                    };
//                    btt = new ConnectedThread(MainActivity.mmSocket);
//                    btt.start();
//                }
//                else {
//                    try {
//                        MainActivity.mmSocket.close();
//                        MainActivity.mmSocket = null;
//                        MainActivity.mmInStream = null;
//                        mmOutStream = null;
//                        connect.setText("Not Connected");
//                    } catch (IOException e) {}
//                }
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (MainActivity.mmSocket != null) {
//                    try {
//                        MainActivity.mmSocket.close();
//                        MainActivity.mmSocket = null;
//                        MainActivity.mmInStream = null;
//                        mmOutStream = null;
//                        connect.setText("Not Connected");
//                    } catch (IOException e) {}
//                }
            }
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

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() == null) {
                    scanDeviceList.add("Unknown Device: " + device.getAddress());
                }
                else {
                    scanDeviceList.add(device.getName() + ": " + device.getAddress());
                }
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(scanDeviceList);
                scanDeviceList.clear();
                scanDeviceList.addAll(hashSet);
                mDeviceListAdapter.notifyDataSetChanged();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                toast = Toast.makeText(getActivity(), "Finding Devices...", Toast.LENGTH_SHORT);
                setToast();
                spinner.setVisibility(View.VISIBLE);
                mLevel = 0;
                changeImageView(getView());
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()) && mBlueAdapter.isEnabled()) {
                spinner.setVisibility(View.GONE);
                buttonFind.setText("Find Devices");
            }
        }
    };

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


    private class ConnectedThread extends Thread {

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {}

//            MainActivity.mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            BufferedReader br;
//            br = new BufferedReader(new InputStreamReader(MainActivity.mmInStream));
            while (true) {
//                try {
//                    String resp = br.readLine();
//                    Message msg = new Message();
//                    msg.what = RESPONSE_MESSAGE;
//                    msg.obj = resp;
//                    MainActivity.mHandler.sendMessage(msg);
//                } catch (IOException e) {
//                    break;
//                }
            }
        }

        public void cancel() {
//            try {
//                MainActivity.mmSocket.close();
//            } catch (IOException e) {}
        }
    }

    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        requireActivity().registerReceiver(receiver, filter);
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        requireActivity().registerReceiver(receiver, filter1);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        requireActivity().registerReceiver(receiver, filter2);
    }

    private void findPairedDevices() {
        mDeviceListAdapter.clear();
        Set<BluetoothDevice> bluetoothSet = mBlueAdapter.getBondedDevices();
        if (bluetoothSet.size() > 0) {
           for (BluetoothDevice device : bluetoothSet) {
               String deviceName = device.getName();
               String deviceAddress = device.getAddress();
//               paired.setVisibility(View.VISIBLE);
               scanDeviceList.add(deviceName + ": " + deviceAddress);
               mDeviceListAdapter.notifyDataSetChanged();
               onStart();
           }
        }
    }

    public void retrieveJSON(String sensor, boolean check, String symbol, int key) {
        JSONObject object = new JSONObject();
        try {
            object.put("temperature", sensor);
            object.put("check", check);
            object.put("symbol", symbol);
            object.put("key", key);
            String userString = object.toString();
            File file = new File(requireContext().getFilesDir(), "temp.json");
            try {
                FileWriter writer = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(userString);
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            writeJSON(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void writeJSON(JSONObject object) {
        String userString = object.toString();
        File file = new File(requireContext().getFilesDir(), "temp.json");
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(userString);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        retrieveJSON(tf, check, symbol, key);
//        File file = new File(requireContext().getFilesDir(), "temp.json");
//        try {
//            FileReader fileReader = new FileReader(file);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            StringBuilder stringBuilder = new StringBuilder();
//            String line = bufferedReader.readLine();
//            while (line != null) {
//                stringBuilder.append(line).append("\n");
//                line = bufferedReader.readLine();
//            }
//            bufferedReader.close();
//            String num = stringBuilder.toString();
//            JSONObject jsonObject = new JSONObject(num);
//            tf = jsonObject.getString("temperature");
//            check = jsonObject.getBoolean("check");
//            key = jsonObject.getInt("key");
//            response.setText(tf);
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//        if (MainActivity.mmSocket != null) {
//            connect.setText(MainActivity.deviceName);
//            btt = new ConnectedThread(MainActivity.mmSocket);
//            btt.start();
//        }
//        toast = Toast.makeText(getActivity(), "onResume", Toast.LENGTH_SHORT);
//        setToast();
//        if (MainActivity.hide) {
//            hide.setChecked(true);
//        } else {
//            hide.setChecked(false);
//        }

    }

    @Override
    public void onStop() {
        super.onStop();
        retrieveJSON(tf, check, symbol, key);
//        if (MainActivity.mmSocket != null) {
//            connect.setText(MainActivity.deviceName);
//            btt = new ConnectedThread(MainActivity.mmSocket);
//            btt.start();
//        }
//        toast = Toast.makeText(getActivity(), "onStop", Toast.LENGTH_SHORT);
//        setToast();
//        MainActivity.hide = check;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBlueAdapter.cancelDiscovery();
        spinner.setVisibility(View.GONE);
        requireActivity().unregisterReceiver(receiver);
    }
}