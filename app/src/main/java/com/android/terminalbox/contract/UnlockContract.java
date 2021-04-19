
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

        void handleFetchPageAssetsList(List<AssetsListItemInfo> assetsInfos);

        void handleBorrowTools(BaseResponse borrowToolsResponse);

        void handleBackTools(BaseResponse backToolsResponse);


    }

    interface Presenter extends AbstractPresenter<View> {

        void fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions);

        void borrowTools(NewBorrowBackPara borrowPara);

        void backTools(NewBorrowBackPara backPara);



    }
}
