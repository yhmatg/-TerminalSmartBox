package com.android.terminalbox.old;

import android.content.Context;
import android.os.SystemClock;

import cn.shorr.serialport.SerialPortConfig;
import cn.shorr.serialport.SerialPortUtil;
import cn.shorr.serialport.SerialRead;
import cn.shorr.serialport.SerialWrite;

import com.esim.rylai.smartbox.ekey.EkeyCmdPack;
import com.esim.rylai.smartbox.ekey.EkeyDeviceStatus;
import com.esim.rylai.smartbox.ekey.EkeyFailStatusEnum;
import com.esim.rylai.smartbox.ekey.EkeyStatusChange;
import com.esim.rylai.smartbox.ekey.EkeyStatusEnum;
import com.esim.rylai.smartbox.utils.BytesUtils;
import com.esim.rylai.smartbox.utils.FormatUtil;
import com.esim.rylai.smartbox.utils.logs.DefaultLogger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EkeyManager{
    private static final String TAG = EkeyManager.class.getSimpleName();
    private static EkeyManager ekeyManager;
    private  Context mContext;
    private SerialRead mSerialRead;
    private SerialPortConfig mSerialPortConfig;
    private static SerialPortUtil serialPortUtil;
    private String mSerialPort="/dev/ttyS4";
    private int mBaudrate=9600;
    private long checkTimeout=2000;
    private static final long minInterval=500;
    private static long lastSendTime=0;
//    private static Map<Integer,EkeyStatusEnum> ekeyStatusMap;
//    private static Map<Integer,Long> ekeyLastRevTime;
    private int[] mEkeyAddrs;
    private static Map<Integer, EkeyDeviceStatus> ekeyDeviceStatusMap=new HashMap<>();
    private EkeyStatusListener mEkeyStatusChangeListener;

    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(128);
    private CheckEkeyStatusThread checkEkeyStatusThread;
//    final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private DefaultLogger slog=new DefaultLogger();
    private boolean mDebug=false;
    private OpenKeyThread openKeyThread=null;
    public static EkeyManager getInstance() {
        if (ekeyManager == null) {
            synchronized (EkeyManager.class) {
                if (ekeyManager == null) {
                    ekeyManager = new EkeyManager();
                }
            }
        }
        return ekeyManager;
    }

    public EkeyManager init(Context context,String serialPort,int baudrate){
        mContext = context;
        mSerialPort=serialPort;
        mBaudrate=baudrate;
        reInitSerial();
        return this;
    }
    public EkeyManager config(EkeyStatusListener ekeyStatusChangeListener, long checkTimeout,boolean debug,int...ekeyAddrs) {
        this.checkTimeout=checkTimeout;
        mEkeyStatusChangeListener =ekeyStatusChangeListener;
        mDebug=debug;
        ekeyDeviceStatusMap.clear();
        mEkeyAddrs=ekeyAddrs;
        reInitEkeyAddrs();
        return this;
    }
    public EkeyManager confEkeyAddrs(int... ekeyAddrs){
        mEkeyAddrs = ekeyAddrs;
        reInitEkeyAddrs();
        return this;
    }
    public EkeyManager debug(boolean isDebug){
        mDebug=isDebug;
        return this;
    }
    public EkeyManager confCheckTimeout(long checkTimeout){
        this.checkTimeout=checkTimeout;
        return this;
    }
    public EkeyManager registerListener(EkeyStatusListener ekeyStatusChangeListener){
        this.mEkeyStatusChangeListener =ekeyStatusChangeListener;
        return this;
    }

    private void reInitSerial() {
        try {
            if(serialPortUtil!=null){
                mSerialRead.unRegisterListener();
                serialPortUtil.unBindService();
                serialPortUtil=null;
            }
            if(mSerialRead!=null){
                mSerialRead.unRegisterListener();
                mSerialRead=null;
            }
            mSerialPortConfig=new SerialPortConfig(mSerialPort,mBaudrate);
            serialPortUtil = new SerialPortUtil(mContext,mSerialPortConfig);
            serialPortUtil.setDebug(mDebug);
            serialPortUtil.bindService();
            mSerialRead = new SerialRead(mContext);
            mSerialRead.registerListener(finalReadListener);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private void reInitEkeyAddrs(){
        try {
            if (checkEkeyStatusThread != null && !checkEkeyStatusThread.isInterrupted()) {
                checkEkeyStatusThread.interrupt();
                checkEkeyStatusThread = null;
            }
            ekeyDeviceStatusMap.clear();
            for (int i = 0; i < mEkeyAddrs.length; i++) {
                int addr = mEkeyAddrs[i];
                EkeyDeviceStatus ekeyDeviceStatus = new EkeyDeviceStatus(addr, 0, 0, EkeyStatusEnum.DISCONNECT);
                ekeyDeviceStatusMap.put(addr, ekeyDeviceStatus);
                if (mEkeyStatusChangeListener != null) {
                    mEkeyStatusChangeListener.onEkeyStatusChange(addr, EkeyStatusChange.TO_DISCONNECTED);
                }
            }
            checkEkeyStatusThread = new CheckEkeyStatusThread();
            checkEkeyStatusThread.start();
        }catch (Exception ex){

        }
    }
//    public void init(Context context, String serialPort, int baudrate, EkeyStatusListener ekeyStatusChangeListener, long checkTimeout, int...ekeyAddrs) {
//        slog.d(TAG, "config:====="+serialPort+"===="+baudrate);
//        mContext = context;
//        mSerialPort=serialPort;
//        mBaudrate=baudrate;
//        this.checkTimeout=checkTimeout;
//        try {
//            mSerialPortConfig=new SerialPortConfig(mSerialPort,mBaudrate);
//            serialPortUtil = new SerialPortUtil(mContext,mSerialPortConfig);
//            //设置为调试模式，打印收发数据
//            serialPortUtil.setDebug(false);
//            serialPortUtil.bindService();
//
//            mSerialRead = new SerialRead(mContext);
//            mSerialRead.registerListener(finalReadListener);
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//        mEkeyStatusChangeListener =ekeyStatusChangeListener;
//        ekeyDeviceStatusMap=new HashMap<>();
////        ekeyLastRevTime=new HashMap<>();
//        final Long initTime = 0l;
//        for(int i=0;i<ekeyAddrs.length;i++){
//            int addr=ekeyAddrs[i];
//            EkeyDeviceStatus ekeyDeviceStatus=new EkeyDeviceStatus(addr,0,0,EkeyStatusEnum.DISCONNECT);
//            ekeyDeviceStatusMap.put(addr,ekeyDeviceStatus);
//            if(mEkeyStatusChangeListener!=null) {
//                mEkeyStatusChangeListener.onEkeyStatusChange(addr, EkeyStatusChange.TO_DISCONNECTED);
//            }
//        }
//        checkEkeyStatusThread=new CheckEkeyStatusThread();
//        executorService.submit(checkEkeyStatusThread);
//    }
    public EkeyStatusEnum getEkeyDeviceStatus(int ekeyAddr){
        EkeyDeviceStatus ekeyDeviceStatus = ekeyDeviceStatusMap.get(ekeyAddr);
        if(ekeyDeviceStatus!=null){
            return ekeyDeviceStatus.getEkeyStatusEnum();
        }
        return null;
    }
    public void openEkey(){
          openEkey(EkeyCmdPack.ADDR_DEFAULT);
    }
    public void openEkey(int keyAddr){
        if(openKeyThread!=null && !openKeyThread.isInterrupted()){
            openKeyThread.interrupt();
        }
        openKeyThread=new OpenKeyThread(keyAddr);
        openKeyThread.start();
    }
    public void openEkey(EkeyStatusListener ekeyStatusListener){
        openEkey(EkeyCmdPack.ADDR_DEFAULT,ekeyStatusListener);
    }
    public void openEkey(int keyAddr,EkeyStatusListener ekeyStatusListener){
        this.mEkeyStatusChangeListener=ekeyStatusListener;
        if(openKeyThread!=null && !openKeyThread.isInterrupted()){
            openKeyThread.interrupt();
        }
        openKeyThread=new OpenKeyThread(keyAddr);
        openKeyThread.start();
    }

    private final SerialRead.ReadDataListener finalReadListener=new SerialRead.ReadDataListener() {
        @Override
        public void onReadData(byte[] data) {
            try {
                synchronized (this) {
                    mReadBuffer.put(data);
                }
                int startIdx = -1;
                int endIdx = -1;
                boolean isFindEnd = false;
                for (int pos = 0; pos < mReadBuffer.position() - 1; pos++) {
                    if (mReadBuffer.get(pos) == EkeyCmdPack.PACK_HEADER && mReadBuffer.get(pos + 1) != EkeyCmdPack.PACK_HEADER) {
                        startIdx = pos;
                    }
                    if (mReadBuffer.get(pos) == EkeyCmdPack.PACK_HEADER && mReadBuffer.get(pos + 1) == EkeyCmdPack.PACK_HEADER) {
                        endIdx = pos + 1;
                        isFindEnd = true;
                    }
                    if (isFindEnd) {
                        mReadBuffer.flip();
                        if (startIdx != 0) {
                            byte[] discard = new byte[startIdx];
                            mReadBuffer.get(discard);
                        }
                        byte[] onePack = new byte[endIdx + 1 - startIdx];
                        mReadBuffer.get(onePack);
                        slog.d(TAG, "onNewData: "+ FormatUtil.bytesToHexString(onePack));
                        new HeadlePackThread(onePack).start();
//                            headlePack(onePack);
                        mReadBuffer.compact();
                        pos = -1;
                        isFindEnd = false;
                    }
                }

            }catch (Exception ex){
                mReadBuffer.clear();
                slog.d(TAG,ex.toString());
            }
        }
    };
    private class HeadlePackThread extends Thread{
        private byte[] pack;

        public HeadlePackThread(byte[] pack) {
            this.pack = pack;
        }

        @Override
        public void run(){
            headlePack(pack);
        }
    }
    private void headlePack(byte[] pack){
        if(pack.length<10){
            return;
        }
        EkeyStatusChange ekeyStatusChange=null;
        int addr=pack[1];
        if(!ekeyDeviceStatusMap.containsKey(addr)){
            return;
        }
        long revTime=SystemClock.elapsedRealtime();
        ekeyDeviceStatusMap.get(addr).setEkeyLastRevTime(revTime);
        EkeyStatusEnum mEkeyPreStatus=ekeyDeviceStatusMap.get(addr).getEkeyStatusEnum();
       /* if (pack[8] == 0x05 && pack[9]==0x01){
            ekeyStatusChange=EkeyStatusChange.READY_TO_OPENED;
        }*/
        if (pack[8] == 0x16 && pack[9]==0x00){
            if(mEkeyPreStatus== EkeyStatusEnum.OPENED) {
                ekeyStatusChange = EkeyStatusChange.OPENED_TO_CLOSED;
            }else if(mEkeyPreStatus== EkeyStatusEnum.DISCONNECT){
                ekeyStatusChange = EkeyStatusChange.TO_CONNECTED;
            }
            ekeyDeviceStatusMap.get(addr).setEkeyStatusEnum(EkeyStatusEnum.CLOSED);
        }
        if (pack[8] == 0x16 && pack[9]==0x01){
            if(mEkeyPreStatus== EkeyStatusEnum.CLOSED) {
                ekeyStatusChange = EkeyStatusChange.CLOSED_TO_OPENED;
            }else if(mEkeyPreStatus== EkeyStatusEnum.DISCONNECT){
                ekeyStatusChange = EkeyStatusChange.TO_CONNECTED;
            }
            ekeyDeviceStatusMap.get(addr).setEkeyStatusEnum(EkeyStatusEnum.OPENED);
        }

        if(ekeyStatusChange!=null && mEkeyStatusChangeListener!=null){
            mEkeyStatusChangeListener.onEkeyStatusChange(addr,ekeyStatusChange);
        }
    }
    private void sendCmd(int addr,int cmdPackType){
        byte[] bytes=null;
        if(cmdPackType==EkeyCmdPack.CMD_OPEN_KEY) {
            bytes= EkeyCmdPack.packOpenKey(addr);
        }else if(cmdPackType==EkeyCmdPack.CMD_CHECK_STATUS){
            bytes= EkeyCmdPack.packCheckKeyStatus(addr);
        }
        lastSendTime = SystemClock.elapsedRealtime();
        ekeyDeviceStatusMap.get(addr).setEkeyLastSendTime(lastSendTime);
        slog.d(TAG, "SendCMD Addr"+addr+":"+BytesUtils.bytesToHex(bytes));
        SerialWrite.sendData(mContext, bytes);
    }
    public void setShowLog(boolean showLog) {
        slog.showLog(showLog);
    }
    private class CmdQueue{
        private int addr;
        private int CMD_TYPE;

        public CmdQueue() {
        }

        public CmdQueue(int addr, int CMD_TYPE) {
            this.addr = addr;
            this.CMD_TYPE = CMD_TYPE;
        }

        public int getAddr() {
            return addr;
        }

        public void setAddr(int addr) {
            this.addr = addr;
        }

        public int getCMD_TYPE() {
            return CMD_TYPE;
        }

        public void setCMD_TYPE(int CMD_TYPE) {
            this.CMD_TYPE = CMD_TYPE;
        }
    }
    class OpenKeyThread extends Thread {
        private int keyAddr;
        public OpenKeyThread(int keyAddr) {
            this.keyAddr = keyAddr;
        }
        @Override
        public void run() {
            try {
                slog.d(TAG, "openEkey: " + ekeyDeviceStatusMap.get(keyAddr));
                EkeyFailStatusEnum ekeyFailStatusEnum = null;
                if (!ekeyDeviceStatusMap.containsKey(keyAddr) || ekeyDeviceStatusMap.get(keyAddr).getEkeyStatusEnum() == EkeyStatusEnum.DISCONNECT) {
                    ekeyFailStatusEnum = EkeyFailStatusEnum.DISCONNECTED;
                } else if (ekeyDeviceStatusMap.get(keyAddr).getEkeyStatusEnum() == EkeyStatusEnum.OPENED) {
                    ekeyFailStatusEnum = EkeyFailStatusEnum.HAD_OPENED;
                } else {
                    for (Map.Entry<Integer, EkeyDeviceStatus> devStatus : ekeyDeviceStatusMap.entrySet()) {
                        if (devStatus.getValue().getEkeyStatusEnum() == EkeyStatusEnum.OPENED) {
                            ekeyFailStatusEnum = EkeyFailStatusEnum.HAD_OPENED_INGROUP;
                            break;
                        }
                    }
                }
                if (ekeyFailStatusEnum != null) {
                    if(mEkeyStatusChangeListener!=null) mEkeyStatusChangeListener.onFail(keyAddr, ekeyFailStatusEnum);
                } else {
                    CmdQueue cmdQueue = new CmdQueue(keyAddr, EkeyCmdPack.CMD_OPEN_KEY);
                    checkEkeyStatusThread.addToCmdQueue(cmdQueue);
                }
            }catch (Exception e){
            }
        }
    }
    private class CheckEkeyStatusThread extends Thread {
        private List<CmdQueue> cmdQueues=new ArrayList<>();
        public CheckEkeyStatusThread() {
        }
        @Override
        public void run() {
            while (true) {
                try {
                    if(cmdQueues.size()>0){
                        CmdQueue cmdQueue = cmdQueues.get(0);
                        cmdQueues.remove(cmdQueue);
                        Thread.sleep(minInterval);
                        mReadBuffer.clear();
                        sendCmd(cmdQueue.getAddr(),cmdQueue.getCMD_TYPE());
                        Thread.sleep(minInterval);
                        continue;
                    }
                    EkeyDeviceStatus ekeyDeviceStatus = null;
                    long currentTime = SystemClock.elapsedRealtime();
                    for (Map.Entry<Integer, EkeyDeviceStatus> entry : ekeyDeviceStatusMap.entrySet()) {
                        if(ekeyDeviceStatus==null){
                            ekeyDeviceStatus=entry.getValue();
                        }
                        if (entry.getValue().getEkeyStatusEnum() == EkeyStatusEnum.OPENED) {
                            ekeyDeviceStatus = entry.getValue();
                            break;
                        }else{
                            if(entry.getValue().getEkeyLastSendTime()<ekeyDeviceStatus.getEkeyLastSendTime()){
                                ekeyDeviceStatus=entry.getValue();
                            }
                        }
                    }
                    try {
                        if (ekeyDeviceStatus.getEkeyLastRevTime()< ekeyDeviceStatus.getEkeyLastSendTime() && currentTime-ekeyDeviceStatus.getEkeyLastRevTime()>checkTimeout) {
                            if (ekeyDeviceStatus.getEkeyStatusEnum() != EkeyStatusEnum.DISCONNECT) {
                                ekeyDeviceStatusMap.get(ekeyDeviceStatus.getAddr()).setEkeyStatusEnum(EkeyStatusEnum.DISCONNECT);
                                if (mEkeyStatusChangeListener != null) {
                                    mEkeyStatusChangeListener.onEkeyStatusChange(ekeyDeviceStatus.getAddr(), EkeyStatusChange.TO_DISCONNECTED);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (currentTime - lastSendTime > minInterval) {
                        sendCmd(ekeyDeviceStatus.getAddr(),EkeyCmdPack.CMD_CHECK_STATUS);
                    }
                }catch (Exception e){

                }
                finally {
                }
            }
        }
        public void addToCmdQueue(CmdQueue cmdQueue){
            this.cmdQueues.add(cmdQueue);
        }
    }
    public interface EkeyStatusListener {
        void onEkeyStatusChange(int ekeyAddr, EkeyStatusChange ekeyStatusChange);
        void onFail(int ekeyAddr, EkeyFailStatusEnum ekeyFailStatusEnum);
    }

    public void deInit(){
        if(serialPortUtil!=null){
            mSerialRead.unRegisterListener();
            serialPortUtil.unBindService();
            serialPortUtil=null;
        }
        if(mSerialRead!=null){
            mSerialRead.unRegisterListener();
            mSerialRead=null;
        }
    }
}
