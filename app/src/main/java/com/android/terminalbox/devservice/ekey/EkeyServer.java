package com.android.terminalbox.devservice.ekey;

import android.os.SystemClock;
import android.util.Log;

import com.android.terminalbox.common.AppProp;
import com.android.terminalbox.devservice.serial.SerialServer;
import com.android.terminalbox.utils.box.FormatUtil;

import java.util.Timer;
import java.util.TimerTask;

import cn.shorr.serialport.SerialRead;


/*
  //鉴权成功命令---开锁(远程开锁命令)
//    Signal 取值： 0005---鉴权成功 无消息参数 净荷长度：2
//            7E0101000000020005C1777E7E
//    返回：Signal 取值： 0005---鉴权成功
//    消息参数：01—成功；
//            2) 鉴权失败—闭锁(远程闭锁命令)
//    Signal 取值： 0006---鉴权失败 无消息参数 净荷长度：2
//            7E0101000000020006F1147E7E
//    返回：Signal 取值： 0006---鉴权失败
//    消息参数：01—成功；
//            3) 查询锁状态\刷卡卡号及其他信息等事件命令
//    Signal 取值： 0016---查询锁事件 无消息参数 净荷长度：2
//            7E0101000000020016E3257E7E
//    返回：Signal 取值： 0016---查询锁事件
//    锁状态:
//            01—锁柄开启； 00---锁柄关闭
* */
public class EkeyServer {
    private static final String TAG = "EkeyServer";
    private static final String cmd_open = "7E0101000000020005C1777E7E";
    //    private static final String cmd_close="7E0101000000020006F1147E7E";
    private static final String cmd_status_check = "7E0101000000020016E3257E7E";

    byte[] dataBytes;
    int pos = 0;



    private static EkeyStatus ekeyStatus = EkeyStatus.DISCONNECT;
    //    final ByteBuffer byteBuffer= ByteBuffer.allocate(64);
    private static EkeyServer eKeyServer;
    private static Timer timer;
    private static CheckStatusTask checkStatusTask;
    EkeyStatusChangeListener ekeyStatusChangeListener;
    private static long lastRevTime;

    public static EkeyServer getInstance() {
        if (eKeyServer == null) {
            synchronized (EkeyServer.class) {
                if (eKeyServer == null) {
                    eKeyServer = new EkeyServer();
                    eKeyServer.init();
                }
            }
        }
        return eKeyServer;
    }

    public void addStatusChangeListenner(EkeyStatusChangeListener var) {
        if (ekeyStatusChangeListener != null) {
            ekeyStatusChangeListener = null;
        }
        this.ekeyStatusChangeListener = var;
    }

    final SerialRead.ReadDataListener revListener = new SerialRead.ReadDataListener() {
        @Override
        public void onReadData(byte[] data) {
            lastRevTime = SystemClock.elapsedRealtime();
            if (data.length == 0) {
                return;
            }
            if (data.length + pos > dataBytes.length) {
                pos = 0;
                dataBytes = new byte[64];
            }
            if (pos == 0) {
                int v_pos = 0;
                for (int idx = 0; idx < data.length; idx++) {
                    if (data[idx] != 0x7E) {
                        v_pos++;
                    } else {
                        break;
                    }
                }
                int v_len = data.length - v_pos;
                System.arraycopy(data, v_pos, dataBytes, 0, v_len);
                pos += v_len;
            } else {
                System.arraycopy(data, 0, dataBytes, pos, data.length);
                pos += data.length;
            }

            int endPosIdx = 0;

            for (int i = 1; i < dataBytes.length && dataBytes.length > 1; i++) {
                if (dataBytes[i - 1] == 0x7E && dataBytes[i] == 0x7E) {
                    endPosIdx = i;

                }
            }
            if (endPosIdx > 0) {
                byte[] pack = new byte[endPosIdx + 1];
                System.arraycopy(dataBytes, 0, pack, 0, endPosIdx + 1);
                pos = 0;
                dataBytes = new byte[64];
                Log.d(TAG, "onReadData: 接收完成：" + FormatUtil.bytesToHexString(pack));
                //7E01010000000F00160000000000FFFFFFFFFFFF0000EF217E7E
                if (pack[8] == 0x16) {
                    if (pack[9] == 0x00) {//关闭
                        if (ekeyStatus == EkeyStatus.OPEN) {
                            ekeyStatus = EkeyStatus.CLOSED;
                            Log.d(TAG, "onReadData: 关闭锁");
                            stopTimer();
                            if (ekeyStatusChangeListener != null) {
                                ekeyStatusChangeListener.onEkeyStatusChange(ekeyStatus);
                            }
                        }else{
                            ekeyStatus = EkeyStatus.CLOSED;
                        }
                    } else if (pack[9] == 0x01) {//开启
                        if (ekeyStatus == EkeyStatus.CLOSED) {
                            ekeyStatus = EkeyStatus.OPEN;
                            Log.d(TAG, "onReadData: 开启锁");
                            if (ekeyStatusChangeListener != null) {
                                ekeyStatusChangeListener.onEkeyStatusChange(ekeyStatus);
                            }
                        }else{
                            ekeyStatus = EkeyStatus.OPEN;
                        }
                    }
                }
            }
        }
    };

    public void openEkey() {
        SerialServer.getInstance().sendHexData(cmd_open);
        startTimer();
    }

    /**
     * 初始化
     *
     * @return 是否初始化成功
     */
    private void init() {
        SerialServer.getInstance().registerListener(revListener);
        dataBytes = new byte[64];
    }
    private void stopTimer(){
        if (timer != null){
            checkStatusTask.cancel();
            timer.cancel();
            timer=null;
        }
    }
    private void startTimer(){
        stopTimer();
        timer = new Timer();
        int ekey_check_period = AppProp.getInstance().getEkey_check_period();
        checkStatusTask=new CheckStatusTask();
        timer.schedule(checkStatusTask, 1000, ekey_check_period);
    }

    private class  CheckStatusTask extends TimerTask{
        @Override
        public void run() {
            if (SystemClock.elapsedRealtime() - lastRevTime > 3000) {
                ekeyStatus = EkeyStatus.DISCONNECT;
                if (ekeyStatusChangeListener != null) {
                    Log.d(TAG, "run: 锁连接断开");
                    ekeyStatusChangeListener.onEkeyStatusChange(ekeyStatus);
                }
            }
            SerialServer.getInstance().sendHexData(cmd_status_check);
            Log.d(TAG, "run: 锁状态检查:");
        }
    };
    public EkeyStatus getEkeyStatus() {
        return ekeyStatus;
    }
}
