package com.example.continuoustempsensor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.continuoustempsensor.MainActivity.*;

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
    public static boolean spark = false;
    private static final String TAG = "AndroidService";

    @Override
    public void onCreate() {
        super.onCreate();
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            address = intent.getStringExtra("address");
//            mDevice = mBlueAdapter.getRemoteDevice(address);
 //           startConnection();
        }
        return START_STICKY;
    }

    private void startConnection() {
        if (mmSocket == null || !mmSocket.isConnected()) {
            BluetoothSocket tmp;
            try {
                tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
                mmSocket.connect();
                spark = true;
            } catch (IOException e) {
                try {
                    mmSocket.close();
                    spark = false;
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

    @SuppressLint("ShortAlarm")
    @Override
    public void onTaskRemoved(Intent rootIntent) {

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.cancelAll();
        }
        ComponentName componentName = new ComponentName(getApplicationContext(), TestJobService.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("address", address);
        JobInfo jobInfo;
        jobInfo = new JobInfo.Builder(101, componentName)
                .setExtras(bundle)
                .setPersisted(false)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .setBackoffCriteria(TimeUnit.MINUTES.toMillis(20), JobInfo.BACKOFF_POLICY_LINEAR)
                .build();
        if (jobScheduler.schedule(jobInfo)==JobScheduler.RESULT_SUCCESS) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
