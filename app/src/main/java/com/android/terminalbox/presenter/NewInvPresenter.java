package com.android.terminalbox.presenter;

import com.android.terminalbox.base.presenter.BasePresenter;
import com.android.terminalbox.contract.NewInvContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.cmb.AssetsListPage;
import com.android.terminalbox.core.http.widget.BaseObserver;
import com.android.terminalbox.utils.RxUtils;
import java.util.ArrayList;
import java.util.List;

public class NewInvPresenter extends BasePresenter<NewInvContract.View> implements NewInvContract.Presenter {

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
