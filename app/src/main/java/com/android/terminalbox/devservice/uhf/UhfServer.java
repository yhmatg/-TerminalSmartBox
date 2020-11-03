package com.android.terminalbox.devservice.uhf;//package com.rylai.smartbox.devservice.uhf;
//
//import android.os.CountDownTimer;
//
//import com.rylai.rfidapi.uhf.EsimUhfHelper;
//import com.rylai.rfidapi.uhf.EsimUhfListener;
//import com.rylai.rfidapi.uhf.UhfTag;
//import com.rylai.smartbox.util.StreamUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.stream.Collectors;
//
//public class UhfServer {
//    private static final String TAG = "UhfServer";
//
//    private String hostName = "172.16.63.220";
//    private int antIndex=1;
//    EsimUhfHelper esimUhfHelper=null;
//    EsimUhfListener esimUhfListener=null;
//    private boolean isInvTags = false;
//    private static UhfServer uhfServer = null;
////    private static List<UhfTag> cacheTags=new ArrayList<>();
//    Timer timer = new Timer();
//    CountDownTimer countTimer=null;
//    CountDownTimer autoStopTimer=null;
//    private int autoStopMiliSeconds=30000;//30秒盘点自动停止
//    public static UhfServer getInstance() {
//        if (uhfServer == null) {
//            synchronized (UhfServer.class) {
//                if (uhfServer == null) {
//                    uhfServer = new UhfServer();
//                }
//            }
//        }
//        return uhfServer;
//    }
//    /**
//     * 初始化
//     *
//     * @return 是否初始化成功
//     */
//    private boolean init() {
//        esimUhfHelper = new EsimUhfHelper.Builder()
//                .allAntNum(4)
//                .hostIp(hostName)
//                .esimUhfListener(finalListener).build();
//        return esimUhfHelper.init();
//    }
//    public boolean startReadTags(EsimUhfListener esimUhfListener) {
//        if(isInvTags){
//            return false;
//        }
////        cacheTags.clear();
//        this.esimUhfListener=esimUhfListener;
//        boolean initSuc = init();
//        if(!initSuc){
//            return false;
//        }
//        boolean isStartSuc = esimUhfHelper.startReadTag(antIndex);
//        if(isStartSuc){
//            autoStopTimer=new CountDownTimer(autoStopMiliSeconds, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                }
//                @Override
//                public void onFinish() {
//                    esimUhfHelper.stopRead();
//                    esimUhfHelper.closeReader();
//                }
//            }.start();
//            isInvTags=isStartSuc;
//        }
//
//        return isStartSuc;
//    }
//    public void closeReader(){
//        isInvTags=false;
//        esimUhfHelper.stopRead();
//        esimUhfHelper.closeReader();
//    }
//    private final EsimUhfListener finalListener= tags -> {
////        cacheTags.addAll(tags);
//        if(esimUhfListener!=null){
//            esimUhfListener.onTagRead(tags);
//        }
//    };
//
//}
