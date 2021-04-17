package com.android.terminalbox.presenter;

import android.util.Log;

import com.android.terminalbox.base.presenter.BasePresenter;
import com.android.terminalbox.contract.RecognizeContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;
import com.android.terminalbox.core.http.widget.BaseObserver;
import com.android.terminalbox.utils.RxUtils;

import java.sql.Date;
import java.util.List;

public class RecognizePresenter extends BasePresenter<RecognizeContract.View> implements RecognizeContract.Presenter {

    public RecognizePresenter() {
        super();
    }


    @Override
    public void login(UserInfo userInfo) {
        addSubscribe(DataManager.getInstance().login(userInfo)
                .compose(RxUtils.handleResult())
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<UserLoginResponse>(mView, false) {
                    @Override
                    public void onNext(UserLoginResponse userLoginResponse) {
                        mView.handleLogin(userLoginResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.e("Throwable",e.toString());
                    }
                }));
    }
}
