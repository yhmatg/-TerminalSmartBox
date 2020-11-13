package com.android.terminalbox.ui.unlock;

import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.terminalbox.R;
import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.core.bean.user.NewOrderBody;
import com.android.terminalbox.core.bean.user.OrderResponse;
import com.android.terminalbox.contract.UnlockContract;
import com.android.terminalbox.core.bean.BaseResponse;
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
import com.android.terminalbox.core.bean.user.EpcFile;
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
    @BindView(R.id.unlock_status)
    TextView unlockStatus;
    @BindView(R.id.inv_numbers)
    TextView invNumbers;
    @BindView(R.id.out_numbers)
    TextView outNumbers;
    @BindView(R.id.in_numbers)
    TextView inNumbers;
    @BindView(R.id.iv_round)
    ImageView roundImg;
    @BindView(R.id.rv_inv_items)
    RecyclerView mRecycleView;
    private List<EpcFile> files = new ArrayList<>();
    private List<EpcFile> localFiles = new ArrayList<>();
    private List<EpcFile> inFiles = new ArrayList<>();
    private List<EpcFile> outFiles = new ArrayList<>();
    private FileBeanAdapter mAdapter;
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
                runOnUiThread(() -> unlockStatus.setText("锁状态：开启"));
                break;
            case CLOSED:
                Log.e(TAG, "=========ekey open============: " + Thread.currentThread().toString());
                runOnUiThread(() -> {
                    unlockStatus.setText("锁状态：关闭");
                });
                startInvTags();
                break;
        }
    };
    EsimUhfParams esimUhfParams;
    private EsimUhfHelper.EsimUhfListener uhfListener = new EsimUhfHelper.EsimUhfListener() {
        @Override
        public void onTagRead(List<UhfTag> tags) {
            synchronized (UnlockActivity.class) {
                Log.e(invCount + "======" + epcUnChangeTime, tags.toString());
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invNumbers.setText("总数：" + files.size());
                        }
                    });
                } else {
                    epcUnChangeTime++;
                    if (epcUnChangeTime >= epcUnChangeMaxTime) {//次扫描不到新标签，假定扫描完 Log.d(TAG, "invTags: " + epcUnChangeTime + "+次未找到新标签,假定扫描完");
                        if (files.size() != preSize) {
                            preSize = files.size();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        EsimUhfHelper.getInstance().stopRead();
                        if (invCount < invMaxCount) {
                            invCount++;
                            EsimUhfHelper.getInstance().startReadTags(esimUhfParams, uhfListener);
                        } else {
                            runOnUiThread(new Runnable() {
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
                                    inNumbers.setText("存件：" + tempInvFiles.size());
                                    outNumbers.setText("取件：" + tempLocal.size());
                                    //数据库更新
                                    BaseDb.getInstance().getEpcFileDao().insertItems(tempInvFiles);
                                    BaseDb.getInstance().getEpcFileDao().deleteItems(tempLocal);
                                    roundImg.clearAnimation();
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
                                    invReport(orderUuid, inEpcStrings, inEpcStrings);
                                }
                            });
                        }
                    }
                }
            }
        }

    };

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
        localFiles = BaseDb.getInstance().getEpcFileDao().findAllEpcFile();
        unlockStatus.setText("本地：" + localFiles.size());
        mAdapter = new FileBeanAdapter(files, this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);
        //初始化锁
        ekeyServer = EkeyServer.getInstance();
        ekeyServer.addStatusChangeListenner(ekeyStatusChangeListener);
        //todo 锁暂不实现
        /*new Thread(() -> {
            try {
                Looper.prepare();
                ekeyServer.openEkey();
                Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();*/
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShort("读写器异常");
                    roundImg.clearAnimation();
                }
            });
        }
    }

    @OnClick({R.id.bt_open, R.id.bt_close})
    void performClick(View v) {
        switch (v.getId()) {
            case R.id.bt_open:
                NewOrderBody newOrderBody = new NewOrderBody();
                newOrderBody.setActType("存取");
                orderUuid = UUID.randomUUID().toString();
                newOrderBody.setRelevanceId(orderUuid);
                newOrderBody.setRemark("remarkOne");
                mPresenter.newOrder(deviceId, newOrderBody);
                break;
            case R.id.bt_close:
                if (StringUtils.isEmpty(orderUuid)) {
                    ToastUtils.showShort("请先开锁！！！");
                } else {
                    closeReport(orderUuid);
                    roundImg.startAnimation(mRadarAnim);
                    files.clear();
                    mAdapter.notifyDataSetChanged();
                    invNumbers.setText("总数");
                    inNumbers.setText("存件");
                    outNumbers.setText("取件");
                    new Thread(() -> {
                        try {
                            Looper.prepare();
                            startInvTags();
                            Looper.loop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
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

    public void invReport(String relevanceId, List<String> inList, List<String> outList) {
        Props props = new Props();
        ArrayList<ResultProp> resultProps = new ArrayList<>();
        ResultProp resultProp = new ResultProp();
        resultProp.setCap_id("id3");
        resultProp.setRelevance_id(relevanceId);
        resultProp.setData_event_time(System.currentTimeMillis());
        ResultProp.Prop prop = new ResultProp.Prop();
        prop.setRfid_in(inList);
        prop.setRfid_out(outList);
        resultProp.setProp(prop);
        resultProps.add(resultProp);
        props.setProps(resultProps);
        MqttServer.getInstance().reportProperties(new Gson().toJson(props));
    }

    @Override
    public void handleNewOrder(BaseResponse<OrderResponse> NewOrderResponse) {
        if (200000 == NewOrderResponse.getCode()) {
            openReport(NewOrderResponse.getData().getRelevanceId());
        }
    }
}
