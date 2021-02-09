package com.android.terminalbox.rs485.humiture;

public interface HumitureReadListener {
    void onHumitureValue(int ekeyAddr, HumitureType humitureType, int value);
}