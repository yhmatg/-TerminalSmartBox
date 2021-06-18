package com.android.terminalbox.presenter;

import com.android.terminalbox.base.presenter.BasePresenter;
import com.android.terminalbox.contract.SettingsContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.cmb.AssetsListPage;
import com.android.terminalbox.core.http.widget.BaseObserver;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.utils.RxUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SettingsPresenter extends BasePresenter<SettingsContract.View> implements SettingsContract.Presenter {

    public SettingsPresenter() {
        super();
    }


    @Override
    public void fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions) {
        mView.showDialog("数据导入中...");
        addSubscribe(DataManager.getInstance().fetchPageAssetsList(size, page, patternName, userRealName, conditions)
                .compose(RxUtils.handleResult())
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<AssetsListPage>() {
                    @Override
                    public void accept(AssetsListPage assetsListPage) throws Exception {
                        BaseDb.getInstance().getAssetDao().insertItems(assetsListPage.getList());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new BaseObserver<AssetsListPage>(mView, false) {
                    @Override
                    public void onNext(AssetsListPage assetsListPage) {
                        mView.dismissDialog();
                        mView.handleFetchPageAssetsList(assetsListPage.getList().size());
                    }
                }));
    }
}
