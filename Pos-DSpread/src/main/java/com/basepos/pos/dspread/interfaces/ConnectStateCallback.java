package com.basepos.pos.dspread.interfaces;

import android.bluetooth.BluetoothDevice;

public interface ConnectStateCallback {

    void onRequestQposConnected();

    void onRequestQposDisconnected();

    void onRequestNoQposDetected();
    
}
