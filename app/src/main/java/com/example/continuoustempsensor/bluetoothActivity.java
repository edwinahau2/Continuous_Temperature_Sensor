package com.example.continuoustempsensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class bluetoothActivity extends AppCompatActivity {

    RecyclerView btRecycle;
    Dialog myDialog;
    BtAdapter btAdapter;
    List<BtDevice> mData;
    private BluetoothAdapter mBlueAdapter;
    private static final int REQUEST_CODE = 1;
    Button find;
    Button yes;
    Button no;
    private static final int REQUEST_ENABLE_BT = 0;
    Toast toast;
    TextView connect;
    String correct;
    String addy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();
        }
        setContentView(R.layout.activity_bluetooth);
        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        btRecycle = findViewById(R.id.bt_list);
        find = findViewById(R.id.find);
        mData = new ArrayList<>();
        mData.add(new BtDevice("HC-06:1234"));
//        btAdapter = new BtAdapter(this, mData, this);
        btRecycle.setAdapter(btAdapter);
        btRecycle.setLayoutManager(new LinearLayoutManager(this));
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        find.setOnClickListener(v -> {
            Handler handler = new Handler();
            Runnable checkSettings = () -> {
                Intent i = new Intent(bluetoothActivity.this, bluetoothActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            };
            startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 0);
            handler.postDelayed(checkSettings, 120000);
//        if (!mBlueAdapter.isEnabled()) {
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, REQUEST_ENABLE_BT);
//        } else {
//            if (mBlueAdapter.isDiscovering()) {
//                mBlueAdapter.cancelDiscovery();
//                find.setText("Find Devices");
//            } else {
//                mBlueAdapter.startDiscovery();
//                find.setText("Cancel");
//                mData.clear();
//                findPairedDevices();
//            }
//        }
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return pref.getBoolean("isBtOpen", false);
    }

//    @SuppressLint("ShowToast")
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ENABLE_BT) {
//            if (resultCode == RESULT_OK) {
//                mBlueAdapter.startDiscovery();
//                find.setText("Cancel");
//                findPairedDevices();
//            } else {
//                toast = Toast.makeText(this, "Unable to turn on Bluetooth", Toast.LENGTH_SHORT);
//                setToast();
//                mBlueAdapter.cancelDiscovery();
//            }
//        }
//    }

//    private void findPairedDevices() {
//        Set<BluetoothDevice> bluetoothSet = mBlueAdapter.getBondedDevices();
//        if (bluetoothSet.size() > 0) {
//            for (BluetoothDevice device : bluetoothSet) {
//                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//                this.registerReceiver(receiver, filter);
//                IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//                this.registerReceiver(receiver, filter1);
//                IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//                this.registerReceiver(receiver, filter2);
//            }
//        }
//    }

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
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(intent.getAction())) {
//                toast = Toast.makeText(getActivity(), "Finding Devices...", Toast.LENGTH_SHORT);
//                setToast();
//                spinner.setVisibility(View.VISIBLE);
//                mLevel = 0;
//                changeImageView(getView());
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()) && mBlueAdapter.isEnabled()) {
                find.setText("Find Devices");
            }
        }
    };

//    public void setToast() {
//        toast.setGravity(Gravity.BOTTOM, 0, 180);
//        toast.show();
//    }

//    @SuppressLint("ShowToast")
//    @Override
//    public void onDeviceClick(int position) {
//        correct = mData.get(position).getDevice();
//        ShowPopUp();
//        mBlueAdapter.cancelDiscovery();
//        find.setText("Find Devices");
//        addy = mData.get(position).getAddress();
//    }

    public void ShowPopUp() {
        myDialog.setContentView(R.layout.popup);
        yes = myDialog.findViewById(R.id.ok);
        no = myDialog.findViewById(R.id.no);
        connect = myDialog.findViewById(R.id.connection);
        connect.setText("Connect to " + correct + "?");
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("address", addy);
                bundle.putString("name", correct);
                mainActivity.putExtras(bundle);
                startActivity(mainActivity);

                savePrefsData();
                finish();
            }
        });
        myDialog.show();
    }

    private void savePrefsData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isBtOpen", true);
        editor.apply();
    }
}
