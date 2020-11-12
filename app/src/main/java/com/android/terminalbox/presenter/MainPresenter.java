package com.android.terminalbox.presenter;

import com.android.terminalbox.base.presenter.BasePresenter;
import com.android.terminalbox.contract.MainContract;
import com.android.terminalbox.contract.RecognizeContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.http.widget.BaseObserver;
import com.android.terminalbox.utils.RxUtils;

import java.util.List;

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    public MainPresenter() {
        super();
    }

    @Override
    public void getAllUserInfo() {
        addSubscribe(DataManager.getInstance().getAllUserInfo()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<BaseResponse<List<UserInfo>>>(mView, false) {
                    @Override
                    public void onNext(BaseResponse<List<UserInfo>> listBaseResponse) {
                        mView.handelAllUserInfo(listBaseResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }));
    }

    @Override
    public void updateFeature(List<FaceFeatureBody> faceFeatures) {
        addSubscribe(DataManager.getInstance().updateFeatures(faceFeatures)
        .compose(RxUtils.rxSchedulerHelper())
        .subscribeWith(new BaseObserver<BaseResponse<List<UserInfo>>>(mView, false) {
            @Override
            public void onNext(BaseResponse<List<UserInfo>> listBaseResponse) {
                mView.handleUpdateFeature(listBaseResponse);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        }));
    }


}
