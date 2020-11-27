package com.android.terminalbox.ui.unlock;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.terminalbox.R;
import com.android.terminalbox.app.BaseApplication;
import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.contract.UnlockContract;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.EpcFile;
import com.android.terminalbox.core.bean.user.NewOrderBody;
import com.android.terminalbox.core.bean.user.OrderResponse;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.devservice.ekey.EkeyServer;
import com.android.terminalbox.devservice.ekey.EkeyStatusChangeListener;
import com.android.terminalbox.mqtt.MqttServer;
import com.android.terminalbox.mqtt.own.Props;
import com.android.terminalbox.mqtt.own.ResultProp;
import com.android.terminalbox.presenter.UnlockPresenter;
import com.android.terminalbox.uhf.EsimUhfHelper;
import com.android.terminalbox.uhf.EsimUhfParams;
import com.android.terminalbox.uhf.UhfTag;
import com.android.terminalbox.ui.inventory.FileBeanAdapter;
import com.android.terminalbox.utils.StringUtils;
import com.android.terminalbox.utils.ToastUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

public class UnlockActivity extends BaseActivity<UnlockPresenter> implements UnlockContract.View {
    private static String TAG = "UnlockActivity";
    private static String deviceId = "15aa68f3183311ebb7260242ac120004_uniqueCode002";
    @BindView(R.id.inv_numbers)
    TextView invNumbers;
    @BindView(R.id.all_number)
    TextView allNumbers;
    @BindView(R.id.out_number)
    TextView outNumbers;
    @BindView(R.id.in_number)
    TextView inNumbers;
    @BindView(R.id.iv_round)
    ImageView roundImg;
    @BindView(R.id.rv_out_items)
    RecyclerView mOutRecycleView;
    @BindView(R.id.in_inv_items)
    RecyclerView mInRecycleView;
    @BindView(R.id.open_layout)
    RelativeLayout openLayout;
    @BindView(R.id.close_layout)
    RelativeLayout closeLayout;
    @BindView(R.id.inout_layout)
    RelativeLayout inOutLayout;
    private Handler mHandler = new Handler();
    private List<EpcFile> files = new ArrayList<>();
    private List<EpcFile> localFiles = new ArrayList<>();
    private List<EpcFile> inFiles = new ArrayList<>();
    private List<EpcFile> outFiles = new ArrayList<>();
    private FileBeanAdapter mOutAdapter;
    private FileBeanAdapter mInAdapter;
    private Animation mRadarAnim;
    public static int epcUnChangeTime = 0;
    public static int epcUnChangeMaxTime = 20;
    private int preSize = 0;
    int invCount = 0;
    int invMaxCount = 3;
    private String orderUuid;
    EkeyServer ekeyServer;
    EkeyStatusChangeListener ekeyStatusChangeListener = ekeyStatus -> {
        switch (ekeyStatus) {
            case OPEN:
                Log.e(TAG, "=========ekey open============: " + Thread.currentThread().toString());
                if (!StringUtils.isEmpty(orderUuid)) {
                    openReport(orderUuid);
                }
                break;
            case CLOSED:
                Log.e(TAG, "=========ekey open============: " + Thread.currentThread().toString());
                openLayout.setVisibility(View.GONE);
                closeLayout.setVisibility(View.VISIBLE);
                inOutLayout.setVisibility(View.GONE);
                roundImg.startAnimation(mRadarAnim);
                if (!StringUtils.isEmpty(orderUuid)) {
                    closeReport(orderUuid);
                }
                startInvTags();
                break;
        }
    };
    EsimUhfParams esimUhfParams;
    private EsimUhfHelper.EsimUhfListener uhfListener = new EsimUhfHelper.EsimUhfListener() {
        @Override
        public synchronized void onTagRead(List<UhfTag> tags) {
            synchronized (UnlockActivity.class) {
                Log.e(invCount + "======" + epcUnChangeTime, tags.toString() + Thread.currentThread().toString());
                List<String> epcs = Stream.of(tags).map(new Function<UhfTag, String>() {
                    @Override
                    public String apply(UhfTag uhfTags) {
                        return uhfTags.getEpc();
                    }
                }).collect(Collectors.toList());//Log.d(TAG, "invTags: 本次盘点到标签" + epcs.size() + "    " + epcs.toString());
                List<String> fileToEpcs = Stream.of(files).map(new Function<EpcFile, String>() {
                    @Override
                    public String apply(EpcFile fileBean) {
                        return fileBean.getEpcCode();
                    }
                }).collect(Collectors.toList());
                epcs.removeAll(fileToEpcs);//            Log.d(TAG, "invTags: 其中以前未盘到" + epcs.size() + "    " + epcs.toString());
                if (epcs.size() > 0) {//扫描到新标签
                    epcUnChangeTime = 0;
                    List<EpcFile> epcToFiles = Stream.of(epcs).map(new Function<String, EpcFile>() {
                        @Override
                        public EpcFile apply(String s) {
                            return new EpcFile("档案Epc", s);
                        }
                    }).collect(Collectors.toList());
                    files.addAll(epcToFiles);//Log.d(TAG, "invTags: 本次盘点后总标签" + inBoxEpcsTemp.size() + "    " + inBoxEpcsTemp.toString());
                    Log.e(TAG, files.size() + "====" + files.toString());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (allNumbers != null) {
                                allNumbers.setText(String.valueOf(files.size()));
                            }
                        }
                    });
                } else {
                    epcUnChangeTime++;
                    if (epcUnChangeTime >= epcUnChangeMaxTime) {//次扫描不到新标签，假定扫描完 Log.d(TAG, "invTags: " + epcUnChangeTime + "+次未找到新标签,假定扫描完");
                        if (files.size() != preSize) {
                            preSize = files.size();
                        }
                        EsimUhfHelper.getInstance().stopRead();
                        if (invCount < invMaxCount) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    invCount++;
                                    epcUnChangeTime = 0;
                                    EsimUhfHelper.getInstance().startReadTags(esimUhfParams, uhfListener);
                                }
                            }, 200);
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<EpcFile> tempLocal = new ArrayList<>();
                                    ArrayList<EpcFile> tempInvFiles = new ArrayList<>();
                                    tempLocal.addAll(localFiles);
                                    tempInvFiles.addAll(files);
                                    //存件
                                    tempInvFiles.removeAll(tempLocal);
                                    //取件
                                    tempLocal.removeAll(files);
                                    if (inNumbers != null) {
                                        inNumbers.setText(String.valueOf(tempInvFiles.size()));
                                    }
                                    if (outNumbers != null) {
                                        outNumbers.setText(String.valueOf(tempLocal.size()));
                                    }
                                    inFiles.addAll(tempInvFiles);
                                    outFiles.addAll(tempLocal);
                                    mInAdapter.notifyDataSetChanged();
                                    mOutAdapter.notifyDataSetChanged();
                                    //数据库更新
                                    BaseDb.getInstance().getEpcFileDao().insertItems(tempInvFiles);
                                    BaseDb.getInstance().getEpcFileDao().deleteItems(tempLocal);
                                    List<EpcFile> allEpcFile = BaseDb.getInstance().getEpcFileDao().findAllEpcFile();
                                    List<String> inEpcStrings = Stream.of(tempInvFiles).map(new Function<EpcFile, String>() {
                                        @Override
                                        public String apply(EpcFile epcFile) {
                                            return epcFile.getEpcCode();
                                        }
                                    }).collect(Collectors.toList());
                                    List<String> outEpcStrings = Stream.of(tempLocal).map(new Function<EpcFile, String>() {
                                        @Override
                                        public String apply(EpcFile epcFile) {
                                            return epcFile.getEpcCode();
                                        }
                                    }).collect(Collectors.toList());
                                    invReport(orderUuid, inEpcStrings, outEpcStrings, allEpcFile.size());
                                    if(openLayout != null){
                                        openLayout.setVisibility(View.GONE);
                                    }
                                    if(closeLayout != null){
                                        closeLayout.setVisibility(View.GONE);
                                    }
                                    if(inOutLayout != null){
                                        inOutLayout.setVisibility(View.VISIBLE);
                                    }
                                    if (roundImg != null) {
                                        roundImg.clearAnimation();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }

    };
    private UserInfo currentUer;

    private void initAnim() {
        mRadarAnim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRadarAnim.setFillAfter(true); // 设置保持动画最后的状态
        mRadarAnim.setDuration(2000); // 设置动画时间
        mRadarAnim.setRepeatCount(Animation.INFINITE);//设置动画重复次数 无限循环
        mRadarAnim.setInterpolator(new LinearInterpolator());
        mRadarAnim.setRepeatMode(Animation.RESTART);
    }

    @Override
    public UnlockPresenter initPresenter() {
        return new UnlockPresenter();
    }

    @Override
    protected void initEventAndData() {
        currentUer = BaseApplication.getInstance().getCurrentUer();
        openLayout.setVisibility(View.VISIBLE);
        closeLayout.setVisibility(View.GONE);
        inOutLayout.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent != null) {
            orderUuid = intent.getStringExtra("relevanceId");
        }
        if (StringUtils.isEmpty(orderUuid)) {
            NewOrderBody newOrderBody = new NewOrderBody();
            newOrderBody.setActType("存取");
            orderUuid = UUID.randomUUID().toString();
            newOrderBody.setRelevanceId(orderUuid);
            newOrderBody.setRemark("remarkOne");
            mPresenter.newOrder(deviceId, newOrderBody, currentUer.getId());
        }

        localFiles = BaseDb.getInstance().getEpcFileDao().findAllEpcFile();
        mOutAdapter = new FileBeanAdapter(outFiles, this);
        mOutRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mOutRecycleView.setAdapter(mOutAdapter);
        mInAdapter = new FileBeanAdapter(inFiles, this);
        mInRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mInRecycleView.setAdapter(mInAdapter);
        //初始化锁
        ekeyServer = EkeyServer.getInstance();
        ekeyServer.addStatusChangeListenner(ekeyStatusChangeListener);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ekeyServer.openEkey();
            }
        }, 1000);
        //初始化rfid
        esimUhfParams = new EsimUhfParams.Builder().antIndex(1, 2, 3, 4).build();
        initAnim();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_unlock;
    }

    @Override
    protected void initToolbar() {

    }

    private void startInvTags() {
        epcUnChangeTime = 0;
        Log.d(TAG, "startInvTags: start inv...");
        preSize = 0;
        invCount = 1;
        boolean isStartOk = EsimUhfHelper.getInstance().startReadTags(esimUhfParams, uhfListener);
        if (!isStartOk) {
            ToastUtils.showShort("读写器异常");
        }
    }

    @OnClick({R.id.titleLeft})
    void performClick(View v) {
        switch (v.getId()) {
            case R.id.titleLeft:
                finish();
                break;
        }
    }

    public void openReport(String relevanceId) {
        Props props = new Props();
        ArrayList<ResultProp> resultProps = new ArrayList<>();
        ResultProp resultProp = new ResultProp();
        resultProp.setCap_id("id1");
        resultProp.setRelevance_id(relevanceId);
        resultProp.setData_event_time(System.currentTimeMillis());
        ResultProp.Prop prop = new ResultProp.Prop();
        prop.setOpenEkey(true);
        prop.setOpenType("remote");
        resultProp.setProp(prop);
        resultProps.add(resultProp);
        props.setProps(resultProps);
        MqttServer.getInstance().reportProperties(new Gson().toJson(props));
    }

    public void closeReport(String relevanceId) {
        Props props = new Props();
        ArrayList<ResultProp> resultProps = new ArrayList<>();
        ResultProp resultProp = new ResultProp();
        resultProp.setCap_id("id2");
        resultProp.setRelevance_id(relevanceId);
        resultProp.setData_event_time(System.currentTimeMillis());
        ResultProp.Prop prop = new ResultProp.Prop();
        prop.setCloseEkey(true);
        resultProp.setProp(prop);
        resultProps.add(resultProp);
        props.setProps(resultProps);
        MqttServer.getInstance().reportProperties(new Gson().toJson(props));
    }

    public void invReport(String relevanceId, List<String> inList, List<String> outList, int allFileNum) {
        Props props = new Props();
        ArrayList<ResultProp> resultProps = new ArrayList<>();
        ResultProp resultProp = new ResultProp();
        resultProp.setCap_id("id3");
        resultProp.setRelevance_id(relevanceId);
        resultProp.setData_event_time(System.currentTimeMillis());
        ResultProp.Prop prop = new ResultProp.Prop();
        prop.setRfid_in(inList);
        prop.setRfid_out(outList);
        prop.setRfid_inbox(allFileNum);
        resultProp.setProp(prop);
        resultProps.add(resultProp);
        props.setProps(resultProps);
        MqttServer.getInstance().reportProperties(new Gson().toJson(props));
    }

    @Override
    public void handleNewOrder(BaseResponse<OrderResponse> NewOrderResponse) {
        if (200000 == NewOrderResponse.getCode()) {
            orderUuid = NewOrderResponse.getData().getRelevanceId();
        } else {
            orderUuid = null;
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(0);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EsimUhfHelper.getInstance().isInvStart()) {
            EsimUhfHelper.getInstance().stopRead();
        }
    }
}
