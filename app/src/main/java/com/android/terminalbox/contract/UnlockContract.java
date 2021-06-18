
package com.android.terminalbox.contract;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;

import java.util.List;

/**
 * @author yhm
 * @date 2018/2/26
 */

public interface UnlockContract {
    interface View extends AbstractView {

        void handleFetchLocalAssets(List<AssetsListItemInfo> freeAssets);

        void handleUpdateAssetsStatus(int size);
    }

    interface Presenter extends AbstractPresenter<View> {

        void fetchLocalAssets(int status);

        void updateAssetsStatus(List<AssetsListItemInfo> freeAssets);

    }
}
