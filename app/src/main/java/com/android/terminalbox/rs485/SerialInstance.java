package com.android.terminalbox.rs485;

import android.content.Context;

import cn.shorr.serialport.SerialPortConfig;
import cn.shorr.serialport.SerialPortUtil;
import cn.shorr.serialport.SerialRead;

public class SerialInstance {
    private static SerialPortUtil serialPortUtil;
    private static SerialRead serialRead;
    public static SerialPortUtil getSerialPortUtilInstance(Context context, String serialPort, int baudrate) {
        if (serialPortUtil != null) {
            serialPortUtil.bindService();
            serialPortUtil=null;
        }
        SerialPortConfig serialPortConfig=new SerialPortConfig(serialPort,baudrate);
        serialPortUtil = new SerialPortUtil(context,serialPortConfig);
        return serialPortUtil;
    }
    public static SerialRead getSerialReadInstance(Context context) {
        if(serialRead!=null){
            serialRead.unRegisterListener();
            serialRead=null;
        }
        serialRead = new SerialRead(context);
        return serialRead;
    }
}
