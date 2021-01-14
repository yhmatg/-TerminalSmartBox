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
import com.android.terminalbox.mqtt.MqttServer;
import com.android.terminalbox.mqtt.own.Props;
import com.android.terminalbox.mqtt.own.ResultProp;
import com.android.terminalbox.presenter.UnlockPresenter;
import com.android.terminalbox.uhf.EsimUhfHelper;
import com.android.terminalbox.ui.inventory.FileBeanAdapter;
import com.android.terminalbox.utils.StringUtils;
import com.android.terminalbox.utils.ToastUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.esim.rylai.smartbox.ekey.EkeyFailStatusEnum;
import com.esim.rylai.smartbox.ekey.EkeyManager;
import com.esim.rylai.smartbox.ekey.EkeyStatusChange;
import com.esim.rylai.smartbox.uhf.InventoryStrategy;
import com.esim.rylai.smartbox.uhf.ReaderResult;
import com.esim.rylai.smartbox.uhf.UhfManager;
import com.esim.rylai.smartbox.uhf.UhfTag;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

public class NewUnlockActivity extends BaseActivity<UnlockPresenter> implements UnlockContract.View {
    private static String TAG = "NewUnlockActivity";
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
    private String orderUuid;
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

        localFiles = BaseDb.getInstance().getEpcFileDao().findEpcFileByBox("box002");
        mOutAdapter = new FileBeanAdapter(outFiles, this);
        mOutRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mOutRecycleView.setAdapter(mOutAdapter);
        mInAdapter = new FileBeanAdapter(inFiles, this);
        mInRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mInRecycleView.setAdapter(mInAdapter);
        //初始化rfid
        int maxTime = BaseApplication.getInstance().getMixTime();
        int maxUnchange = BaseApplication.getInstance().getMixTimeUnchange();
        ToastUtils.showShort("maxTime===" + maxTime +"      maxUnchange===" + maxUnchange);
        UhfManager.getInstance().confReadListener(uhfListener);
        InventoryStrategy inventoryStrategy = new InventoryStrategy();
        inventoryStrategy.setMaxTimesOfInv(maxTime);
        inventoryStrategy.setMaxTimesOfUnChange(maxUnchange);
        UhfManager.getInstance().confInventoryStrategy(inventoryStrategy);
        EkeyManager.getInstance().openEkey(1,ekeyListener);
        initAnim();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_unlock;
    }

    @Override
    protected void initToolbar() {

    }

    private final EkeyManager.EkeyStatusListener ekeyListener = new EkeyManager.EkeyStatusListener() {
        @Override
        public void onEkeyStatusChange(int ekeyAddr, EkeyStatusChange ekeyStatusChange) {
            Log.d(TAG, "onEkeyStatusChange: " + "Ekey Addr：" + ekeyAddr + "     StatusChange:" + ekeyStatusChange.getDisp());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (ekeyStatusChange) {
                        case CLOSED_TO_OPENED:
                            Log.e(TAG, "=========ekey open============: " + Thread.currentThread().toString());
                            if (!StringUtils.isEmpty(orderUuid)) {
                                openReport(orderUuid);
                            }
                            break;
                        case TO_CONNECTED:
                            break;
                        case OPENED_TO_CLOSED:
                            Log.e(TAG, "=========ekey close============: " + Thread.currentThread().toString());
                            openLayout.setVisibility(View.GONE);
                            closeLayout.setVisibility(View.VISIBLE);
                            inOutLayout.setVisibility(View.GONE);
                            roundImg.startAnimation(mRadarAnim);
                            if (!StringUtils.isEmpty(orderUuid)) {
                                closeReport(orderUuid);
                            }
                            UhfManager.getInstance().startReadTags();
                            break;
                    }
                }
            });
        }

        @Override
        public void onFail(int ekeyAddr, EkeyFailStatusEnum ekeyFailStatusEnum) {

        }
    };

    private final UhfManager.EsimUhfReadListener uhfListener = new UhfManager.EsimUhfReadListener() {
        private long startTime = 0;

        @Override
        public void onStartSuc() {

            Log.d(TAG, "onStartSuc:" + "=============");
        }

        @Override
        public void onStartFail(ReaderResult reader_err) {
            Log.d(TAG, "onStartFail:" + "=============" + reader_err.toString());
        }

        @Override
        public void onReadIncrementalTotal(Collection<String> epcs) {
            Log.d(TAG, "onStartSuc:" + "=============" + epcs.size());
            Log.e("Thread======", Thread.currentThread().toString());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (allNumbers != null) {
                        allNumbers.setText(String.valueOf(epcs.size()));
                    }
                }
            });
        }

        @Override
        public void onReadFinish(Collection<UhfTag> tags) {
            Log.d(TAG, "onReadFinish:" + "=============");
            Log.e("Thread======", Thread.currentThread().toString());
            List<String> epcs = Stream.of(tags).map(new Function<UhfTag, String>() {
                @Override
                public String apply(UhfTag uhfTags) {
                    return uhfTags.getEpc();
                }
            }).collect(Collectors.toList());
           /* List<EpcFile> epcToFiles = Stream.of(epcs).map(new Function<String, EpcFile>() {
                @Override
                public EpcFile apply(String s) {
                    return new EpcFile("档案Epc", s);
                }
            }).collect(Collectors.toList());*/
            List<EpcFile> epcToFiles = BaseDb.getInstance().getEpcFileDao().findEpcFileByEpcs(epcs);
            files.addAll(epcToFiles);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<EpcFile> tempLocal = new ArrayList<>();
                    ArrayList<EpcFile> tempInvFiles = new ArrayList<>();
                    tempLocal.addAll(localFiles);
                    tempInvFiles.addAll(files);
                    //存件
                    tempInvFiles.removeAll(tempLocal);
                    for (EpcFile tempInvFile : tempInvFiles) {
                        tempInvFile.setBoxCode("box002");
                    }
                    //取件
                    tempLocal.removeAll(files);
                    for (EpcFile epcFile : tempLocal) {
                        epcFile.setBoxCode("");
                    }
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
                    BaseDb.getInstance().getEpcFileDao().insertItems(tempLocal);
                    List<EpcFile> allEpcFile = BaseDb.getInstance().getEpcFileDao().findEpcFileByBox("box002");
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
                    if (openLayout != null) {
                        openLayout.setVisibility(View.GONE);
                    }
                    if (closeLayout != null) {
                        closeLayout.setVisibility(View.GONE);
                    }
                    if (inOutLayout != null) {
                        inOutLayout.setVisibility(View.VISIBLE);
                    }
                    if (roundImg != null) {
                        roundImg.clearAnimation();
                    }
                }
            });
        }
    };

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
