package com.android.terminalbox.base.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.terminalbox.MainActivity;
import com.android.terminalbox.R;
import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.utils.CommonUtils;

/**
 * MVP模式的Base Activity
 *
 * @author yhm
 * @date 2017/11/28
 */

public abstract class BaseActivity<T extends AbstractPresenter> extends AbstractSimpleActivity implements
        AbstractView {

    protected T mPresenter;

    private MaterialDialog dialog;
    private CountDownTimer countDownTimer;
    private boolean mNeedGoHome = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenter();
        onViewCreated();
        initEventAndData();
        initCountDownTimer();
    }

    private void initCountDownTimer() {
        countDownTimer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(BaseActivity.this, MainActivity.class));
                finish();
            }
        };
    }


    public abstract T initPresenter();

    @Override
    protected void onViewCreated() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
        super.onDestroy();
    }


    @Override
    public void useNightMode(boolean isNight) {
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate();
    }

    @Override
    public void showErrorMsg(String errorMsg) {
        CommonUtils.showSnackMessage(this, errorMsg);
    }

    @Override
    public void showNormal() {

    }

    @Override
    public void showError() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void showCollectSuccess() {

    }

    @Override
    public void showCancelCollectSuccess() {

    }

    @Override
    public void showLoginView() {

    }

    @Override
    public void showLogoutView() {

    }

    @Override
    public void showToast(String message) {
        CommonUtils.showMessage(this, message);
    }

    @Override
    public void showSnackBar(String message) {
        CommonUtils.showSnackMessage(this, message);
    }

    public void showDialog(String title) {
        if (dialog != null) {
            dialog.show();
        } else {
            //MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(this, title, true);
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .title(title)
                    .progress(true, 0)
                    .progressIndeterminateStyle(true)
                    .canceledOnTouchOutside(false)
                    .backgroundColorRes(R.color.white)
                    .keyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {//如果是按下，则响应，否则，一次按下会响应两次
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    //activity.onBackPressed();

                                }
                            }
                            return false;//false允许按返回键取消对话框，true除了调用取消，其他情况下不会取消
                        }
                    });
            dialog = builder.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 初始化数据
     */
    protected abstract void initEventAndData();

    @Override
    public void startLoginActivity() {
    }

    public void JumpToActivity(Class activityClass, Bundle bundle) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    public void JumpToActivity(Class activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    protected boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    public void afterRequestPermission(int requestCode, boolean isAllGranted) {

    }

    ;

    public void isNeedGoHomeActivity(boolean isNeedGoHome) {
        mNeedGoHome = isNeedGoHome;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mNeedGoHome){
            countDownTimer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mNeedGoHome){
            countDownTimer.cancel();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mNeedGoHome){
            switch (ev.getAction()) {
                //获取触摸动作，如果ACTION_UP，计时开始。
                case MotionEvent.ACTION_UP:
                    countDownTimer.start();
                    break;
                //否则其他动作计时取消
                default:
                    countDownTimer.cancel();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
