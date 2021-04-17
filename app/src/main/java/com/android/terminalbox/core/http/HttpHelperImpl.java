package com.android.terminalbox.core.http;

import com.android.terminalbox.core.bean.cmb.AssetsListPage;
import com.android.terminalbox.core.bean.cmb.NewBorrowBackPara;
import com.android.terminalbox.core.bean.cmb.TerminalInfo;
import com.android.terminalbox.core.bean.cmb.TerminalLoginPara;
import com.android.terminalbox.core.bean.user.NewOrderBody;
import com.android.terminalbox.core.bean.user.OrderResponse;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;
import com.android.terminalbox.core.http.api.GeeksApis;
import com.android.terminalbox.core.http.client.RetrofitClient;

import java.util.List;

import io.reactivex.Observable;

/**
 * 对外隐藏进行网络请求的实现细节
 *
 * @author yhm
 * @date 2017/11/27
 */

public class HttpHelperImpl implements HttpHelper {

    private GeeksApis mGeeksApis;

    private volatile static HttpHelperImpl INSTANCE = null;


    private HttpHelperImpl(GeeksApis geeksApis) {
        mGeeksApis = geeksApis;
    }

    public static HttpHelperImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpHelperImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpHelperImpl(RetrofitClient.getInstance().create(GeeksApis.class));
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<BaseResponse<UserLoginResponse>> login(UserInfo userInfo) {
        return mGeeksApis.login(userInfo);
    }

    @Override
    public Observable<BaseResponse<List<UserInfo>>> getAllUserInfo() {
        return mGeeksApis.getAllUserInfo();
    }

    @Override
    public Observable<BaseResponse<UserInfo>> updateFeature(FaceFeatureBody faceFeatureBody) {
        return mGeeksApis.updateFeature(faceFeatureBody);
    }

    @Override
    public Observable<BaseResponse<List<UserInfo>>> updateFeatures(List<FaceFeatureBody> faceFeatures) {
        return mGeeksApis.updateFeatures(faceFeatures);
    }

    @Override
    public Observable<BaseResponse<OrderResponse>> newOrder(String devId, NewOrderBody newOrderBody,int userId) {
        return mGeeksApis.newOrder(devId, newOrderBody,userId);
    }

    @Override
    public Observable<BaseResponse<TerminalInfo>> terminalLogin(TerminalLoginPara terminalLoginPara) {
        return mGeeksApis.terminalLogin(terminalLoginPara);
    }

    @Override
    public Observable<BaseResponse<AssetsListPage>> fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions) {
        return mGeeksApis.fetchPageAssetsList(size, page, patternName, userRealName, conditions);
    }

    @Override
    public Observable<BaseResponse> borrowTools(NewBorrowBackPara borrowPara) {
        return mGeeksApis.borrowTools(borrowPara);
    }

    @Override
    public Observable<BaseResponse> backTools(NewBorrowBackPara backPara) {
        return mGeeksApis.backTools(backPara);
    }

}
