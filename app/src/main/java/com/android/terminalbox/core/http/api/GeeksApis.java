package com.android.terminalbox.core.http.api;

import com.android.terminalbox.core.bean.user.NewOrderBody;
import com.android.terminalbox.core.bean.user.OrderResponse;
import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.FaceFeatureBody;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author yhm
 * @date 2018/2/12
 */

public interface GeeksApis {

    //登录
    @POST("user-server/userauth/loginwithinfo")
    Observable<BaseResponse<UserLoginResponse>> login(@Body UserInfo userInfo);

    //获取所有用户信息
    @GET("/api/v1/unauth/users")
    Observable<BaseResponse<List<UserInfo>>> getAllUserInfo();

    //人脸头像特征值更新
    @POST("/api/v1/unauth/updateFeature")
    Observable<BaseResponse<UserInfo>> updateFeature(@Body FaceFeatureBody faceFeatureBody);

    //批量更新人脸特征值
    @POST("/api/v1/unauth/updateFeatures")
    Observable<BaseResponse<List<UserInfo>>> updateFeatures(@Body List<FaceFeatureBody> faceFeatures);

    //创建操作单
    @POST("/api/v1/actrecords/devices/{dev_id}/")
    Observable<BaseResponse<OrderResponse>> newOrder(@Path("dev_id") String devId, @Body NewOrderBody newOrderBody);
}
