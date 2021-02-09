package com.android.terminalbox.rs485.ekey;

public interface EkeyStatusListener {
        void onEkeyStatusChange(int ekeyAddr, EkeyStatusChange ekeyStatusChange);
}