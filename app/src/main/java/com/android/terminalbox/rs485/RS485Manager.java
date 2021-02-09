package com.android.terminalbox.rs485;

import android.content.Context;

import com.esim.rylai.smartbox.utils.BytesUtils;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import cn.shorr.serialport.SerialPortConfig;
import cn.shorr.serialport.SerialPortUtil;
import cn.shorr.serialport.SerialRead;
import cn.shorr.serialport.SerialWrite;

public class RS485Manager {
    private static final Logger logger= Logger.getLogger(RS485Manager.class.getSimpleName());
    private static final String TAG = RS485Manager.class.getSimpleName();

    private SerialRead mSerialRead;
    private SerialPortConfig serialPortConfig;
    private static SerialPortUtil serialPortUtil;

    private Context context;
    private String serialPort="/dev/ttyS4";
    private int baudrate=9600;
    private SerialTimeoutListener serialTimeoutListener;
    //读取
    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(128);
    private final List<Rs485PackageHandleFilter> rs485PackageHandleFilters=new ArrayList<>();
    private static final ScheduledExecutorService readExecutor = Executors.newScheduledThreadPool(1);
    private final ReadRunnable readRunnable=new ReadRunnable();
    private CmdSend cmdSend=null;
    //发送
    CircularFifoQueue<CmdPretreat> cmdQueue=new CircularFifoQueue(10);
    private static final ScheduledExecutorService sendExecutor = Executors.newScheduledThreadPool(1);
    private final SendRunnable sendRunnable=new SendRunnable();
    private long READ_TIMEOUT=3000;//读超时

    private static final ScheduledExecutorService autoSendExecutor = Executors.newScheduledThreadPool(1);
    private final Map<String,CmdAutoSend> mapCmdAutoSend=new HashMap<>();
    private final AutoSendRunnable autoSendRunnable=new AutoSendRunnable();

    public List<Rs485PackageHandleFilter> getRs485PackageHandleFilters() {
        return rs485PackageHandleFilters;
    }
    public void addFilters(Rs485PackageHandleFilter... filters){
        for(Rs485PackageHandleFilter filter:filters){
            rs485PackageHandleFilters.add(filter);
        }
    }
    public void send(byte[] cmd){
        cmdQueue.add(new CmdPretreat(cmd));
    }
    public void sendAuto(byte[] cmd,int interval){
        String key = BytesUtils.bytesToHex(cmd);
        mapCmdAutoSend.put(key,new CmdAutoSend(cmd,interval));
    }
    public void stopAuto(byte[] cmd){
        String key = BytesUtils.bytesToHex(cmd);
        mapCmdAutoSend.remove(key);
        //cmdQueue.removeIf(cmdPretreat->BytesUtils.bytesToHex(cmd).equals(BytesUtils.bytesToHex(cmdPretreat.getCmdBody())));
    }
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(String serialPort) {
        this.serialPort = serialPort;
    }

    public int getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public SerialTimeoutListener getSerialTimeoutListener() {
        return serialTimeoutListener;
    }

    public void setSerialTimeoutListener(SerialTimeoutListener serialTimeoutListener) {
        this.serialTimeoutListener = serialTimeoutListener;
    }

