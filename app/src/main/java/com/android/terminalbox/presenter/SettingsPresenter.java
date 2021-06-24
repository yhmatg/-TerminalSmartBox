package com.android.terminalbox.presenter;

import android.util.Log;

import com.android.terminalbox.base.presenter.BasePresenter;
import com.android.terminalbox.contract.SettingsContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;
import com.android.terminalbox.core.bean.cmb.AssetsListPage;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;
import com.android.terminalbox.core.http.widget.BaseObserver;
import com.android.terminalbox.core.room.BaseDb;
import com.android.terminalbox.utils.RxUtils;
import com.xuexiang.xlog.XLog;

import java.util.ArrayList;
import java.util.List;

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
                        List<AssetsListItemInfo> assetList = assetsListPage.getList();
                        ArrayList<String> epcs = new ArrayList<>();
                        for (AssetsListItemInfo assetsListItemInfo : assetList) {
                            epcs.add(assetsListItemInfo.getAst_epc_code());
                        }
                        XLog.get().e("EPC数据：" + epcs.toString());
                        BaseDb.getInstance().getAssetDao().deleteAllData();
                        BaseDb.getInstance().getAssetDao().insertItems(assetList);
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
