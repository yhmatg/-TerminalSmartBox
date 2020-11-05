package com.android.terminalbox.contract;

import android.view.View;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;

import java.util.List;

import retrofit2.http.Body;

/**
 * @author yhm
 * @date 2018/2/26
 */

public interface RecognizeContract {

    interface View extends AbstractView {
        void handelAllUserInfo(BaseResponse<List<UserInfo>> userInfos);

        void handleUpdateFeature(BaseResponse<UserInfo> userInfo);

    }

    interface Presenter extends AbstractPresenter<View> {
        void getAllUserInfo();

        void updateFeature( FaceFeatureBody faceFeatureBody);
    }
}
