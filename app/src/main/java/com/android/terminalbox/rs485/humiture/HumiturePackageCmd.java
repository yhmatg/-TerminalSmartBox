package com.android.terminalbox.rs485.humiture;

import com.esim.rylai.smartbox.utils.CRC16A001;

public class HumiturePackageCmd {
    private static final byte ADDR_DEFAULT=0x01;
    public static byte[] packReadTemperature(byte addr){
        return pack(addr,HumitureCmd.CMD_READ,HumitureCmd.MODBUS_TEMPERATURE);
    }
    public static byte[] packReadHumidity(byte addr){
        return pack(addr,HumitureCmd.CMD_READ,HumitureCmd.MODBUS_HUMIDITY);
    }
    private static  byte[] pack(byte keyAddr,short cmd,short modbus){
        int fixLen=8;
        byte[] bytes=new byte[fixLen];
        bytes[0]= keyAddr;
        bytes[1]=(byte) cmd;
        bytes[2]=(byte)(modbus>>8 & 0xFF);
        bytes[3]=(byte) (modbus & 0xFF);
        bytes[4]=(byte)0x00;

        bytes[5]=0x01;
        byte[] dd=new byte[6];
        System.arraycopy(bytes,0,dd,0,6);
        byte[] crc2 = CRC16A001.crc16ModuleBusLow2Hi(dd);
        bytes[6]=crc2[0];
        bytes[7]=crc2[1];
        return bytes;
    }
}
