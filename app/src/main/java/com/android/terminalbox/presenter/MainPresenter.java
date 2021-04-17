package com.android.terminalbox.presenter;

import com.android.terminalbox.base.presenter.BasePresenter;
import com.android.terminalbox.contract.MainContract;
import com.android.terminalbox.contract.RecognizeContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.cmb.AssetsListPage;
import com.android.terminalbox.core.bean.cmb.TerminalInfo;
import com.android.terminalbox.core.bean.cmb.TerminalLoginPara;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.http.widget.BaseObserver;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.utils.CommonUtils;
import com.android.terminalbox.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    public MainPresenter() {
        super();
    }

    @Override
    public void terminalLogin(TerminalLoginPara terminalLoginPara) {
        addSubscribe(DataManager.getInstance().terminalLogin(terminalLoginPara)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<BaseResponse<TerminalInfo>>(mView, false) {
                    @Override
                    public void onNext(BaseResponse<TerminalInfo> terminalInfoBaseResponse) {
                        mView.handleTerminalLogin(terminalInfoBaseResponse);
                    }
                }));
    }

    @Override
    public void fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions) {
        addSubscribe(DataManager.getInstance().fetchPageAssetsList(size, page, patternName, userRealName, conditions)
                .compose(RxUtils.rxSchedulerHelper())
                .compose(RxUtils.handleResult())
                .subscribeWith(new BaseObserver<AssetsListPage>(mView, false) {
                    @Override
                    public void onNext(AssetsListPage assetsListPage) {
                        if (page <= assetsListPage.getPages()) {
                            mView.handleFetchPageAssetsList(assetsListPage.getList());
                        } else {
                            mView.handleFetchPageAssetsList(new ArrayList<>());
                        }
                    }
                }));
    }
}
