package com.example.continuoustempsensor;

import android.Manifest;
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
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class fragment_tab3 extends Fragment {
    private static final int REQUEST_CODE = 1;
    private static final int RESULT_OK = -1;
    private Button buttonOn, buttonOff, buttonDisc, buttonFind;
    private BluetoothAdapter mBlueAdapter;
    private TextView mStatusBlueTv;
    private ImageView mBlueIv;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    ListView scanListView;
    ArrayList mDeviceList;
    ArrayAdapter<String> mDeviceListAdapter;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_layout, container, false);
        mStatusBlueTv = view.findViewById(R.id.statusBluetoothTv);
        mBlueIv = view.findViewById(R.id.bluetoothIv);
        buttonOn = view.findViewById(R.id.btnON);
        buttonOff = view.findViewById(R.id.btnOFF);
        buttonDisc = view.findViewById(R.id.discoverableBtn);
        buttonFind = view.findViewById(R.id.pairedBtn);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        mDeviceList = new ArrayList();
        scanListView = view.findViewById(R.id.scanListView);
        mDeviceListAdapter = new ArrayAdapter<String>(requireActivity().getApplicationContext(), android.R.layout.simple_list_item_1, mDeviceList);
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
        buttonOn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!mBlueAdapter.isEnabled()){
                    Toast.makeText(getActivity(), "Turning on Bluetooth...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
                else {
                    Toast.makeText(getActivity(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mBlueAdapter.isEnabled()){
                    Toast.makeText(getActivity(), "Turning Bluetooth Off...", Toast.LENGTH_SHORT).show();
                    mBlueAdapter.disable();
                }
                else {
                    Toast.makeText(getActivity(), "Bluetooth is already off", Toast.LENGTH_SHORT).show();
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
                    mBlueAdapter.cancelDiscovery();
                    mDeviceListAdapter.clear();
                    mBlueAdapter.startDiscovery();
                    onStart();
                }
//                    findPairedDevices();
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
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK){
                    Toast.makeText(getActivity(), "Bluetooth is on", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Failed to turn on Bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + ": " + device.getAddress());
                mDeviceListAdapter.notifyDataSetChanged();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
                Toast.makeText(getActivity(), "Finding Devices...", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()) && mBlueAdapter.isEnabled()) {
                Toast.makeText(getActivity(), "Finding Devices Complete", Toast.LENGTH_SHORT).show();
            }
            scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String deviceName = scanListView.getAdapter().getItem(position).toString();
                    int i = deviceName.indexOf(":");
                    deviceName = deviceName.substring(i+2);
                    BluetoothDevice mDevice = mBlueAdapter.getRemoteDevice(deviceName);
                    Toast.makeText(getActivity(), "You clicked on " + deviceName, Toast.LENGTH_SHORT).show();
                    pairDevice(mDevice);
//                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//                    requireActivity().registerReceiver(receiver3, filter);
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
//    private void findPairedDevices() {
//        int i = 0;
//        Set<BluetoothDevice> bluetoothSet = mBlueAdapter.getBondedDevices();
//        String[] str = new String[bluetoothSet.size()];
//
//        if (bluetoothSet.size() > 0) {
//            for (BluetoothDevice device: bluetoothSet) {
//                str[i] = device.getName();
//                i++;
//            }
//            mDeviceListAdapter = new ArrayAdapter<String>(requireContext().getApplicationContext(), android.R.layout.simple_expandable_list_item_1, str);
//            scanListView.setAdapter(mDeviceListAdapter);
//        }
//    }
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

    private final BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_SHORT).show();
                }
                else if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Toast.makeText(getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Toast.makeText(getActivity(), "Unable to Connect", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getActivity(), "No Bonding!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void pairDevice(BluetoothDevice mDevice) {
        final BluetoothSocket mSocket;
        BluetoothSocket tmp = null;
        try {
            tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Socket Failed", Toast.LENGTH_SHORT).show();
        }
        mSocket = tmp;

        try {
            mSocket.connect();
        } catch (IOException connectException) {
            try {
                mSocket.close();
            } catch (IOException closeException) {
                Toast.makeText(getActivity(), "Could not close socket", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mBlueAdapter.cancelDiscovery();
        requireActivity().unregisterReceiver(receiver);
        requireActivity().unregisterReceiver(receiver2);
        requireActivity().unregisterReceiver(receiver3);
    }
}
