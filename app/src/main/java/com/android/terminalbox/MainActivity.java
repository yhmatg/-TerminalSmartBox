package com.android.terminalbox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.View;

import com.android.terminalbox.app.BaseApplication;
import com.android.terminalbox.base.activity.BaseActivity;
import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.common.ConstFromSrc;
import com.android.terminalbox.common.Constants;
import com.android.terminalbox.contract.MainContract;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.faceserver.FaceServer;
import com.android.terminalbox.presenter.MainPresenter;
import com.android.terminalbox.ui.face.RecognizeActivity;
import com.android.terminalbox.ui.inventory.InventoryActivity;
import com.android.terminalbox.ui.inventory.InventoryActivity_ViewBinding;
import com.android.terminalbox.ui.rfid.SmartBoxInvActivity;
import com.android.terminalbox.utils.box.ConfigUtil;
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.RuntimeABI;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.arcsoft.face.enums.DetectFaceOrientPriority.ASF_OP_90_ONLY;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {
    private static String TAG = "MainActivity";
    private List<UserInfo> users = new ArrayList<>();
    /**
     * 用于特征提取的引擎
     */
    private FaceEngine frEngine;

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
        activeEngine();
        frEngine = new FaceEngine();
        frEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, 6, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);
        mPresenter.getAllUserInfo();
    }

    @OnClick({R.id.btn_inv, R.id.btn_access, R.id.bt_change_org})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.btn_inv:
                JumpToActivity(InventoryActivity.class);
                break;
            case R.id.btn_access:
                mPresenter.getAllUserInfo();
                break;
            case R.id.bt_change_org:
                bundle.putString(ConstFromSrc.activityFrom, ConstFromSrc.tagsIn);
                ConfigUtil.setFtOrient(this, ASF_OP_90_ONLY);
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
                        processImage(userInfo);
                        FaceFeatureBody faceFeatureBody = new FaceFeatureBody();
                        faceFeatureBody.setFaceFeature(userInfo.getFaceFeature());
                        faceFeatureBody.setId(userInfo.getId());
                        faceFeatureBodys.add(faceFeatureBody);
                    }
                    //提取完人脸特征值后将用户提交的后端,本地数据库,内存
                    BaseApplication.getInstance().getUsers().clear();
                    BaseApplication.getInstance().getUsers().addAll(userInfos);
                    BaseDb.getInstance().getUserDao().insertItems(userInfos);
                    mPresenter.updateFeature(faceFeatureBodys);
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
                userInfo.setFaceFeature(new String(mainFeature.getFeatureData(), "ISO_8859_1"));
                userInfo.setFaceStatus("2");
            } catch (UnsupportedEncodingException e) {
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
}
