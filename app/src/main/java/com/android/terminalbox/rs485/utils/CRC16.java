package com.android.terminalbox.rs485.utils;

public class CRC16 {
    private short[] crcTable = new short[256];
    private int gPloy = 0x1021; // 生成多项式
    private static CRC16 crc16;
    public static CRC16 getInstance() {
        if (crc16 == null) {
            synchronized (CRC16.class) {
                if (crc16 == null) {
                    crc16 = new CRC16();
                }
            }
        }
        return crc16;
    }
    private CRC16(){
        for (int i = 0; i < 256; i++) {
            crcTable[i] = getCrcOfByte(i);
        }
    }

    private short getCrcOfByte(int aByte) {
        int value = aByte << 8;
        for (int count = 7; count >= 0; count--) {
            if ((value & 0x8000) != 0) { // 高第16位为1，可以按位异或
                value = (value << 1) ^ gPloy;
            } else {
                value = value << 1; // 首位为0，左移
            }
        }
        value = value & 0xFFFF; // 取低16位的值
        return (short)value;
    }
    public short getShortCrc(byte[] data) {
        int crc = 0;
        int length = data.length;
        for (int i = 0; i < length; i++) {
            crc = ((crc & 0xFF) << 8) ^ crcTable[(((crc & 0xFF00) >> 8) ^ data[i]) & 0xFF];
        }
        crc = crc & 0xFFFF;
        return (short)crc;
    }
    public byte[] getBytesCrc(byte[] data) {
        int crc = 0;
        int length = data.length;
        for (int i = 0; i < length; i++) {
            crc = ((crc & 0xFF) << 8) ^ crcTable[(((crc & 0xFF00) >> 8) ^ data[i]) & 0xFF];
        }
        crc = (short)crc & 0xFFFF;
        byte[] bytes = new byte[2];
        for (int i = 1; i >= 0; i--) {
            bytes[i] = (byte)(crc % 256);
            crc >>= 8;
        }
        return bytes;
    }
    public byte[] getBytesCrc(byte[] data,int startIdx,int len) {
        int crc = 0;
        for (int i = 0; i < len; i++) {
            crc = ((crc & 0xFF) << 8) ^ crcTable[(((crc & 0xFF00) >> 8) ^ data[i+startIdx]) & 0xFF];
        }
        crc = (short)crc & 0xFFFF;
        byte[] bytes = new byte[2];
        for (int i = 1; i >= 0; i--) {
            bytes[i] = (byte)(crc % 256);
            crc >>= 8;
        }
        return bytes;
    }
}

 



