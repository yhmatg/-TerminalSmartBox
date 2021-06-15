package com.android.terminalbox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

import com.android.terminalbox.app.BaseApplication;
import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.common.Constants;
import com.android.terminalbox.contract.MainContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.box.IotDevice;
import com.android.terminalbox.core.bean.cmb.AssetFilterParameter;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;
import com.android.terminalbox.core.bean.cmb.TerminalInfo;
import com.android.terminalbox.core.bean.cmb.TerminalLoginPara;
import com.android.terminalbox.core.bean.user.EpcFile;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.OrderBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.mqtt.MqttServer;
import com.android.terminalbox.mqtt.RylaiMqttCallback;
import com.android.terminalbox.old.EkeyManager;
import com.android.terminalbox.presenter.MainPresenter;
import com.android.terminalbox.ui.SettingActivity;
import com.android.terminalbox.ui.inventory.NewInvActivity;
import com.android.terminalbox.ui.recognize.RecognizeActivity;
import com.android.terminalbox.ui.unlock.NewUnlockActivity;
import com.android.terminalbox.utils.Md5Util;
import com.android.terminalbox.utils.ToastUtils;
import com.android.terminalbox.utils.box.ConfigUtil;
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.RuntimeABI;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.bumptech.glide.Glide;
import com.esim.rylai.smartbox.uhf.UhfManager;
import com.esim.rylai.smartbox.uhf.UhfReader;
import com.google.gson.Gson;
import com.multilevel.treelist.Node;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_0_ONLY;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {
    private static String TAG = "MainActivity";
    @BindView(R.id.week_text)
    TextClock weekText;
    @BindView(R.id.time_text)
    TextClock timeText;
    @BindView(R.id.file_number)
    TextView fileNumber;
    @BindString(R.string.loc_id)
    String locId;
    @BindString(R.string.loc_name)
    String locName;
    private List<UserInfo> users = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 100;
    private AssetFilterParameter conditions = new AssetFilterParameter();
    private boolean isLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToolbar() {

    }

    @Override
    public MainPresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void initEventAndData() {
        List<Node> mSelectAssetsLocations = new ArrayList<>();
        mSelectAssetsLocations.add(new Node(locId, "-1", locName));
        conditions.setmSelectAssetsLocations(mSelectAssetsLocations);
        isNeedGoHomeActivity(false);
        activeEngine();
        weekText.setFormat24Hour("EEEE");
        timeText.setFormat24Hour("MM/dd HH:mm");
        EkeyManager.getInstance().init(this, "/dev/ttyXRUSB1", 9600).config( null, 2000, true,1, 2);
        EkeyManager.getInstance().setShowLog(true);
        Set<UhfReader> uhfReaders=new HashSet<>();
        int[] ants=new int[]{1,2,3,4};
        String ipOne = DataManager.getInstance().getIpOne();
        String ipTwo = DataManager.getInstance().getIpTwo();
        ToastUtils.showShort("ipOne===" + ipOne +"      ipTwo===" + ipTwo);
        UhfReader reader1=new UhfReader(ipOne);
        reader1.setAnts(ants);
        UhfReader reader2=new UhfReader(ipTwo);
        reader2.setAnts(ants);
        uhfReaders.add(reader1);
        uhfReaders.add(reader2);
        UhfManager.getInstance().confReaders(uhfReaders).setShowLog(true);
        UhfManager.getInstance().confReadTagFilter(null);
        UhfManager.getInstance().setShowLog(true);
        ConfigUtil.setFtOrient(MainActivity.this, ASF_OP_0_ONLY);
        //mPresenter.terminalLogin(new TerminalLoginPara("banma001","123456"));
        UserInfo userInfo = new UserInfo();
        userInfo.setUser_name("admin01");
        userInfo.setUser_password(Md5Util.getMD5("123456"));
        mPresenter.login(userInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.fetchPageAssetsList(pageSize, currentPage, "", "", conditions.toString());
    }

    @OnClick({R.id.btn_inv, R.id.btn_access, R.id.bt_change_org,R.id.iv_title})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.btn_inv:
                if(isLogin){
                    JumpToActivity(NewInvActivity.class);
                }
                break;
            case R.id.btn_access:
                JumpToActivity(RecognizeActivity.class);
                break;
            case R.id.bt_change_org:
                ConfigUtil.setFtOrient(MainActivity.this, ASF_OP_0_ONLY);
                break;
            case R.id.iv_title:
               intoSettings();
                break;
        }
    }

    @Override
    public void handleTerminalLogin(BaseResponse<TerminalInfo> terminalInfo) {
        if ("200000".equals(terminalInfo.getCode())) {
            DataManager.getInstance().setToken(terminalInfo.getResult().getId());
            isLogin = true;
        }
    }

    @Override
    public void handleFetchPageAssetsList(List<AssetsListItemInfo> assetsInfos) {
        int i = 0;
        for (AssetsListItemInfo tool : assetsInfos) {
            if (locName.equals(tool.getLoc_name())) {
                if (tool.getAst_used_status() == 0) {
                    i ++;
                }
            }
        }
        fileNumber.setText(String.valueOf(i));
    }

    @Override
    public void handleLogin(UserLoginResponse userLoginResponse) {
        if(userLoginResponse != null){
            DataManager.getInstance().setToken(userLoginResponse.getToken());
            isLogin = true;
        }
    }

    /**
     * 所需的所有权限信息
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE

    };
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    public void activeEngine() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i(TAG, "subscribe: getRuntimeABI() " + runtimeABI);

                long start = System.currentTimeMillis();
                int activeCode = FaceEngine.activeOnline(MainActivity.this, Constants.APP_ID, Constants.SDK_KEY);
                Log.i(TAG, "subscribe cost: " + (System.currentTimeMillis() - start));
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            showToast(getString(R.string.active_success));
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast(getString(R.string.already_activated));
                        } else {
                            showToast(getString(R.string.active_failed, activeCode));
                        }
                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(MainActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i(TAG, activeFileInfo.toString());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    // 需要点击几次 就设置几
    long [] mHits = null;

    public void intoSettings() {
        if (mHits == null) {
            mHits = new long[5];
        }
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);//把从第二位至最后一位之间的数字复制到第一位至倒数第一位
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();//记录一个时间
        if (SystemClock.uptimeMillis() - mHits[0] <= 1000) {//一秒内连续点击。
            mHits = null;	//这里说明一下，我们在进来以后需要还原状态，否则如果点击过快，第六次，第七次 都会不断进来触发该效果。重新开始计数即可
            JumpToActivity(SettingActivity.class);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EkeyManager.getInstance().deInit();
    }
}
