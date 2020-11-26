package com.android.terminalbox.core.http;

import com.android.terminalbox.core.bean.user.NewOrderBody;
import com.android.terminalbox.core.bean.user.OrderResponse;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author yhm
 * @date 2017/11/27
 */

public interface HttpHelper {

    Observable<BaseResponse<UserLoginResponse>> login(UserInfo userInfo);

    Observable<BaseResponse<List<UserInfo>>> getAllUserInfo();

    Observable<BaseResponse<UserInfo>> updateFeature(FaceFeatureBody faceFeatureBody);

    Observable<BaseResponse<List<UserInfo>>> updateFeatures(List<FaceFeatureBody> faceFeatures);

    Observable<BaseResponse<OrderResponse>> newOrder(String devId, NewOrderBody newOrderBody,int userId);
}
