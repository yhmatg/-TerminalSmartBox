package com.android.terminalbox.ui.inventory;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.terminalbox.R;
import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.contract.NewInvContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.cmb.AssetFilterParameter;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;
import com.android.terminalbox.core.bean.user.EpcFile;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.presenter.NewInvPresenter;
import com.android.terminalbox.utils.ToastUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.esim.rylai.smartbox.uhf.InventoryStrategy;
import com.esim.rylai.smartbox.uhf.ReaderResult;
import com.esim.rylai.smartbox.uhf.UhfManager;
import com.esim.rylai.smartbox.uhf.UhfTag;
import com.multilevel.treelist.Node;
import com.xuexiang.xlog.XLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

public class NewInvActivity extends BaseActivity<NewInvPresenter> implements NewInvContract.View {
    String TAG = "InventoryActivity";
    @BindView(R.id.tv_number)
    TextView numberText;
    @BindView(R.id.tv_inv_status)
    TextView invStatus;
    @BindView(R.id.iv_round)
    ImageView roundImg;
    @BindView(R.id.rv_inv_items)
    RecyclerView mRecycleView;
    @BindView(R.id.edit_search)
    EditText editText;
    @BindView(R.id.number_layout)
    FrameLayout numberLayout;
    @BindView(R.id.tv_see_detail)
    TextView seeDetail;
    @BindView(R.id.detail_layout)
    RelativeLayout detailLayout;
    @BindString(R.string.loc_id)
    String locId;
    @BindString(R.string.loc_name)
    String locName;
    private List<AssetsListItemInfo> files = new ArrayList<>();
    //存储一次盘点的数据
    private List<AssetsListItemInfo> allFiles = new ArrayList<>();
    private FileBeanAdapter mAdapter;
    private Animation mRadarAnim;
    private Handler mHandler = new Handler();
    private boolean isDestroy;
    private int currentPage = 1;
    private int pageSize = 600;
    private AssetFilterParameter conditions = new AssetFilterParameter();
    //闲置的工具
    private HashMap<String, AssetsListItemInfo> epcToolMap = new HashMap<>();
    private List<String> epcList = new ArrayList<>();

    private void initAnim() {
        mRadarAnim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRadarAnim.setFillAfter(true); // 设置保持动画最后的状态
        mRadarAnim.setDuration(2000); // 设置动画时间
        mRadarAnim.setRepeatCount(Animation.INFINITE);//设置动画重复次数 无限循环
        mRadarAnim.setInterpolator(new LinearInterpolator());
        mRadarAnim.setRepeatMode(Animation.RESTART);
    }

    @Override
    public NewInvPresenter initPresenter() {
        return new NewInvPresenter();
    }

    @Override
    protected void initEventAndData() {
        List<Node> mSelectAssetsLocations = new ArrayList<>();
        mSelectAssetsLocations.add(new Node(locId, "-1", locName));
        conditions.setmSelectAssetsLocations(mSelectAssetsLocations);
        numberLayout.setVisibility(View.VISIBLE);
        roundImg.setVisibility(View.VISIBLE);
        seeDetail.setVisibility(View.VISIBLE);
        detailLayout.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        mAdapter = new FileBeanAdapter(files, this, true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    String assetsId = editText.getText().toString();
                    editText.setSelection(assetsId.length());
                    List<AssetsListItemInfo> filterList = Stream.of(allFiles).filter(new Predicate<AssetsListItemInfo>() {
                        @Override
                        public boolean test(AssetsListItemInfo value) {
                            int i = value.getAst_epc_code().indexOf(assetsId);
                            return i != -1;
                        }
                    }).collect(Collectors.toList());
                    files.clear();
                    files.addAll(filterList);
                    mAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });
        initAnim();
        //开始盘点
        /*int maxTime = DataManager.getInstance().getMixTime();
        int maxUnchange = DataManager.getInstance().getMixTimeUnchange();
        ToastUtils.showShort("maxTime===" + maxTime + "      maxUnchange===" + maxUnchange);
        roundImg.startAnimation(mRadarAnim);
        UhfManager.getInstance().confReadListener(uhfListener);
        InventoryStrategy inventoryStrategy = new InventoryStrategy();
        inventoryStrategy.setMaxTimesOfInv(maxTime);
        inventoryStrategy.setMaxTimesOfUnChange(maxUnchange);
        UhfManager.getInstance().confInventoryStrategy(inventoryStrategy);
        UhfManager.getInstance().startReadTags();*/
        Log.e("Thread======", Thread.currentThread().toString());
        mPresenter.fetchPageAssetsList(pageSize, currentPage, "", "", conditions.toString());
    }

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
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isDestroy) {
                        return;
                    }
                    if (numberText != null) {
                        epcs.retainAll(epcList);
                        numberText.setText(String.valueOf(epcs.size()));
                    }
                }
            });
        }

        @Override
        public void onReadFinish(Collection<UhfTag> tags) {
            Log.d(TAG, "onReadFinish:" + "=============");
            for (UhfTag tag : tags) {
                AssetsListItemInfo assetsListItemInfo = epcToolMap.get(tag.getEpc());
                if (assetsListItemInfo != null && !files.contains(assetsListItemInfo)) {
                    files.add(assetsListItemInfo);
                }
            }
            Log.e(TAG, files.size() + "====" + files.toString());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isDestroy) {
                        return;
                    }
                    if (numberText != null) {
                        numberText.setText("" + files.size());
                        invStatus.setText("盘点完成");
                        mAdapter.notifyDataSetChanged();
                        allFiles.addAll(files);
                        if (roundImg != null) {
                            roundImg.clearAnimation();
                        }
                    }
                }
            });
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_inv;
    }

    @Override
    protected void initToolbar() {

    }

    @OnClick({R.id.titleLeft, R.id.tv_see_detail})
    void performClick(View v) {
        switch (v.getId()) {
            case R.id.titleLeft:
                finish();
                break;
            case R.id.tv_see_detail:
                numberLayout.setVisibility(View.GONE);
                roundImg.setVisibility(View.GONE);
                detailLayout.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                seeDetail.setVisibility(View.GONE);
                break;
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
    }

    @Override
    public void handleFetchPageAssetsList(List<AssetsListItemInfo> assetsInfos) {
        XLog.get().e(TAG + "存取资产数量=====" + assetsInfos.size());
        epcToolMap.clear();
        epcList.clear();
        for (AssetsListItemInfo tool : assetsInfos) {
            if (locName.equals(tool.getLoc_name())) {
                epcToolMap.put(tool.getAst_epc_code(), tool);
                epcList.add(tool.getAst_epc_code());
              /*  if (tool.getAst_used_status() == 0) {
                    epcList.add(tool.getAst_epc_code());
                }*/
            }
        }
        //开始盘点
        int maxTime = DataManager.getInstance().getMixTime();
        int maxUnchange = DataManager.getInstance().getMixTimeUnchange();
        ToastUtils.showShort("maxTime===" + maxTime + "      maxUnchange===" + maxUnchange);
        roundImg.startAnimation(mRadarAnim);
        UhfManager.getInstance().confReadListener(uhfListener);
        InventoryStrategy inventoryStrategy = new InventoryStrategy();
        inventoryStrategy.setMaxTimesOfInv(maxTime);
        inventoryStrategy.setMaxTimesOfUnChange(maxUnchange);
        UhfManager.getInstance().confInventoryStrategy(inventoryStrategy);
        UhfManager.getInstance().startReadTags();
    }
}
