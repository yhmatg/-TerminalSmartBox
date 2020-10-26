package com.android.terminalbox.core.http.api;

import com.android.terminalbox.core.bean.BaseResponse;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.bean.user.UserLoginResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author yhm
 * @date 2018/2/12
 */

public interface GeeksApis {

    String HOST = "http://172.16.63.25:12000"; //佳航25，言娇35，梦伟32

    //登录
    @POST("user-server/userauth/loginwithinfo")
    Observable<BaseResponse<UserLoginResponse>> login(@Body UserInfo userInfo);
}
