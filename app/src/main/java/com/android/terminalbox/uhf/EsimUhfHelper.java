package com.android.terminalbox.uhf;

import android.util.Log;
import com.android.terminalbox.utils.BytesUtils;
import com.uhf.api.cls.BackReadOption;
import com.uhf.api.cls.ReadListener;
import com.uhf.api.cls.Reader;

import java.util.ArrayList;
import java.util.List;

public class EsimUhfHelper{
    private static final String TAG = "EsimUhfHelper";
    private Reader reader;
    private EsimUhfListener esimUhfListener;
    private static EsimUhfHelper esimUhfHelper = null;
//    CountDownTimer autoStopTimer=null;
    private static String hostIp="172.16.63.100";
//    private static String hostIp="/dev/ttyS4";
    private static int allAntNum=4;
    private static int readMillisSeconds=30000;
    private static ReadListener RL=null;
    private boolean isInvStart = false;
    public interface EsimUhfListener {
        void onTagRead(List<UhfTag> tags);
    }

    private EsimUhfHelper() {
//        autoStopTimer=new CountDownTimer(readMillisSeconds, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//            @Override
//            public void onFinish() {
//                if (reader != null) {
//                    reader.StopReading();
//                }
//            }
//        };
        RL=new ReadListener() {
            @Override
            public void tagRead(Reader r, final Reader.TAGINFO[] tag) {
                List<UhfTag> tags=new ArrayList<>();
                for(int i=0;i<tag.length;i++){
                    Reader.TAGINFO taginfo = tag[i];
                    String s = BytesUtils.bytesToHex(taginfo.EpcId);
                    tags.add(new UhfTag(taginfo.AntennaID,BytesUtils.bytesToHex(taginfo.EpcId),BytesUtils.bytesToHex(taginfo.EmbededData),taginfo.RSSI));
                }
                Log.d(TAG, "tagRead: RL rev"+tags.toString());
                if(esimUhfListener!=null) {
                    esimUhfListener.onTagRead(tags);
                }
            }
        };
    }
    public static EsimUhfHelper getInstance() {
        if (esimUhfHelper == null) {
            synchronized (EsimUhfHelper.class) {
                if (esimUhfHelper == null) {
                    esimUhfHelper = new EsimUhfHelper();
                }
            }
        }
        return esimUhfHelper;
    }
    private boolean initDevice(){
        reader=new Reader();
        reader.addReadListener(RL);
        Reader.READER_ERR reader_err = reader.InitReader_Notype(hostIp, allAntNum);
        Log.d(TAG, "initUHFDevice: "+reader_err);
        if(reader_err!= Reader.READER_ERR.MT_OK_ERR){
            reader=null;
        }else{
            //设置功率
            Reader.AntPowerConf apcf=reader.new AntPowerConf();
            apcf.antcnt=2;
            for(int i=0;i<apcf.antcnt;i++)
            {
                Reader.AntPower jaap=reader.new AntPower();
                jaap.antid=i+1;
                jaap.readPower =(short)3300;//(500-3000)
                jaap.writePower=(short)3300;
                apcf.Powers[i]=jaap;
            }
            reader.ParamSet(Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
        }
        return reader_err== Reader.READER_ERR.MT_OK_ERR;
    }
    public boolean startReadTags(EsimUhfParams esimUhfParams,EsimUhfListener esimUhfListener) {
        return  startReadTags(esimUhfParams.getAntIndex(),esimUhfListener);
    }
    public boolean startReadTags(int[] antIndexs,EsimUhfListener esimUhfListener) {
        if(esimUhfListener==null||antIndexs==null||antIndexs.length==0){
            return false;
        }
        if(reader==null){
            boolean isInitSuc=initDevice();
            if(!isInitSuc){
                return false;
            }
        }else {
            reader.StopReading();
        }
//        autoStopTimer.cancel();
        this.esimUhfListener=esimUhfListener;
        int length = antIndexs.length;
        BackReadOption m_BROption=new BackReadOption();
        m_BROption.IsFastRead = true;//R2000支持
        m_BROption.ReadDuration = (short)(200 * allAntNum);//读取周期
        m_BROption.ReadInterval = 0;//周期间不工作时间
        Reader.READER_ERR startOk = reader.StartReading(antIndexs, length, m_BROption);
        if(startOk!= Reader.READER_ERR.MT_OK_ERR){
            return false;
        }else{
//            autoStopTimer.start();
        }
        isInvStart = true;
        return true;
    }

    public void stopRead(){
        Log.d(TAG, "stopRead: 停止盘存");
        if(reader!=null){
            Reader.READER_ERR reader_err = reader.StopReading();
            if(reader_err == Reader.READER_ERR.MT_OK_ERR){
                isInvStart = false;
                esimUhfListener = null;
            }
            Log.d(TAG, "stopRead: 停止盘存"+reader_err);
        }
    }

    public boolean isInvStart() {
        return isInvStart;
    }
}
