package com.android.terminalbox.rs485.humiture;

public class HumitureCmd {
    public static final byte CMD_READ=0x03;
    public static final byte CMD_READ_ERR= (byte) 0x83;
    public static final short MODBUS_TEMPERATURE=0x0000;
    public static final short MODBUS_HUMIDITY=0x0001;

    //01H 03H 00H 00H 00H 01H 22H C7H
    //01H 03H 02H 00H 28H B8H A5H
}
