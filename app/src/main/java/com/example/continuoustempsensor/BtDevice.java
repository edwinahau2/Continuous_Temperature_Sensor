package com.example.continuoustempsensor;

import android.bluetooth.BluetoothDevice;

public class BtDevice {
    String Device;

    public BtDevice(String device) {
        Device = device;
    }

    public void setDevice(String device) {
        this.Device = device;
    }

    public String getDevice() {
        int i = Device.indexOf(":");
        return Device.substring(0, i);
    }

    public String getAddress(){
        int i = Device.indexOf(":");
        return Device.substring(i+1);
    }
}