    private final SerialRead.ReadDataListener finalReadListener= data -> {
        try {
            mReadBuffer.put(data);
        }catch (Exception ex){
            mReadBuffer.clear();
        }
    };
    private RS485Manager(Builder builder){
        this.context=builder.context;
        this.baudrate=builder.baudrate;
        this.serialPort=builder.serialPort;
        this.serialTimeoutListener=builder.serialTimeoutListener;
        try {
//            if(serialPortUtil!=null){
//                mSerialRead.unRegisterListener();
//                serialPortUtil.unBindService();
//                serialPortUtil=null;
//            }
//            if(mSerialRead!=null){
//                mSerialRead.unRegisterListener();
//                mSerialRead=null;
//            }
//            serialPortConfig=new SerialPortConfig(serialPort,baudrate);
//            serialPortUtil = new SerialPortUtil(context,serialPortConfig);
//            mSerialRead = new SerialRead(context);
//            mSerialRead.registerListener(finalReadListener);
            SerialPortUtil serialPortUtilInstance = SerialInstance.getSerialPortUtilInstance(context, serialPort, baudrate);
            serialPortUtilInstance.bindService();
            SerialRead serialRead=SerialInstance.getSerialReadInstance(context);
            serialRead.registerListener(finalReadListener);
            readExecutor.scheduleWithFixedDelay(readRunnable,1000,100, TimeUnit.MILLISECONDS);
            sendExecutor.scheduleWithFixedDelay(sendRunnable,1000,100, TimeUnit.MILLISECONDS);
            autoSendExecutor.scheduleWithFixedDelay(autoSendRunnable,1000,100, TimeUnit.MILLISECONDS);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public void disConnect(){
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
            readExecutor.shutdown();
            sendExecutor.shutdown();
            autoSendExecutor.shutdown();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static class Builder {
        private Context context;
        private String serialPort="/dev/ttyS4";
        private int baudrate=9600;
        private SerialTimeoutListener serialTimeoutListener;
        public Builder context(Context context) {
            this.context = context;
            return this;
        }
        public Builder serialPort(String serialPort) {
            this.serialPort = serialPort;
            return this;
        }
        public Builder baudrate(int baudrate) {
            this.baudrate = baudrate;
            return this;
        }
        public Builder onTimeout(SerialTimeoutListener serialTimeoutListener) {
            this.serialTimeoutListener = serialTimeoutListener;
            return this;
        }
        public RS485Manager build() {
            return new RS485Manager(this);
        }
    }
    class SendRunnable implements Runnable {
        @Override
        public void run() {
            try {
                long now = System.currentTimeMillis();
                if(cmdSend!=null){
                    if(now-cmdSend.getSendTime()>READ_TIMEOUT){//上个命令未收到响应,情况命令队列
                        if(serialTimeoutListener!=null){
                            serialTimeoutListener.onTimeout(cmdSend.getCmdBody());
                        }
//                        cmdQueue.clear();
                        cmdSend=null;
                    }
                    return;
                }
                CmdPretreat cmdPretreat = cmdQueue.peek();
                if(cmdPretreat==null){//队列为null
                    return;
                }

                cmdPretreat = cmdQueue.poll();
                cmdSend=new CmdSend(cmdPretreat.getCmdBody());
                logger.info("=============SendData:"+ BytesUtils.bytesToHex(cmdPretreat.getCmdBody())+"====================");
                SerialWrite.sendData(context,cmdPretreat.getCmdBody());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class AutoSendRunnable implements Runnable {
        private int loopCount=0;
        @Override
        public void run() {
            try {
                loopCount++;
                if(loopCount>36000){//100ms一次，一小时重置计数
                    loopCount=1;
                }
                for(Map.Entry<String, CmdAutoSend> entry : mapCmdAutoSend.entrySet()){
                    CmdAutoSend cmdAutoSend = entry.getValue();
                    int sendInterval = cmdAutoSend.getIntervalMultiplesOf100ms();
                    if(loopCount%sendInterval==0){
                        CmdPretreat cmdPretreat=new CmdPretreat(cmdAutoSend.getCmdBody());
                        cmdQueue.add(cmdPretreat);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class ReadRunnable implements Runnable {
        int preBufferLength=0;
        @Override
        public void run() {
            try {
                int crtBufferLength= mReadBuffer.position();
                if(crtBufferLength<=0){
                    return;
                }
                if(crtBufferLength>preBufferLength){ //100ms内收到新数据
                    preBufferLength=crtBufferLength;
                }else{// 100ms内未收到数据，商定为数据收完，开始处理数据，并清空buffer
                    mReadBuffer.flip();  //make buffer ready for read
                    int remaining = mReadBuffer.remaining();
                    byte[] data=new byte[remaining];
                    mReadBuffer.get(data,0,remaining);
                    logger.info("=============RevData:"+ BytesUtils.bytesToHex(data)+"====================");
                    mReadBuffer.clear();
                    preBufferLength=0;
                    new Thread(() -> {
                        for(Rs485PackageHandleFilter filter:rs485PackageHandleFilters){
                            if(filter.checkMatched(data)) {
                                if(cmdSend!=null) {
                                    String msgCode=bytesToHex(cmdSend.getCmdBody());
                                    filter.setDefaultMsgCode(msgCode);
                                    filter.revPackage(data);
                                }
                            }
                        }
                        cmdSend=null;
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 字节数组转16进制字符串
     * @param bs
     * @return
     */
    private final  char[] HEX_VOCABLE = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    public String bytesToHex(byte[] bs) {
        if(bs==null||bs.length==0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bs) {
            int high = (b >> 4) & 0x0f;
            int low = b & 0x0f;
            sb.append(HEX_VOCABLE[high]);
            sb.append(HEX_VOCABLE[low]);
        }
        return sb.toString();
    }
}
