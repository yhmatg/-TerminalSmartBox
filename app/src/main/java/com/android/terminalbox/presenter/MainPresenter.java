package com.android.terminalbox.presenter;

import com.android.terminalbox.base.presenter.BasePresenter;
import com.android.terminalbox.contract.MainContract;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;
import com.android.terminalbox.core.http.widget.BaseObserver;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.utils.RxUtils;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    public MainPresenter() {
        super();
    }

    @Override
    public void fetchLocalFreeAssets(int status) {
        addSubscribe(getLocalFreeAssets(status)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<List<AssetsListItemInfo>>(mView, false) {
                    @Override
                    public void onNext(List<AssetsListItemInfo> assetsListItemInfos) {
                        mView.handleFetchLocalFreeAssets(assetsListItemInfos);
                    }
                }));
    }

    private Observable<List<AssetsListItemInfo>> getLocalFreeAssets(int status) {
        return Observable.create(emitter -> {
            List<AssetsListItemInfo> freeAssets = new ArrayList<>();
            if (status == -1) {
                freeAssets = BaseDb.getInstance().getAssetDao().findAllAssets();
            } else {
                freeAssets = BaseDb.getInstance().getAssetDao().findAssetsByStatus(status);
            }
            emitter.onNext(freeAssets);
        });
    }
}
