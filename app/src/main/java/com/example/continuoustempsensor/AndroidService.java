package com.example.continuoustempsensor;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

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
//            startConnection();

            mDevice = mBlueAdapter.getRemoteDevice(address);
            startConnection();
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

    @Override
    public void onTaskRemoved(Intent rootIntent) {

//        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        //creating a new intent specifying the broadcast receiver
//        Intent i = new Intent(this, MyAlarm.class);
//
//        //creating a pending intent using the intent
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//
//        Calendar cal = Calendar.getInstance();
//        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR)-3, cal.get(Calendar.MINUTE));
//        SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
//        long time = cal.getTimeInMillis();
//
//        //setting the repeating alarm that will be fired every day
//        am.setRepeating(AlarmManager.RTC_WAKEUP, time, 60*1000, pi);
//        Log.d("ExampleJobService", "Alarm is set at " + sdf.format(cal.getTime()));

//        ComponentName componentName = new ComponentName(getApplicationContext(), TestJobService.class);
//        PersistableBundle bundle = new PersistableBundle();
//        bundle.putString("address", address);
//        JobInfo jobInfo = new JobInfo.Builder(101, componentName)
//                .setExtras(bundle)
//                .setPersisted(false)
//                .setRequiresCharging(false)
//                .setPeriodic(TimeUnit.MINUTES.toMillis(20))
//                .setBackoffCriteria(TimeUnit.MINUTES.toMillis(5), JobInfo.BACKOFF_POLICY_LINEAR)
//                .build();
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        jobScheduler.schedule(jobInfo);
//
//        if (jobScheduler.schedule(jobInfo)==JobScheduler.RESULT_SUCCESS) {
//            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show();
//        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
