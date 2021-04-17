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
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.cmb.AssetFilterParameter;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;
import com.android.terminalbox.core.bean.user.EpcFile;
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
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
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
    @BindString(R.string.loc_id)
    String locId;
    @BindString(R.string.loc_name)
    String locName;
    private Handler mHandler = new Handler();
    private List<AssetsListItemInfo> files = new ArrayList<>();
    private List<AssetsListItemInfo> inFiles = new ArrayList<>();
    private List<AssetsListItemInfo> outFiles = new ArrayList<>();
    private FileBeanAdapter mOutAdapter;
    private FileBeanAdapter mInAdapter;
    private Animation mRadarAnim;
    private UserInfo currentUer;
    private boolean isDestroy;
    private int currentPage = 1;
    private int pageSize = 100;
    private AssetFilterParameter conditions = new AssetFilterParameter();
    private HashMap<String, AssetsListItemInfo> epcToolMap = new HashMap<>();
    private List<AssetsListItemInfo> toolList = new ArrayList<>();

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
        mOutAdapter = new FileBeanAdapter(outFiles, this,false);
        mOutRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mOutRecycleView.setAdapter(mOutAdapter);
        mInAdapter = new FileBeanAdapter(inFiles, this,false);
        mInRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mInRecycleView.setAdapter(mInAdapter);
        //初始化rfid
        int maxTime = DataManager.getInstance().getMixTime();
        int maxUnchange = DataManager.getInstance().getMixTimeUnchange();
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
                    if (isDestroy){
                        return;
                    }
                    switch (ekeyStatusChange) {
                        case CLOSED_TO_OPENED:
                            Log.e(TAG, "=========ekey open============: " + Thread.currentThread().toString());
                            break;
                        case TO_CONNECTED:
                            break;
                        case OPENED_TO_CLOSED:
                            Log.e(TAG, "=========ekey close============: " + Thread.currentThread().toString());
                            if(openLayout == null || closeLayout == null || inOutLayout == null){
                                return;
                            }
                            openLayout.setVisibility(View.GONE);
                            closeLayout.setVisibility(View.VISIBLE);
                            inOutLayout.setVisibility(View.GONE);
                            roundImg.startAnimation(mRadarAnim);
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
                    if (isDestroy){
                        return;
                    }
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
            for (UhfTag tag : tags) {
                AssetsListItemInfo assetsListItemInfo = epcToolMap.get(tag.getEpc());
                if(assetsListItemInfo != null && !files.contains(assetsListItemInfo)){
                    files.add(assetsListItemInfo);
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isDestroy){
                        return;
                    }
                    ArrayList<AssetsListItemInfo> tempLocal = new ArrayList<>();
                    ArrayList<AssetsListItemInfo> tempInvFiles = new ArrayList<>();
                    tempInvFiles.addAll(files);
                    tempLocal.addAll(toolList);
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

    @Override
    public void handleFetchPageAssetsList(List<AssetsListItemInfo> assetsInfos) {
        Log.e(TAG, "page资产数量是=====" + assetsInfos.size());
        epcToolMap.clear();
        toolList.clear();
        for (AssetsListItemInfo tool : assetsInfos) {
            if (locName.equals(tool.getLoc_name())) {
                if (tool.getAst_used_status() == 0) {
                    epcToolMap.put(tool.getAst_epc_code(), tool);
                    toolList.add(tool);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
        isDestroy = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EsimUhfHelper.getInstance().isInvStart()) {
            EsimUhfHelper.getInstance().stopRead();
        }
    }
}
