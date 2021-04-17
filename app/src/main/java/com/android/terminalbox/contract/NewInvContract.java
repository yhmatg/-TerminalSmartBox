package com.android.terminalbox.contract;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;

import java.util.List;

public interface NewInvContract {
    interface View extends AbstractView {

        void handleFetchPageAssetsList(List<AssetsListItemInfo> assetsInfos);
    }

    interface Presenter extends AbstractPresenter<View> {

        void fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions);

    }
}
