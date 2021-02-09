package com.android.terminalbox.rs485;

public interface SerialTimeoutListener {
    void onTimeout(byte[] sendCmdBody);
}
