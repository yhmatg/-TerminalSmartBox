package com.android.terminalbox.core.http.api;

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

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author yhm
 * @date 2018/2/12
 */

public interface GeeksApis {

    //登录
  /*  @POST("user-server/userauth/loginwithinfo")
    Observable<BaseResponse<UserLoginResponse>> login(@Body UserInfo userInfo);*/

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
    Observable<BaseResponse<OrderResponse>> newOrder(@Path("dev_id") String devId, @Body NewOrderBody newOrderBody, @Query("userId") int userId);

    //招商演示
    //用户登录
    @POST("user-server/userauth/loginwithinfo")
    Observable<BaseResponse<UserLoginResponse>> login(@Body UserInfo userInfo);

    //设备登录
    @POST("/inventory-server/terminal/login")
    Observable<BaseResponse<TerminalInfo>> terminalLogin(@Body TerminalLoginPara terminalLoginPara);

    //模糊查询资产详情（写入标签）分页
    @GET("assets-server/assets/multiconditions")
    Observable<BaseResponse<AssetsListPage>> fetchPageAssetsList(@Query("size") Integer size, @Query("page") Integer page, @Query("pattern_name") String patternName, @Query("user_real_name") String userRealName, @Query("conditions") String conditions);

    //借用工具
    @POST("assets-server/general/bussiness/apply/BORROW")
    Observable<BaseResponse> borrowTools(@Body NewBorrowBackPara borrowPara);

    //归还工具
    @POST("assets-server/general/bussiness/apply/BACK")
    Observable<BaseResponse> backTools(@Body NewBorrowBackPara backPara);
}
