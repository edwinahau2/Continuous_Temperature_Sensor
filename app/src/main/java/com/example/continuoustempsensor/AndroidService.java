package com.example.continuoustempsensor;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class AndroidService extends Service {

    static BluetoothSocket mmSocket;
    BluetoothDevice mDevice;
    BluetoothAdapter mBlueAdapter;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    ConnectedThread btt = null;
    static InputStream mmInStream;
    static Handler mHandler;
    String address;
    public static final int RESPONSE_MESSAGE = 10;

    @Override
    public void onCreate() {
        super.onCreate();
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            address = intent.getStringExtra("address");
            mDevice = mBlueAdapter.getRemoteDevice(address);
            startConnection();
        }
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return START_STICKY;
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
            btt = new ConnectedThread(mmSocket);
            btt.start();
        }
    }

    protected static class ConnectedThread extends Thread {
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        return super.stopService(name);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
