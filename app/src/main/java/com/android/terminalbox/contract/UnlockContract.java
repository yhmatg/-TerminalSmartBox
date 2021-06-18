
package com.android.terminalbox.contract;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;
import com.android.terminalbox.core.bean.cmb.NewBorrowBackPara;
import com.android.terminalbox.core.bean.user.NewOrderBody;
import com.android.terminalbox.core.bean.user.OrderResponse;
import com.android.terminalbox.core.bean.BaseResponse;

import java.util.List;

/**
 * @author yhm
 * @date 2018/2/26
 */

public interface UnlockContract {
    interface View extends AbstractView {

        void handleFetchLocalAssets(List<AssetsListItemInfo> freeAssets);

        void handleUpdateAssetsStatus(boolean result);
    }

    interface Presenter extends AbstractPresenter<View> {

        void fetchLocalAssets(int status);

        void updateAssetsStatus(List<AssetsListItemInfo> freeAssets);

    }
}
