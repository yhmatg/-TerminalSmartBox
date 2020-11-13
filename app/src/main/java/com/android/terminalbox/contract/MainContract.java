package com.android.terminalbox.contract;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;

import java.util.List;

/**
 * @author yhm
 * @date 2018/2/26
 */

public interface MainContract {

    interface View extends AbstractView {
        void handelAllUserInfo(BaseResponse<List<UserInfo>> userInfos);

        void handleUpdateFeature(BaseResponse<List<UserInfo>> userInfos);

    }

    interface Presenter extends AbstractPresenter<View> {
        void getAllUserInfo();

        void updateFeatures(List<FaceFeatureBody> faceFeatures);
    }
}
