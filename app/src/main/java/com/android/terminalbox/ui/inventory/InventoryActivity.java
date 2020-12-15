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
import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.core.bean.user.EpcFile;
import com.android.terminalbox.uhf.EsimUhfHelper;
import com.android.terminalbox.uhf.EsimUhfParams;
import com.android.terminalbox.uhf.UhfTag;
import com.android.terminalbox.utils.ToastUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class InventoryActivity extends BaseActivity {
    String TAG = "InventoryActivity";
    @BindView(R.id.tv_number)
    TextView numberText;
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
    private List<EpcFile> files = new ArrayList<>();
    //存储一次盘点的数据
    private List<EpcFile> allFiles = new ArrayList<>();
    private FileBeanAdapter mAdapter;
    public static int epcUnChangeTime = 0;
    public static int epcUnChangeMaxTime = 10;
    private int preSize = 0;
    int invCount = 0;
    int invMaxCount = 5;
    EsimUhfParams esimUhfParams;
    private Animation mRadarAnim;
    private Handler mHandler = new Handler();
    private EsimUhfHelper.EsimUhfListener uhfListener = new EsimUhfHelper.EsimUhfListener() {
        @Override
        public synchronized void onTagRead(List<UhfTag> tags) {
            Log.e(invCount + "======bug" + epcUnChangeTime, tags.toString());
            Log.e("Thread======", Thread.currentThread().toString());
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
                        if(numberText != null){
                            numberText.setText("" + files.size());
                        }
                    }
                });
            } else {
                epcUnChangeTime++;
                if (epcUnChangeTime >= epcUnChangeMaxTime) {//次扫描不到新标签，假定扫描完 Log.d(TAG, "invTags: " + epcUnChangeTime + "+次未找到新标签,假定扫描完");
                    if (files.size() != preSize) {
                        preSize = files.size();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
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
                        },200);

                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                allFiles.addAll(files);
                                if(roundImg != null){
                                    roundImg.clearAnimation();
                                }
                            }
                        });
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
    public AbstractPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData() {
        numberLayout.setVisibility(View.VISIBLE);
        roundImg.setVisibility(View.VISIBLE);
        seeDetail.setVisibility(View.VISIBLE);
        detailLayout.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        mAdapter = new FileBeanAdapter(files, this);
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
                    List<EpcFile> filterList = Stream.of(allFiles).filter(new Predicate<EpcFile>() {
                        @Override
                        public boolean test(EpcFile value) {
                            int i = value.getEpcCode().indexOf(assetsId);
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
        roundImg.startAnimation(mRadarAnim);
        esimUhfParams = new EsimUhfParams.Builder().antIndex(1, 2, 3, 4).build();
        startInvTags();
    }

    private void startInvTags() {
        epcUnChangeTime = 0;
        Log.d(TAG, "startInvTags: start inv...");
        preSize = 0;
        invCount = 1;
        //todo 开始转圈动画
        boolean isStartOk = EsimUhfHelper.getInstance().startReadTags(esimUhfParams, uhfListener);
        if (!isStartOk) {
            ToastUtils.showShort("读写器异常");
        }
    }

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
        mHandler.removeMessages(0);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(EsimUhfHelper.getInstance().isInvStart()){
            EsimUhfHelper.getInstance().stopRead();
        }
    }
}
