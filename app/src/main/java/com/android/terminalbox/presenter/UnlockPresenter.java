package com.android.terminalbox.presenter;


import com.android.terminalbox.base.presenter.BasePresenter;
import com.android.terminalbox.contract.UnlockContract;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;
import com.android.terminalbox.core.http.widget.BaseObserver;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.utils.RxUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


/**
 * @author yhm
 * @date 2018/2/26
 */
public class UnlockPresenter extends BasePresenter<UnlockContract.View> implements UnlockContract.Presenter {

    @Override
    public void fetchLocalAssets(int status) {
        addSubscribe(getLocalAssets(status)
        .compose(RxUtils.rxSchedulerHelper())
        .subscribeWith(new BaseObserver<List<AssetsListItemInfo>>(mView, false) {
            @Override
            public void onNext(List<AssetsListItemInfo> assetsListItemInfos) {
                mView.handleFetchLocalAssets(assetsListItemInfos);
            }
        }));
    }

    Observable<List<AssetsListItemInfo>> getLocalAssets(int status) {
        return Observable.create(new ObservableOnSubscribe<List<AssetsListItemInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<AssetsListItemInfo>> emitter) throws Exception {
                List<AssetsListItemInfo> localAssets = new ArrayList<>();
                if (status == -1) {
                    localAssets = BaseDb.getInstance().getAssetDao().findAllAssets();
                } else {
                    localAssets = BaseDb.getInstance().getAssetDao().findAssetsByStatus(status);
                }
                emitter.onNext(localAssets);
            }
        });
    }

    @Override
    public void updateAssetsStatus(List<AssetsListItemInfo> assets) {
        addSubscribe(updateLocalAssetStatus(assets)
        .compose(RxUtils.rxSchedulerHelper())
        .subscribeWith(new BaseObserver<Integer>(mView, false) {
            @Override
            public void onNext(Integer size) {
                mView.handleUpdateAssetsStatus(size);
            }
        }));
    }

    Observable<Integer> updateLocalAssetStatus(List<AssetsListItemInfo> freeAssets){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                BaseDb.getInstance().getAssetDao().updateItems(freeAssets);
                emitter.onNext(freeAssets.size());
            }
        });
    }


}
