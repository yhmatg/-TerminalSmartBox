package com.android.terminalbox.contract;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;
import com.android.terminalbox.core.bean.cmb.TerminalInfo;
import com.android.terminalbox.core.bean.cmb.TerminalLoginPara;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;

import java.util.List;

/**
 * @author yhm
 * @date 2018/2/26
 */

public interface MainContract {

    interface View extends AbstractView {
        void handleFetchLocalFreeAssets(List<AssetsListItemInfo> freeAssets);
    }

    interface Presenter extends AbstractPresenter<View> {
        void fetchLocalFreeAssets(int status);
    }
}
