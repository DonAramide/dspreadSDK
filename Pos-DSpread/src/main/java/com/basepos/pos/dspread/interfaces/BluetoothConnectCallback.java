package com.basepos.pos.dspread.interfaces;

import android.bluetooth.BluetoothDevice;

public interface BluetoothConnectCallback {
    void onRequestDeviceScanFinished();

    void onDeviceFound(BluetoothDevice device);

    void onRequestQposConnected();

    void onRequestQposDisconnected();

    void onRequestNoQposDetected();
}
