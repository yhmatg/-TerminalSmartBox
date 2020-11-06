package com.android.terminalbox.ui.face;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.terminalbox.MainActivity;
import com.android.terminalbox.R;
import com.android.terminalbox.app.BaseApplication;
import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.common.ConstFromSrc;
import com.android.terminalbox.core.bean.box.Oprecord;
import com.android.terminalbox.core.bean.box.Tag;
import com.android.terminalbox.core.dao.OprecordDao;
import com.android.terminalbox.core.dao.TagDao;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.devservice.ekey.EkeyServer;
import com.android.terminalbox.devservice.ekey.EkeyStatusChangeListener;
import com.android.terminalbox.mqtt.MqttServer;
import com.android.terminalbox.mqtt.own.Props;
import com.android.terminalbox.mqtt.own.ResultProp;
import com.android.terminalbox.uhf.EsimUhfHelper;
import com.android.terminalbox.uhf.EsimUhfParams;
import com.android.terminalbox.uhf.UhfTag;
import com.android.terminalbox.ui.rfid.SimpleTagAdapter;
import com.android.terminalbox.utils.StringUtils;
import com.android.terminalbox.widget.LoadingDialog;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.gson.Gson;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InOutActivity extends BaseActivity {
    private static final String TAG = "InOutActivity";
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_total_show)
    TextView tvTotalShow;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.img_face)
    ImageView imgFace;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_name_show)
    TextView tvNameShow;
    @BindView(R.id.tv_key_status)
    TextView tvKeyStatus;
    EkeyServer ekeyServer;

    private SimpleTagAdapter mTagAdapter;
    private Set<String> currentBoxTags = new HashSet<>();
    private List<String> changeEpcs = new ArrayList<>();
    private List<String> db_epcs=new ArrayList<>();

    Handler mainHandler = new Handler();
    public static int epcUnChangeTime = 0;
    public static int epcUnChangeMaxTime = 20;
    private static boolean isStart = true;
    EsimUhfParams esimUhfParams;
    private GestureDetectorCompat mDetector;
    ImageOptions imageOptions;
    private String userName;
    private TagDao tagDao;
    private static Oprecord current_op=null;
    OprecordDao oprecordDao;
    private String opType;

    LoadingDialog loadingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_in_out;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public AbstractPresenter initPresenter() {
        return null;
    }

    @Override
    protected void initEventAndData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle!=null){
            userName = bundle.getString("userName");
            opType=bundle.getString(ConstFromSrc.activityFrom);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ButterKnife.bind(this);
        initView();
        mDetector = new GestureDetectorCompat(this, new GestureListener());
    }


    private void initView() {
        if(opType.equals(ConstFromSrc.tagsOut)) {
            tvTotal.setText("出库统计：");
        }else if(opType.equals(ConstFromSrc.tagsIn)){
            tvTotal.setText("入库统计：");
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_divider));


        mTagAdapter = new SimpleTagAdapter(changeEpcs);
        rvList.setLayoutManager(layoutManager);
        rvList.setAdapter(mTagAdapter);
        rvList.addItemDecoration(divider);

        esimUhfParams = new EsimUhfParams.Builder().antIndex(1,2,3,4).build();

        /*imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(240), DensityUtil.dip2px(240))
                .setRadius(DensityUtil.dip2px(30))
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.down)
                .setFailureDrawableId(R.drawable.user_icon)
                .build();
        if (!StringUtils.isEmpty(userName)) {
            String path = this.getFilesDir().getAbsolutePath() + File.separator + "register" + File.separator + "imgs" + File.separator + userName + ".jpg";
            try {
                x.image().bind(imgFace, path, imageOptions);
            } catch (Exception ex) {

            }

        }*/
        tvNameShow.setText(userName);
        ekeyServer = EkeyServer.getInstance();
        ekeyServer.addStatusChangeListenner(ekeyStatusChangeListener);

        new Thread(() -> {
            try {
                Looper.prepare();
                if(oprecordDao==null) {
                    oprecordDao = BaseDb.getInstance().getOprecordDao();
                }
                List<Oprecord> oprecords = oprecordDao.findOprecords();
                Oprecord latestOp = null;
                if(oprecords.size() > 0){
                    latestOp = oprecords.get(0);
                }
                if(latestOp!=null){
                    tagDao=BaseDb.getInstance().getTagDao();
                    List<Tag> dbtags=tagDao.findTagByOpId(latestOp.getId());
                    Log.d(TAG, "================getDbTags: tagsSize"+dbtags.size()+"============");
                    db_epcs=Stream.of(dbtags).map(Tag::getTagEpc).collect(Collectors.toList());
                    Log.d(TAG, "================getDbTags: "+db_epcs.toString()+"============");
                }
                Thread.sleep(1000);
                ekeyServer.openEkey();
                Log.d(TAG, "=============================开锁============================== ");
                Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
    EkeyStatusChangeListener ekeyStatusChangeListener = ekeyStatus -> {
        switch (ekeyStatus) {
            case OPEN:
                Log.d(TAG, "=========ekey open============: ");
                if (!StringUtils.isEmpty(BaseApplication.getRelevanceId())){
                    openReport(BaseApplication.getRelevanceId());
                }
                runOnUiThread(() -> tvKeyStatus.setText("锁状态：开启"));
                break;
            case CLOSED:
                Log.d(TAG, "=========ekey close============: ");
                if (!StringUtils.isEmpty(BaseApplication.getRelevanceId())){
                   closeReport(BaseApplication.getRelevanceId());
                }
                runOnUiThread(() -> {
                    tvKeyStatus.setText("锁状态：关闭");
                });
                invTags();
                break;
        }
    };

//    @OnClick({R.id.btn_open_key,R.id.btn_inv_again, R.id.btn_exit})
    @OnClick({R.id.btn_inv_again, R.id.btn_exit})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_open_key:
//                currentBoxTags.clear();
//                ekeyServer.openEkey();
//                break;
            case R.id.btn_inv_again:
                new Thread(() -> {
                    try {
                        Looper.prepare();
                        invTags();
                        Looper.loop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                break;

            case R.id.btn_exit:
                if(changeEpcs!=null &&changeEpcs.size()>0) {
                    current_op = new Oprecord();
                    current_op.setOpType(opType);
                    current_op.setUserName(userName);
                    oprecordDao.insertItem(current_op);
                    tagDao.deleteByOpId(current_op.getId());
                    ArrayList<Tag> tags = new ArrayList<>();
                    for (String currentBoxTag : currentBoxTags) {
                        Tag tag=new Tag();
                        tag.setOpId(current_op.getId());
                        tag.setTagEpc(currentBoxTag);
                        tags.add(tag);
                    }
                    tagDao.insertItems(tags);
                }
                JumpToActivity(MainActivity.class);
                break;
        }
    }
    private void invTags() {
        epcUnChangeTime = 0;
        loadingDialog=new LoadingDialog(InOutActivity.this,"统计中...");
        loadingDialog.show();
        boolean isStartOk = EsimUhfHelper.getInstance().startReadTags(esimUhfParams, new EsimUhfHelper.EsimUhfListener() {
            @Override
            public void onTagRead(List<UhfTag> tags) {
                List<String> epcs = Stream.of(tags).map(uhfTags -> uhfTags.getEpc()).collect(Collectors.toList());//Log.d(TAG, "invTags: 本次盘点到标签" + epcs.size() + "    " + epcs.toString());
                epcs.removeAll(currentBoxTags);// Log.d(TAG, "invTags: 其中以前未盘到" + epcs.size() + "    " + epcs.toString());
                if (epcs.size() > 0) {//扫描到新标签
                    epcUnChangeTime = 0;
                    currentBoxTags.addAll(epcs);
                } else {
                    epcUnChangeTime++;
                    if (epcUnChangeTime >= epcUnChangeMaxTime) {//多次扫描不到新标签，假定扫描完Log.d(TAG, "invTags: " + epcUnChangeTime + "次未找到新标签,假定扫描完");
                        EsimUhfHelper.getInstance().stopRead();
                        changeEpcs.clear();
                        if (opType.equals(ConstFromSrc.tagsIn)) {
                            changeEpcs.addAll(Stream.of(currentBoxTags).filter(epc -> !db_epcs.contains(epc)).distinct().collect(Collectors.toList()));
                        } else {
                            changeEpcs.addAll(Stream.of(db_epcs).filter(epc -> !currentBoxTags.contains(epc)).distinct().collect(Collectors.toList()));
                        }
                        Log.d(TAG, "==========invTags: dbEpc" + db_epcs.toString() + "==========");
                        Log.d(TAG, "==========invTags: currentEpc" + currentBoxTags.toString() + "==========");
                        Log.d(TAG, "==========invTags: changeEpc" + changeEpcs.toString() + "==========");
                        mainHandler.post(() -> {
                            if (loadingDialog != null) {
                                loadingDialog.dismiss();
                            }
                            tvTotalShow.setText("" + changeEpcs.size());
                            mTagAdapter.notifyDataSetChanged();
                        });
                    } else {
                        Log.d(TAG, "invTags: 第" + epcUnChangeTime + "次无新标签");
                    }
                }
            }
        });
        Log.d(TAG, "invTags: isStartOk:" + isStartOk);
        if (!isStartOk) {
            Toast.makeText(this, "读写器异常", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void afterRequestPermission(int requestCode, boolean isAllGranted) {

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
//                JumpToActivity(LoginActivity.class);
                ekeyServer.openEkey();
                return true;
            }
            return false;
        }
    }

    //发布开锁消息
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

    public void nvReport(String relevanceId) {
        Props props = new Props();
        ArrayList<ResultProp> resultProps = new ArrayList<>();
        ResultProp resultProp = new ResultProp();
        resultProp.setCap_id("id3");
        resultProp.setRelevance_id(relevanceId);
        resultProp.setData_event_time(System.currentTimeMillis());
        ResultProp.Prop prop = new ResultProp.Prop();
        ArrayList<String> inList = new ArrayList<>();
        inList.add("epc01");
        ArrayList<String> outList = new ArrayList<>();
        outList.add("epc02");
        prop.setRfid_in(inList);
        prop.setRfid_out(outList);
        resultProp.setProp(prop);
        resultProps.add(resultProp);
        props.setProps(resultProps);
        MqttServer.getInstance().reportProperties(new Gson().toJson(props));
    }
}
