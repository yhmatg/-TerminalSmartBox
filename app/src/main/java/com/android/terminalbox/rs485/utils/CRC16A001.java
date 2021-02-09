package com.android.terminalbox.rs485.utils;


import com.esim.rylai.smartbox.utils.BytesUtils;

public class CRC16A001 {
    private static byte[] crc(byte[] bytes){
        int CRC =  0x0000ffff;
        int POLYNOMIAL =  0x0000A001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC = CRC ^ bytes[i];
            for (j = 0; j < 8; j++) {
                int right1 =CRC & 0x00000001;
                CRC = CRC >>>1;
                if (right1== 1) {
                    CRC = CRC^ POLYNOMIAL;
                }
            }
        }
        byte[] byt=new byte[2];

        byt[0]=(byte)((CRC >>>8)& 0xFF);
        byt[1]=(byte)(CRC & 0xFF);
        return byt;
    }
    private static byte[] crcLow2Hi(byte[] bytes){
        byte[] CRC = crc(bytes);
        byte hi=CRC[0];
        CRC[0]=CRC[1];
        CRC[1]=hi;
        return CRC;
    }
    public static String crc16ModuleBusHex(byte[] bytes) {
        return BytesUtils.bytesToHex(crc(bytes));
    }
    public static String crc16ModuleBusHexLow2Hi(byte[] bytes) {
        return BytesUtils.bytesToHex(crcLow2Hi(bytes));
    }
    public static byte[] crc16ModuleBus(byte[] bytes) {
        return crc(bytes);
    }
    public static byte[] crc16ModuleBusLow2Hi(byte[] bytes) {
        return crcLow2Hi(bytes);
    }
    public static void main(String[] args){
        //010300000001 840A
        //018302 C0F1 错误？
        //0103020028 B8A5
        byte[] src= BytesUtils.hexToBytes("0103020028");
        System.out.println(crc16ModuleBusHex(src));
        System.out.println(crc16ModuleBusHexLow2Hi(src));
        System.out.println(BytesUtils.bytesToHex(crc16ModuleBus(src)));
        System.out.println(BytesUtils.bytesToHex(crc16ModuleBusLow2Hi(src)));
    }
}

 



