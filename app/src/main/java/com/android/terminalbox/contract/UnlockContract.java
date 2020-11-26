
package com.android.terminalbox.contract;

import com.android.terminalbox.base.presenter.AbstractPresenter;
import com.android.terminalbox.base.view.AbstractView;
import com.android.terminalbox.core.bean.user.NewOrderBody;
import com.android.terminalbox.core.bean.user.OrderResponse;
import com.android.terminalbox.core.bean.BaseResponse;

/**
 * @author yhm
 * @date 2018/2/26
 */

public interface UnlockContract {
    interface View extends AbstractView {

        void handleNewOrder(BaseResponse<OrderResponse> NewOrderResponse);

    }

    interface Presenter extends AbstractPresenter<View> {

        void newOrder(String devId, NewOrderBody newOrderBody,int userId);

    }
}
