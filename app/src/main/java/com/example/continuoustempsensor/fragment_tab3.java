package com.example.continuoustempsensor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class fragment_tab3 extends Fragment {
    private static final int REQUEST_CODE = 1;
    private static final int RESULT_OK = -1;
    private Button buttonDisc, buttonFind;
    private SwitchCompat simpleSwitch;
    private BluetoothAdapter mBlueAdapter;
    private TextView mStatusBlueTv, response;
    private ProgressBar spinner;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    TextView paired, other;
    ListView scanListView;
    ArrayList scanDeviceList;
    ArrayAdapter<String> mDeviceListAdapter;
    BluetoothSocket mmSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public Handler mHandler;
    ConnectedThread btt = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        mStatusBlueTv = view.findViewById(R.id.statusBluetoothTv);
        response = view.findViewById(R.id.response);
        spinner = view.findViewById(R.id.indeterminateBar);
        spinner.setVisibility(View.GONE);
        simpleSwitch = view.findViewById(R.id.simpleSwitch);
        simpleSwitch.setShowText(true);
        simpleSwitch.setTextOff("OFF");
        paired = view.findViewById(R.id.pairedDevices);
        paired.setVisibility(View.GONE);
        buttonDisc = view.findViewById(R.id.discoverableBtn);
        buttonFind = view.findViewById(R.id.pairedBtn);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        scanDeviceList = new ArrayList();
        scanListView = view.findViewById(R.id.scanListView);
        mDeviceListAdapter = new ArrayAdapter<String>(requireActivity().getApplicationContext(), android.R.layout.simple_list_item_1, scanDeviceList);
        scanListView.setAdapter(mDeviceListAdapter);
        if (mBlueAdapter == null) {
            mStatusBlueTv.setText("Bluetooth is not available");
            mStatusBlueTv.setTextColor(Color.BLUE);

        }
        else {
            mStatusBlueTv.setText("Bluetooth is available");
            mStatusBlueTv.setTextColor(Color.BLUE);
        }
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        if (mBlueAdapter.isEnabled()) {
            simpleSwitch.setChecked(true);
        }
        else {
            simpleSwitch.setChecked(false);
        }

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    simpleSwitch.setTextOn("ON");
                    if (!mBlueAdapter.isEnabled()){
                        Toast.makeText(getActivity(), "Turning on Bluetooth...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BT);
                    }
                    else {
                        Toast.makeText(getActivity(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    simpleSwitch.setTextOff("OFF");
                    if (mBlueAdapter.isEnabled()){
                        Toast.makeText(getActivity(), "Turning Bluetooth Off...", Toast.LENGTH_SHORT).show();
                        mBlueAdapter.disable();
                        mStatusBlueTv.setText("Bluetooth is Available");
                        spinner.setVisibility(View.GONE);
                    }
                    else {
                        Toast.makeText(getActivity(), "Bluetooth is already off", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonDisc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!mBlueAdapter.isDiscovering()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                    requireActivity().registerReceiver(receiver2,filter);
                }
                else {
                    Toast.makeText(getActivity(), "Your Device is Already Discoverable", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonFind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!mBlueAdapter.isEnabled()) {
                    Toast.makeText(getActivity(), "Turn on Bluetooth to find devices", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (mBlueAdapter.isDiscovering()) {
                        mBlueAdapter.cancelDiscovery();
                        buttonFind.setText("      Find Devices");
                    }
                    else {
                        mDeviceListAdapter.clear();
                        mBlueAdapter.startDiscovery();
                        buttonFind.setText("  Stop Finding Devices");
                        findPairedDevices();
                    }
                }
            }
            });
        return view;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                simpleSwitch.setTextOn("ON");
                Toast.makeText(getActivity(), "Bluetooth is on", Toast.LENGTH_SHORT).show();
                simpleSwitch.setChecked(true);
            } else {
                simpleSwitch.setTextOn("OFF");
                Toast.makeText(getActivity(), "Failed to turn on Bluetooth", Toast.LENGTH_SHORT).show();
                simpleSwitch.setChecked(false);
            }
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            int i = 0;
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
                spinner.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Finding Devices...", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()) && mBlueAdapter.isEnabled()) {
                spinner.setVisibility(View.GONE);
                buttonFind.setText("      Find Devices");
            }
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
                    Toast.makeText(getActivity(), "You clicked on " + deviceName, Toast.LENGTH_SHORT).show();
                    if (mmSocket == null) {
                        BluetoothSocket tmp = null;
                        try {
                            tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                            mmSocket = tmp;
                            mmSocket.connect();
                            mStatusBlueTv.setText("Connected to " + deviceName);
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
                                if (msg.what == ConnectedThread.RESPONSE_MESSAGE) {
                                    String txt = (String) msg.obj;
                                    response.append("\n" + txt);
                                }
                            }
                        };

                        btt = new ConnectedThread(mmSocket, mHandler);
                        btt.start();
                    }
                    else {
                        Toast.makeText(getActivity(), "Disconnecting from " + deviceName, Toast.LENGTH_SHORT).show();
                        try {
                            mmSocket.close();
                        } catch (IOException e) {}
                        mmSocket = null;
                        mStatusBlueTv.setText("Bluetooth is Available");
                    }
                }
            });
        }
    };

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
    private final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(getActivity(), "Discoverability Enabled", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Toast.makeText(getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        mBlueAdapter.cancelDiscovery();
        spinner.setVisibility(View.GONE);
        requireActivity().unregisterReceiver(receiver);
        requireActivity().unregisterReceiver(receiver2);
    }
}