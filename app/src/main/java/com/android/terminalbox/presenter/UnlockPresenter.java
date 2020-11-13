package com.android.terminalbox.presenter;


import com.android.terminalbox.base.presenter.BasePresenter;
import com.android.terminalbox.core.bean.user.NewOrderBody;
import com.android.terminalbox.core.bean.user.OrderResponse;
import com.android.terminalbox.contract.UnlockContract;
import com.android.terminalbox.core.DataManager;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.http.widget.BaseObserver;
import com.android.terminalbox.utils.RxUtils;


/**
 * @author yhm
 * @date 2018/2/26
 */
public class UnlockPresenter extends BasePresenter<UnlockContract.View> implements UnlockContract.Presenter {

    private static String deviceId = "15aa68f3183311ebb7260242ac120004_uniqueCode002";

    @Override
    public void newOrder(String devId, NewOrderBody newOrderBody) {
        addSubscribe(DataManager.getInstance().newOrder(devId, newOrderBody)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribeWith(new BaseObserver<BaseResponse<OrderResponse>>(mView, false) {
                                   @Override
                                   public void onNext(BaseResponse<OrderResponse> orderResponse) {
                                       mView.handleNewOrder(orderResponse);
                                   }
                               }
                ));
    }

}
