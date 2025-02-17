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
import com.android.terminalbox.core.bean.user.EpcFile;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.OrderBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.mqtt.MqttServer;
import com.android.terminalbox.mqtt.RylaiMqttCallback;
import com.android.terminalbox.presenter.MainPresenter;
import com.android.terminalbox.ui.SettingActivity;
import com.android.terminalbox.ui.inventory.NewInvActivity;
import com.android.terminalbox.ui.recognize.RecognizeActivity;
import com.android.terminalbox.ui.unlock.NewUnlockActivity;
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
import com.esim.rylai.smartbox.ekey.EkeyManager;
import com.esim.rylai.smartbox.uhf.UhfManager;
import com.esim.rylai.smartbox.uhf.UhfReader;
import com.google.gson.Gson;

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
    private List<UserInfo> users = new ArrayList<>();
    /**
     * 用于特征提取的引擎
     */
    private FaceEngine frEngine;
    private IotDevice iotDevice;
    private RylaiMqttCallback rylaiMqttCallback = new RylaiMqttCallback() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.e(TAG, "Mqtt Message onSuccess");
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.e(TAG, "Mqtt Message onFailure");
            Log.d(TAG, "Mqtt Message Action==============失败");
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Log.e(TAG, "Mqtt Message connectComplete");
        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.e(TAG, "Mqtt Message connectionLost");
        }


        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.e(TAG, "Mqtt Message deliveryComplete");
            Log.d(TAG, "Mqtt Message==============消息上传完成");

        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.e(TAG, "Mqtt Message messageArrived");
            if ("app/devices/15aa68f3183311ebb7260242ac120004_uniqueCode002/insts".equals(topic)) {
                OrderBody orderBody = new Gson().fromJson(new String(message.getPayload()), OrderBody.class);
                int userId = orderBody.getInstData().getUserId();
                List<UserInfo> users = BaseApplication.getInstance().getUsers();
                for (UserInfo user : users) {
                    if(userId == user.getId()){
                        BaseApplication.getInstance().setCurrentUer(user);
                        break;
                    }
                }
                Intent intent = new Intent(MainActivity.this, NewUnlockActivity.class);
                intent.putExtra("relevanceId", orderBody.getInstData().getRelevanceId());
                startActivity(intent);
            }
            if ("app/userface/update".equals(topic)) {
                mPresenter.getAllUserInfo();
            }
        }

    };

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
        isNeedGoHomeActivity(false);
        activeEngine();
        frEngine = new FaceEngine();
        frEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, ASF_OP_0_ONLY,
                16, 6, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);
        mPresenter.getAllUserInfo();
        //初始化mqtt
        MqttConnect mqttConnect = new MqttConnect();
        mqttConnect.start();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<EpcFile> allEpcFile = BaseDb.getInstance().getEpcFileDao().findEpcFileByBox("box002");
        fileNumber.setText(String.valueOf(allEpcFile.size()));
    }

    @OnClick({R.id.btn_inv, R.id.btn_access, R.id.bt_change_org,R.id.iv_title})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.btn_inv:
                JumpToActivity(NewInvActivity.class);
                break;
            case R.id.btn_access:
                JumpToActivity(RecognizeActivity.class);
                break;
            case R.id.bt_change_org:
                //mPresenter.getAllUserInfo();
                ConfigUtil.setFtOrient(MainActivity.this, ASF_OP_0_ONLY);
               /* UserInfo userInfo = new UserInfo();
                userInfo.setId(3);
                BaseApplication.getInstance().setCurrentUer(userInfo);
                JumpToActivity(NewUnlockActivity.class);*/
                break;
            case R.id.iv_title:
               intoSettings();
                break;
        }
    }

    @Override
    public void handelAllUserInfo(BaseResponse<List<UserInfo>> userInfos) {
        if (200000 == userInfos.getCode()) {
            //获取所有用户，解析人脸特征值
            registerFaceAndGetFeature(userInfos.getData());
        }
    }

    @Override
    public void handleUpdateFeature(BaseResponse<List<UserInfo>> userInfos) {
        if(200000 == userInfos.getCode()){
            //ToastUtils.showShort("更新特征值成功");
        }else {
            ToastUtils.showShort("更新特征值失败：" + userInfos.getMessage());
        }

    }

    @SuppressLint("StaticFieldLeak")
    public void registerFaceAndGetFeature(final List<UserInfo> userInfos) {
        new AsyncTask<Void, Void, List<UserInfo>>() {
            @Override
            protected List<UserInfo> doInBackground(Void... params) {
                ArrayList<FaceFeatureBody> faceFeatureBodys = new ArrayList<>();
                try {
                    //要在子线程中使用，否则会报错
                    for (UserInfo userInfo : userInfos) {
                        if("0".equals(userInfo.getFaceStatus())){
                            processImage(userInfo);
                            FaceFeatureBody faceFeatureBody = new FaceFeatureBody();
                            faceFeatureBody.setFaceFeature(userInfo.getFaceFeature());
                            faceFeatureBody.setId(userInfo.getId());
                            faceFeatureBodys.add(faceFeatureBody);
                        }
                    }
                    //提取完人脸特征值后将用户提交的后端,本地数据库,内存
                    BaseApplication.getInstance().getUsers().clear();
                    BaseApplication.getInstance().getUsers().addAll(userInfos);
                    BaseDb.getInstance().getUserDao().insertItems(userInfos);
                    mPresenter.updateFeatures(faceFeatureBodys);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return userInfos;
            }

            @Override
            protected void onPostExecute(List<UserInfo> files) {

            }
        }.execute();
    }

    public void processImage(UserInfo userInfo) {
        if (userInfo.getFaceImg() == null) {
            return;
        }
        File file = null;
        try {
            file = Glide.with(MainActivity.this).downloadOnly().load(userInfo.getFaceImg()).submit().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        if (file != null) {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        if (bitmap == null) {
            return;
        }
        if (frEngine == null) {
            return;
        }
        // 接口需要的bgr24宽度必须为4的倍数
        bitmap = ArcSoftImageUtil.getAlignedBitmap(bitmap, true);
        if (bitmap == null) {
            return;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // bitmap转bgr24
        byte[] bgr24 = ArcSoftImageUtil.createImageData(bitmap.getWidth(), bitmap.getHeight(), ArcSoftImageFormat.BGR24);
        int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);
        if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) {
            Log.e(TAG, "failed to transform bitmap to imageData, code is " + transformCode);
            return;
        }
        List<FaceInfo> faceInfoList = new ArrayList<>();
        //人脸检测
        int detectCode = frEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList);
        if (detectCode != 0 || faceInfoList.size() == 0) {
            Log.e(TAG, "face detection finished, code is " + detectCode + ", face num is " + faceInfoList.size());
            return;
        }
        FaceFeature mainFeature = new FaceFeature();
        int code = frEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), mainFeature);
        if (code != ErrorInfo.MOK) {
            mainFeature = null;
            Log.e(TAG, "code === " + code);
        }
        if (mainFeature != null) {
            try {
                String featureStr = Base64.encodeToString(mainFeature.getFeatureData(), Base64.DEFAULT);
                userInfo.setFaceFeature(featureStr);
                userInfo.setFaceStatus("2");
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    //yhm start 1105
    class MqttConnect extends Thread {
        @Override
        public void run() {
            super.run();
            if (iotDevice == null) {
                iotDevice = new IotDevice();
                iotDevice.setIotHost("117.34.118.157");
                iotDevice.setMqttPort("20008");
                iotDevice.setPordId("15aa68f3183311ebb7260242ac120004");
                iotDevice.setDevVerify("uniqueCode002");
                iotDevice.setDevId(iotDevice.getPordId()+"_"+iotDevice.getDevVerify());
                iotDevice.setDevPassword("smartbox");
            }
            MqttServer.getInstance().init(MainActivity.this, iotDevice, rylaiMqttCallback);
        }
    }
    //yhm end 1105

    public String getWeekDay(){
        int weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String day = "";
        switch (weekDay){
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return day;
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
}
