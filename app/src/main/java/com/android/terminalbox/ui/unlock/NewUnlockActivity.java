package com.android.terminalbox.ui.unlock;

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
import android.widget.Toast;

import com.android.terminalbox.R;
import com.android.terminalbox.app.BaseApplication;
import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.contract.UnlockContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.cmb.AssetBackPara;
import com.android.terminalbox.core.bean.cmb.AssetBorrowPara;
import com.android.terminalbox.core.bean.cmb.AssetFilterParameter;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;
import com.android.terminalbox.core.bean.cmb.NewBorrowBackPara;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.old.EkeyManager;
import com.android.terminalbox.presenter.UnlockPresenter;
import com.android.terminalbox.uhf.EsimUhfHelper;
import com.android.terminalbox.ui.inventory.FileBeanAdapter;
import com.android.terminalbox.utils.ToastUtils;
import com.esim.rylai.smartbox.ekey.EkeyFailStatusEnum;
import com.esim.rylai.smartbox.ekey.EkeyStatusChange;
import com.esim.rylai.smartbox.uhf.InventoryStrategy;
import com.esim.rylai.smartbox.uhf.ReaderResult;
import com.esim.rylai.smartbox.uhf.UhfManager;
import com.esim.rylai.smartbox.uhf.UhfTag;
import com.multilevel.treelist.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
    @BindView(R.id.open_layout)
    RelativeLayout openLayout;
    @BindView(R.id.close_layout)
    RelativeLayout closeLayout;
    @BindView(R.id.inout_layout)
    RelativeLayout inOutLayout;
    @BindView(R.id.tv_borrow_error)
    TextView tvBorrowError;
    @BindString(R.string.loc_id)
    String locId;
    @BindString(R.string.loc_name)
    String locName;
    private Handler mHandler = new Handler();
    private List<AssetsListItemInfo> files = new ArrayList<>();
    //private List<AssetsListItemInfo> inFiles = new ArrayList<>();
    //private List<AssetsListItemInfo> outFiles = new ArrayList<>();
    private List<AssetsListItemInfo> inOutFiles = new ArrayList<>();
    private FileBeanAdapter mOutAdapter;
    private Animation mRadarAnim;
    private UserInfo currentUser;
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
        List<Node> mSelectAssetsLocations = new ArrayList<>();
        mSelectAssetsLocations.add(new Node(locId, "-1", locName));
        conditions.setmSelectAssetsLocations(mSelectAssetsLocations);
        currentUser = BaseApplication.getInstance().getCurrentUer();
        openLayout.setVisibility(View.VISIBLE);
        closeLayout.setVisibility(View.GONE);
        inOutLayout.setVisibility(View.GONE);
        mOutAdapter = new FileBeanAdapter(inOutFiles, this, false);
        mOutRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mOutRecycleView.setAdapter(mOutAdapter);
        int maxTime = DataManager.getInstance().getMixTime();
        int maxUnchange = DataManager.getInstance().getMixTimeUnchange();
        ToastUtils.showShort("maxTime===" + maxTime + "      maxUnchange===" + maxUnchange);
        UhfManager.getInstance().confReadListener(uhfListener);
        InventoryStrategy inventoryStrategy = new InventoryStrategy();
        inventoryStrategy.setMaxTimesOfInv(maxTime);
        inventoryStrategy.setMaxTimesOfUnChange(maxUnchange);
        UhfManager.getInstance().confInventoryStrategy(inventoryStrategy);
        EkeyManager.getInstance().openEkey(1, ekeyListener);
        initAnim();
        mPresenter.fetchPageAssetsList(pageSize, currentPage, "", "", conditions.toString());

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
                    if (isDestroy) {
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
                            if (openLayout == null || closeLayout == null || inOutLayout == null) {
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
                    if (isDestroy) {
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
                if (assetsListItemInfo != null && !files.contains(assetsListItemInfo)) {
                    files.add(assetsListItemInfo);
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isDestroy) {
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
                    if (tempLocal.size() > 5) {
                        tvBorrowError.setVisibility(View.VISIBLE);
                    } else {
                        tvBorrowError.setVisibility(View.GONE);
                    }
                    /*inFiles.addAll(tempInvFiles);
                    outFiles.addAll(tempLocal);*/
                    inOutFiles.addAll(tempInvFiles);
                    inOutFiles.addAll(tempLocal);
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
                    Date today = new Date();
                    AssetBorrowPara assetBorrowPara = new AssetBorrowPara();
                    assetBorrowPara.setOdr_transactor_id(currentUser.getId());
                    assetBorrowPara.setBor_user_id(currentUser.getId());
                    assetBorrowPara.setExpect_rever_date(new Date(today.getTime() + 604800000));
                    assetBorrowPara.setBor_date(today);
                    assetBorrowPara.setOdr_remark("");
                    assetBorrowPara.setUser_mobile(currentUser.getUser_mobile());
                    assetBorrowPara.setTra_user_name(currentUser.getUser_real_name());
                    assetBorrowPara.setBor_user_name(currentUser.getUser_real_name());

                    AssetBackPara assetBackPara = new AssetBackPara();
                    assetBackPara.setRev_user_id(currentUser.getId());
                    assetBackPara.setRev_user_name(currentUser.getUser_real_name());
                    assetBackPara.setActual_rever_date(today);
                    assetBackPara.setOdr_remark("back");
                    for (AssetsListItemInfo assetsListItemInfo : tempLocal) {
                        assetBorrowPara.getAstids().add(assetsListItemInfo.getId());
                    }
                    for (AssetsListItemInfo tempInvFile : tempInvFiles) {
                        assetBackPara.getAst_ids().add(tempInvFile.getId());
                    }
                    if (assetBackPara.getAst_ids().size() > 0) {
                        String formData = assetBackPara.toString();
                        String title = currentUser.getUser_real_name() + "提交的归还申请";
                        mPresenter.backTools(new NewBorrowBackPara(formData, "[]", title));
                    }
                    if (assetBorrowPara.getAstids().size() > 0) {
                        String formData = assetBorrowPara.toString();
                        String title = currentUser.getUser_real_name() + "提交的借用申请";
                        mPresenter.borrowTools(new NewBorrowBackPara(formData, "[]", title));
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
                epcToolMap.put(tool.getAst_epc_code(), tool);
                if (tool.getAst_used_status() == 0) {
                    toolList.add(tool);
                }
            }
        }
        /*openLayout.setVisibility(View.GONE);
        closeLayout.setVisibility(View.VISIBLE);
        inOutLayout.setVisibility(View.GONE);
        roundImg.startAnimation(mRadarAnim);
        UhfManager.getInstance().startReadTags();*/
    }

    @Override
    public void handleBorrowTools(BaseResponse borrowToolsResponse) {
        if ("200000".equals(borrowToolsResponse.getCode())) {
            ToastUtils.showShort("借用工具成功");
        } else if ("200002".equals(borrowToolsResponse.getCode())) {
            ToastUtils.showShort("请求参数异常");
        } else {
            ToastUtils.showShort("借用失败:" + borrowToolsResponse.getMessage() + borrowToolsResponse.getCode());
        }
    }

    @Override
    public void handleBackTools(BaseResponse backToolsResponse) {
        if ("200000".equals(backToolsResponse.getCode())) {
            ToastUtils.showShort("归还工具成功");
        } else if ("200002".equals(backToolsResponse.getCode())) {
            ToastUtils.showShort("请求参数异常");
        } else {
            ToastUtils.showShort("归还失败:" + backToolsResponse.getMessage() + backToolsResponse.getCode());
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
