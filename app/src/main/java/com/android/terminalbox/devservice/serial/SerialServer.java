package com.android.terminalbox.devservice.serial;

import android.content.Context;

import com.android.terminalbox.app.BaseApplication;
import com.android.terminalbox.common.AppProp;
import com.android.terminalbox.utils.box.FormatUtil;
import cn.shorr.serialport.SerialPortConfig;
import cn.shorr.serialport.SerialPortFinder;
import cn.shorr.serialport.SerialPortUtil;
import cn.shorr.serialport.SerialRead;
import cn.shorr.serialport.SerialWrite;

public class SerialServer {
    private static final String TAG = "SerialServer";
    private static SerialServer serialServer;
    private static SerialRead serialRead;
    private static SerialPortUtil serialPortUtil;

    private static Context mContext;
    private SerialRead.ReadDataListener readDataListener;
    public static SerialServer getInstance() {
        if (serialServer == null) {
            synchronized (SerialServer.class) {
                if (serialServer == null) {
                    serialServer = new SerialServer();
                    serialServer.init();
                }
            }
        }
        return serialServer;
    }
    private void init() {
        mContext = BaseApplication.getInstance();
        startSerialPortConnect();
    }
    //1.开始串口连接
    public void startSerialPortConnect() {
        try {
            SerialPortConfig serialPortConfig=new SerialPortConfig(AppProp.getInstance().getEkey_comPort(),AppProp.getInstance().getEkey_baudrate());
            serialPortUtil = new SerialPortUtil(BaseApplication.getInstance(),serialPortConfig);
            //设置为调试模式，打印收发数据
            serialPortUtil.setDebug(true);
            serialPortUtil.bindService();
            serialRead = new SerialRead(mContext);
            serialRead.registerListener(finalReadListener);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //2.设置串口读取监听
    private final SerialRead.ReadDataListener finalReadListener=new SerialRead.ReadDataListener() {
        @Override
        public void onReadData(byte[] data) {
            if(readDataListener!=null){
                readDataListener.onReadData(data);
            }
        }
    };
    public void registerListener(SerialRead.ReadDataListener val){
        readDataListener=val;
    }
    public void unRegisterListener(){
        readDataListener=null;
    }
    public static void sendData(byte[] data){
        SerialWrite.sendData(mContext, data);
    }
    public static void sendHexData(String data){
        SerialWrite.sendData(mContext, FormatUtil.hexStringToByte(data));
//        Log.d(TAG, "sendHexData: sendEkeyData"+data);
    }

    //4.停止串口连接
    public void stopSerialPortConnect() {
        serialRead.unRegisterListener();
        serialPortUtil.unBindService();
    }
    public static String[] getDeviceNames(){
        //5.获取设备所有的串口信息
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        String[] devices = serialPortFinder.getAllDevicesPath();
        return devices;
    }

}
