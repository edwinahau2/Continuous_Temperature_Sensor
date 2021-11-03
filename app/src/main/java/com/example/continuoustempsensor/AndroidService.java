package com.example.continuoustempsensor;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.UUID;

public class AndroidService extends Service {

    BluetoothDevice mDevice;
    BluetoothAdapter mBlueAdapter;
    UUID characteristicUUID = UUID.fromString("0000bead-0000-1000-8000-00805f9b34fb");
    public final String TAG = "BTService";
    private final IBinder binder = new LocalBinder();
    private BluetoothGatt bluetoothGatt;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        AndroidService getService() {
            return AndroidService.this;
        }
    }

    public void startConnection(String address) {
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            mDevice = mBlueAdapter.getRemoteDevice(address);
            bluetoothGatt = mDevice.connectGatt(this, false, bluetoothGattCallback);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                final Intent intent = new Intent(ACTION_GATT_CONNECTED);
                sendBroadcast(intent);
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                final Intent intent = new Intent(ACTION_GATT_DISCONNECTED);
                sendBroadcast(intent);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                final Intent intent = new Intent(ACTION_GATT_SERVICES_DISCOVERED);
                sendBroadcast(intent);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt != null) {
            return bluetoothGatt.getServices();
        } else {
            return null;
        }
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBlueAdapter != null || bluetoothGatt != null) {
            bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            if (characteristicUUID.equals(characteristic.getUuid())) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    private void broadcastUpdate (final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
       if (characteristicUUID.equals(characteristic.getUuid())) {
           final byte[] data = characteristic.getValue();
           if (data != null && data.length > 0) {
               final StringBuilder stringBuilder = new StringBuilder(data.length);
               for (byte byteChar : data)
                   stringBuilder.append(String.format("%02X ", byteChar)); // ex: 1D 01 --> 0x11D
               String hexDigits = stringBuilder.toString();
               String firstHex = hexDigits.substring(4,5);
               int firstVal = Integer.parseInt(firstHex);
               String secondHex = hexDigits.substring(0,2);
               int secondVal = Character.digit(secondHex.charAt(0), 16);
               int thirdVal = Character.digit(secondHex.charAt(1), 16);
               int numericVal = (firstVal * 256) + (secondVal * 16) + (thirdVal);
               Log.d(TAG, "Temperature: " + numericVal);
               intent.putExtra(EXTRA_DATA, String.valueOf(numericVal));
               sendBroadcast(intent);
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
}
