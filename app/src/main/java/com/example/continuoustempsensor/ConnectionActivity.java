package com.example.continuoustempsensor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ConnectionActivity extends AppCompatActivity implements BtAdapter.OnDeviceListener {

    Button back;
    public static String sensor;
    Set<BluetoothDevice> combined = new HashSet<>();
    public static Context context;
    TextView status;
    TextView connect;
    Button rename;
    Dialog myDialog;
    Button find;
    RecyclerView list;
    BtAdapter btAdapter;
    private static final int REQUEST_CODE = 1;
    List<BtDevice> mData;
    private BluetoothAdapter mBlueAdapter;
    private static final int REQUEST_ENABLE_BT = 0;
    Toast toast;
    final Context c = this;
    String correct;
    public static String addy;
    Button yes;
    Button no;
    BluetoothSocket mmSocket;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    BluetoothDevice mDevice;
    MainActivity.ConnectedThread btt = null;
    Boolean spark = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_connection);
        back = findViewById(R.id.back);
        find = findViewById(R.id.pairedBtn);
        myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rename = findViewById(R.id.rename);
        status = findViewById(R.id.status);
        sensor = restoreNameData();
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!MainActivity.spark || !mBlueAdapter.isEnabled()) {
            status.setText("Not Connected");
        } else if ((MainActivity.name != null) && (sensor == null)) {
            sensor = MainActivity.name;
            status.setText("Connected to " + sensor);
        } else if (sensor != null) {
            status.setText("Connected to " + sensor);
        } else {
            status.setText("Not Connected");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        list = findViewById(R.id.list);
        mData = new ArrayList<>();
//        mData.add(new BtDevice("HC-06:1234"));
        btAdapter = new BtAdapter(this, mData, this);
        list.setAdapter(btAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBlueAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    if (mBlueAdapter.isDiscovering()) {
                        mBlueAdapter.cancelDiscovery();
                        find.setText("Find Devices");
                    } else {
                        mBlueAdapter.startDiscovery();
                        find.setText("Cancel");
                        mData.clear();
                        findPairedDevices();
                    }
                }
            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                if (spark) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
                    alertDialog.setTitle("Rename Sensor");
                    final EditText userInput = new EditText(c);
                    alertDialog.setView(userInput);
                    alertDialog.setCancelable(false).setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sensor = userInput.getText().toString();
                            status.setText("Connected to " + sensor);
                            dialog.cancel();
                            saveNameData();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                } else {
                    toast = Toast.makeText(getBaseContext(), "Please connect to a device", Toast.LENGTH_SHORT);
                    setToast();
                }
            }
        });
    }

    @SuppressLint("ShowToast")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                mBlueAdapter.startDiscovery();
                find.setText("Cancel");
                findPairedDevices();
            } else {
                toast = Toast.makeText(this, "Unable to turn on Bluetooth", Toast.LENGTH_SHORT);
                setToast();
                mBlueAdapter.cancelDiscovery();
            }
        }
    }

    private void findPairedDevices() {
        Set<BluetoothDevice> bluetoothSet = mBlueAdapter.getBondedDevices();
        if (bluetoothSet.size() > 0) {
            for (BluetoothDevice device : bluetoothSet) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                this.registerReceiver(receiver, filter);
                IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                this.registerReceiver(receiver, filter1);
                IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                this.registerReceiver(receiver, filter2);
            }
        }
    }

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
                list.setAdapter(btAdapter);
                list.setLayoutManager(new LinearLayoutManager(context));
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

    public void setToast() {
        toast.setGravity(Gravity.BOTTOM, 0, 180);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDeviceClick(int position) {
        correct = mData.get(position).getDevice();
        mBlueAdapter.cancelDiscovery();
        find.setText("Find Devices");
        addy = mData.get(position).getAddress();
        if (addy.equals(MainActivity.address) && MainActivity.mmSocket.isConnected()) {
            toast = Toast.makeText(this, "Already connected to " + correct, Toast.LENGTH_SHORT);
            setToast();
        } else {
            ShowPopUp();
        }
    }

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
                MainActivity.address = addy;
                mDevice = mBlueAdapter.getRemoteDevice(addy);
                startConnection();
            }
        });
        myDialog.show();
    }

    private void startConnection() {
        if (mmSocket == null || !mmSocket.isConnected()) {
            BluetoothSocket tmp;
            try {
                tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException c) {
                    e.printStackTrace();
                }
            }

            btt = new MainActivity.ConnectedThread(mmSocket);
            btt.start();
            myDialog.dismiss();
            status.setText("Connected to " + correct);
            sensor = correct;
            saveNameData();
        }
    }

    private void saveNameData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("connectPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("rename", sensor);
        editor.putString("address", addy);
        editor.apply();
    }

    private String restoreNameData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("connectPref", Context.MODE_PRIVATE);
        return pref.getString("rename", null);
    }

    public static String restoreTheAddy() {
        SharedPreferences pref = context.getSharedPreferences("connectPref", Context.MODE_PRIVATE);
        return pref.getString("address", null);
    }
}