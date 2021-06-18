package com.android.terminalbox.contract;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;

/**
 * @author yhm
 * @date 2018/2/26
 */

public interface SettingsContract {

    interface View extends AbstractView {
        void handleFetchPageAssetsList(int resultSize);
    }

    interface Presenter extends AbstractPresenter<View> {

        void fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions);
    }
}
