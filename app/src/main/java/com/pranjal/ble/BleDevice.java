package com.pranjal.ble;

import android.bluetooth.BluetoothDevice;

public class BleDevice {
    private BluetoothDevice bluetoothDevice;
    private int rssi;

    public BleDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getAddress(){
        return bluetoothDevice.getAddress();
    }

    public String getName(){
        return bluetoothDevice.getName();
    }

}
