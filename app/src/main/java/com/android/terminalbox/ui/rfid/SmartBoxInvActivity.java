package com.android.terminalbox.ui.rfid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.terminalbox.MainActivity;
import com.android.terminalbox.R;
import com.android.terminalbox.core.bean.box.Oprecord;
import com.android.terminalbox.core.bean.box.Tag;
import com.android.terminalbox.core.dao.OprecordDao;
import com.android.terminalbox.core.dao.TagDao;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.uhf.EsimUhfHelper;
import com.android.terminalbox.uhf.EsimUhfParams;
import com.android.terminalbox.uhf.UhfTag;
import com.android.terminalbox.widget.LoadingDialog;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SmartBoxInvActivity extends BaseActivity {
    private static final String TAG = "SmartBoxInvActivity";
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_total_show)
    TextView tvTotalShow;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.btn_start_inv)
    Button btnStartInv;
    @BindView(R.id.btn_back_main)
    Button btnBackMain;

    Handler mainHandler = new Handler();
    public static int epcUnChangeTime = 0;
    public static int epcUnChangeMaxTime = 20;
    private static boolean isStart = true;
    EsimUhfParams esimUhfParams;

    private GestureDetectorCompat mDetector;

    private SimpleTagAdapter mAdapter;
    private List<String> currentBoxTags = new ArrayList<>();
    private TagDao tagDao;
    private OprecordDao oprecordDao;
    LoadingDialog loadingDialog;
    private int preSize=0;
    int invCount=0;
    int invMaxCount=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartbox_inv);
        ButterKnife.bind(this);
        initView();
        mDetector = new GestureDetectorCompat(this, new GestureListener());
    }


    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));


        mAdapter = new SimpleTagAdapter(currentBoxTags);
        rvList.setLayoutManager(layoutManager);
        rvList.setAdapter(mAdapter);
        rvList.addItemDecoration(divider);

        esimUhfParams = new EsimUhfParams.Builder().antIndex(1,2,3,4).build();
        mAdapter.notifyDataSetChanged();
        currentBoxTags.clear();
    }
    @OnClick(R.id.btn_sync)
    public void onSyncClick() {
        if(currentBoxTags.size()>0) {
            if (tagDao == null) {
                tagDao = BaseDb.getInstance().getTagDao();
            }
            if (oprecordDao == null) {
                oprecordDao = BaseDb.getInstance().getOprecordDao();
            }

            Oprecord current_op = new Oprecord();
            current_op.setOpType("sync");
            current_op.setUserName("mgr");
            oprecordDao.insertItem(current_op);
            Log.d(TAG, "onSyncClick: " + current_op.toString());
            tagDao.deleteByOpId(current_op.getId());
            List<Tag> tags = new ArrayList<>();
            for (String epc:currentBoxTags){
                Tag tag=new Tag();
                tag.setOpId(current_op.getId());
                tag.setTagEpc(epc);
                tags.add(tag);
            }
            tagDao.insertItems(tags);
            showLongToast(currentBoxTags.size() + "条数据同步完成");
        }else{
            showLongToast("尚未盘点,无数据");
        }
    }

    @OnClick(R.id.btn_back_main)
    public void onBackClick() {
        JumpToActivity(MainActivity.class);
    }

    @OnClick(R.id.btn_start_inv)
    public void onInvClick() {
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
    private void startInvTags() {
        epcUnChangeTime = 0;
        loadingDialog=new LoadingDialog(SmartBoxInvActivity.this,"盘点中...");
        loadingDialog.show();
        Log.d(TAG, "startInvTags: start inv...");
        preSize=0;
        invCount=1;
        boolean isStartOk= EsimUhfHelper.getInstance().startReadTags(esimUhfParams,uhfListener);
        if (!isStartOk) {
            Toast.makeText(this, "读写器异常", Toast.LENGTH_SHORT);
        }
    }
    private EsimUhfHelper.EsimUhfListener uhfListener=new EsimUhfHelper.EsimUhfListener() {
        @Override
        public void onTagRead(List<UhfTag> tags) {
            List<String> epcs = Stream.of(tags).map(new Function<UhfTag, String>() {
                @Override
                public String apply(UhfTag uhfTags) {
                    return uhfTags.getEpc();
                }
            }).collect(Collectors.toList());//Log.d(TAG, "invTags: 本次盘点到标签" + epcs.size() + "    " + epcs.toString());
            epcs.removeAll(currentBoxTags);//            Log.d(TAG, "invTags: 其中以前未盘到" + epcs.size() + "    " + epcs.toString());
            if (epcs.size() > 0) {//扫描到新标签
                epcUnChangeTime = 0;
                currentBoxTags.addAll(epcs);//Log.d(TAG, "invTags: 本次盘点后总标签" + inBoxEpcsTemp.size() + "    " + inBoxEpcsTemp.toString());
            } else {
                epcUnChangeTime++;
                if (epcUnChangeTime >= epcUnChangeMaxTime) {//次扫描不到新标签，假定扫描完 Log.d(TAG, "invTags: " + epcUnChangeTime + "+次未找到新标签,假定扫描完");
                    if(currentBoxTags.size()!=preSize) {
                        preSize=currentBoxTags.size();
                        mainHandler.post(() -> {
                            tvTotalShow.setText("" + currentBoxTags.size());
                            mAdapter.notifyDataSetChanged();
                        });
                    }

                    EsimUhfHelper.getInstance().stopRead();
                    if(invCount<invMaxCount) {
                        invCount++;
                        EsimUhfHelper.getInstance().startReadTags(esimUhfParams, uhfListener);
                    }else{
                        loadingDialog.dismiss();
                    }
                }
            }
        }
    };
    @Override
    void afterRequestPermission(int requestCode, boolean isAllGranted) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e2.getX() - e1.getX() > 120) {
                //JumpToActivity(LoginActivity.class);
                return true;
            }
            return false;
        }
    }
}
