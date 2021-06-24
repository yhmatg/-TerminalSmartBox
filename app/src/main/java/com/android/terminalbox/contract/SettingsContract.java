package com.android.terminalbox.contract;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;

/**
 * @author yhm
 * @date 2018/2/26
 */

public interface SettingsContract {

    interface View extends AbstractView {
        void handleFetchPageAssetsList(int resultSize);

        void handleLogin(UserLoginResponse userLoginResponse);
    }

    interface Presenter extends AbstractPresenter<View> {

        void fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions);

        void login(UserInfo userInfo);
    }
}
