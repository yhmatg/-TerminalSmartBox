package com.android.terminalbox.rs485.ekey;

import com.esim.rylai.smartbox.utils.CRC16;

public class EkeyPackageCmd {
    public static final byte ADDR_DEFAULT=0x01;
    public static byte[] packOpenKey(){
        return packOpenKey(ADDR_DEFAULT);
    }
    public static  byte[] packOpenKey(int keyAddr){
        return pack(keyAddr,EkeyCmd.CMD_OPEN_KEY);
    }
    public static byte[] packCheckKeyStatus()
    {
        return packCheckKeyStatus(ADDR_DEFAULT);
    }
    public static byte[] packCheckKeyStatus(int keyAddr){
        return pack(keyAddr,EkeyCmd.CMD_CHECK_STATUS);
    }

    private static byte[] pack(int keyAddr,short cmd){
        return pack(keyAddr,cmd,null);
    }
    private static  byte[] pack(int keyAddr,short cmd,byte[] data){
        int packLen=13;
        int dataLen=0;
        if(data!=null){
            dataLen=data.length;
            packLen+=dataLen;
        }
        byte[] bytes=new byte[packLen];
        bytes[0]=EkeyCmd.PACK_SEPARATION;
        bytes[1]=(byte)keyAddr;
        bytes[2]=(byte)keyAddr;
        bytes[3]=(byte)0x00;
        bytes[4]=(byte)0x00;

        bytes[5]=(byte)((2+dataLen)>>8 & 0xFF);
        bytes[6]=(byte)((2+dataLen) & 0xFF);
        bytes[7]=(byte)(cmd>>8 & 0xFF);
        bytes[8]=(byte)(cmd & 0xFF);
        if(dataLen!=0){
            for(int i=0;i<dataLen;i++) {
                bytes[9+i]=data[i];
            }
        }
        byte[] crc= CRC16.getInstance().getBytesCrc(bytes,1,8+dataLen);
        bytes[9+dataLen]=crc[0];
        bytes[10+dataLen]=crc[1];
        bytes[11+dataLen]=(byte) 0x7E;
        bytes[12+dataLen]=(byte) 0x7E;
        return bytes;
    }
}
