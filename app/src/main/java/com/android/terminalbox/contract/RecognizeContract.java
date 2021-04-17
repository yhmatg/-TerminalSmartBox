package com.android.terminalbox.contract;

import android.view.View;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;

import java.util.List;

import retrofit2.http.Body;

/**
 * @author yhm
 * @date 2018/2/26
 */

public interface RecognizeContract {

    interface View extends AbstractView {
        void handleLogin(UserLoginResponse userLoginResponse);
    }

    interface Presenter extends AbstractPresenter<View> {

        void login(UserInfo userInfo);
    }
}
