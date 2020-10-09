package com.example.continuoustempsensor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Collections;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class fragment_tab3 extends Fragment {
    private Callback mCallback;
    private static final int CHANGE_LEVEL = 99;
    private static final int REQUEST_CODE = 1;
    public static final int RESPONSE_MESSAGE = 10;
    private StringBuilder recDataString = new StringBuilder();
    private ToggleButton button;
    private CheckBox enable, hide;
    Toast toast;
    private Spinner dropdown;
    private static final String[] options = {"30 mins", "1 hour", "2 hours", "3 hours", "6 hours", "12 hours", "24 hours"};
    private static final int RESULT_OK = -1;
    private int mLevel;
    private Button buttonFind, f, c, connect;
    private BluetoothAdapter mBlueAdapter;
    private TextView response, notify;
    ImageView spinner;
    private static final int REQUEST_ENABLE_BT = 0;
    TextView paired;
    ListView scanListView;
    ArrayList scanDeviceList;
    ArrayAdapter<String> mDeviceListAdapter;
    BluetoothSocket mmSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public Handler mHandler;
    public Handler imHandler;
    ConnectedThread btt = null;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    ArrayList<Float> tempVals = new ArrayList<Float>();
    public String tf;
    private String symbol;
    private boolean check;
    private boolean isImage = false;
    private ClipDrawable mClipDrawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        button = view.findViewById(R.id.mBlueIv);
        spinner = view.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        mClipDrawable = (ClipDrawable) spinner.getDrawable();
        mClipDrawable.setLevel(0);
//        imHandler.post(animateImage);
        paired = view.findViewById(R.id.pairedDevices);
        response = view.findViewById(R.id.response);
        paired.setVisibility(View.GONE);
        buttonFind = view.findViewById(R.id.pairedBtn);
        f = view.findViewById(R.id.fahrenheit);
        c = view.findViewById(R.id.celsius);
        dropdown = view.findViewById(R.id.spinner);
        enable = view.findViewById(R.id.enable);
        hide = view.findViewById(R.id.hide);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        connect = view.findViewById(R.id.connect);
        notify = view.findViewById(R.id.notify);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        scanDeviceList = new ArrayList();
        scanListView = view.findViewById(R.id.scanListView);
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
            @Override
            public void onClick(View v) {
                f.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#309ae6")));
                c.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e0e0e0")));
                symbol = " °F";
                mCallback.messageFromBt(tf, check, symbol);
            }
        });

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#309ae6")));
                f.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e0e0e0")));
                symbol = " °C";
                mCallback.messageFromBt(tf, check, symbol);
            }
        });

        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notify.setTextColor(Color.parseColor("#000000"));
                } else {
                    notify.setTextColor(Color.parseColor("#ccc8c8"));
                }
            }
        });

        hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check = hide.isChecked();
                if (check) {
                    mCallback.messageFromBt(tf, true, symbol);
                } else {
                    mCallback.messageFromBt(tf, false, symbol);
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
                }
                else {
                    if (mBlueAdapter.isDiscovering()) {
                        mBlueAdapter.cancelDiscovery();
                        buttonFind.setText("Find Devices");
                    }
                    else {
                        mDeviceListAdapter.clear();
                        mBlueAdapter.startDiscovery();
                        buttonFind.setText("Cancel");
                        findPairedDevices();
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
                deviceName = deviceName.substring(0, i);
                BluetoothDevice mDevice = mBlueAdapter.getRemoteDevice(deviceAddress);
                if (mmSocket == null || !mmSocket.isConnected()) {
                    BluetoothSocket tmp;
                    try {
                        tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                        mmSocket = tmp;
                        mmSocket.connect();
                        connect.setText(deviceName);
                    } catch (IOException e) {
                        try {
                            mmSocket.close();
                        } catch (IOException c) {
                        }
                    }

                    mHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == RESPONSE_MESSAGE) { // repeat everytime data is transmitted
                                String readMessage = (String) msg.obj;
                                recDataString.append(readMessage);
                                int endOfLineIndex = recDataString.indexOf("~");
                                if (endOfLineIndex > 0) // if there still exists values to be read
                                {
                                    String dataInPrint = recDataString.substring(0, endOfLineIndex); // extract substring with hashtag
                                    if (recDataString.charAt(0) == '#')
                                    {
                                        String sensor = recDataString.substring(1, endOfLineIndex); // remove hashtag
                                        float sensorVal =  Float.parseFloat(sensor);
                                        response.setText(sensor); // sends data to settings

                                        tempVals.add(sensorVal);
                                        boolean legit = TRUE;
                                        if (tempVals.size()>60){
                                            double min = Collections.min(tempVals);
                                            double max = Collections.max(tempVals);
                                            double total =0;
                                            for(int i=0;i<tempVals.size();i++)
                                            {
                                                total+=tempVals.get(i);
                                            }
                                            double mean = total/tempVals.size();
                                            double total2 =0;
                                            for (int i=0;i<tempVals.size();i++)
                                            {
                                                total2 += Math.pow((i - mean), 2);
                                            }
                                            double std = Math.sqrt( total2 / ( tempVals.size() - 1 ) );
                                            double gLower = (mean - min)/std;
                                            double gUpper = (max-mean)/std;
                                            if(gLower > 3.0269 || gUpper >3.0369){
                                                // There's an outlier
                                                legit = FALSE;
                                            }
                                            if(std*std > 0.50){
                                                //Too much variance
                                                legit =FALSE;
                                            }
                                        }
                                        if(legit) {
                                            Collections.sort(tempVals);
                                            double medianTemp;
                                            if (tempVals.size() % 2 == 0)
                                            {
                                                medianTemp = ((double)tempVals.get(tempVals.size()/2) + (double)tempVals.get(tempVals.size()/2 - 1))/2;
                                            }
                                            else {
                                                medianTemp = (double) tempVals.get(tempVals.size()/2);
                                            }
                                            String finalTemp = Double.toString(medianTemp);
                                            mCallback.messageFromBt(finalTemp, check, symbol); // sends data to home page
                                        }
                                    }
                                    recDataString.delete(0, recDataString.length());
                                    dataInPrint = "";
                                }
                            }
                        }
                    };
                    btt = new ConnectedThread(mmSocket);
                    btt.start();
                }
                else {
                    try {
                        mmSocket.close();
                        mmSocket = null;
                        mmInStream = null;
                        mmOutStream = null;
                        connect.setText("Not Connected");
                    } catch (IOException e) {}
                }
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mmSocket != null) {
                    try {
                        mmSocket.close();
                        mmSocket = null;
                        mmInStream = null;
                        mmOutStream = null;
                        connect.setText("Not Connected");
                    } catch (IOException e) {}
                }
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

    private class ConnectedThread extends Thread {

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            BufferedReader br;
            br = new BufferedReader(new InputStreamReader(mmInStream));
            while (true) {
                try {
                    String resp = br.readLine();
                    Message msg = new Message();
                    msg.what = RESPONSE_MESSAGE;
                    msg.obj = resp;
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
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
               paired.setVisibility(View.VISIBLE);
               scanDeviceList.add(deviceName + ": " + deviceAddress);
               mDeviceListAdapter.notifyDataSetChanged();
               onStart();
           }
        }
    }

    public void setToast() {
        toast.setGravity(Gravity.BOTTOM, 0, 180);
        toast.show();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        public void messageFromBt(String sensor, Boolean b, String symbol);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBlueAdapter.cancelDiscovery();
        spinner.setVisibility(View.GONE);
        requireActivity().unregisterReceiver(receiver);
    }
}