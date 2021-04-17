package com.android.terminalbox.core;

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
import com.android.terminalbox.core.http.HttpHelper;
import com.android.terminalbox.core.http.HttpHelperImpl;
import com.android.terminalbox.core.prefs.PreferenceHelper;
import com.android.terminalbox.core.prefs.PreferenceHelperImpl;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author yhm
 * @date 2017/11/27
 */

public class DataManager implements HttpHelper, PreferenceHelper {

    private HttpHelper mHttpHelper;
    private PreferenceHelper mPreferenceHelper;
    private volatile static DataManager INSTANCE = null;

    private DataManager(HttpHelper httpHelper, PreferenceHelper preferencesHelper) {
        mHttpHelper = httpHelper;
        mPreferenceHelper = preferencesHelper;
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DataManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataManager(HttpHelperImpl.getInstance(), PreferenceHelperImpl.getInstance());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void saveHostUrl(String hostUrl) {
        mPreferenceHelper.saveHostUrl(hostUrl);
    }

    @Override
    public String getHostUrl() {
        return mPreferenceHelper.getHostUrl();
    }

    @Override
    public void saveMixTime(int mixTime) {
        mPreferenceHelper.saveMixTime(mixTime);
    }

    @Override
    public int getMixTime() {
        return mPreferenceHelper.getMixTime();
    }

    @Override
    public void saveMixTimeUnchange(int mixTimeUnchange) {
        mPreferenceHelper.saveMixTimeUnchange(mixTimeUnchange);
    }

    @Override
    public int getMixTimeUnchange() {
        return mPreferenceHelper.getMixTimeUnchange();
    }

    @Override
    public void saveIpOne(String ipOne) {
        mPreferenceHelper.saveIpOne(ipOne);
    }

    @Override
    public String getIpOne() {
        return mPreferenceHelper.getIpOne();
    }

    @Override
    public void saveIpTwo(String ipTwo) {
        mPreferenceHelper.saveIpTwo(ipTwo);
    }

    @Override
    public String getIpTwo() {
        return mPreferenceHelper.getIpTwo();
    }

    @Override
    public void setToken(String token) {
        mPreferenceHelper.setToken(token);
    }

    @Override
    public String getToken() {
        return mPreferenceHelper.getToken();
    }

    @Override
    public Observable<BaseResponse<UserLoginResponse>> login(UserInfo userInfo) {
        return mHttpHelper.login(userInfo);
    }

    @Override
    public Observable<BaseResponse<List<UserInfo>>> getAllUserInfo() {
        return mHttpHelper.getAllUserInfo();
    }

    @Override
    public Observable<BaseResponse<UserInfo>> updateFeature(FaceFeatureBody faceFeatureBody) {
        return mHttpHelper.updateFeature(faceFeatureBody);
    }

    @Override
    public Observable<BaseResponse<List<UserInfo>>> updateFeatures(List<FaceFeatureBody> faceFeatures) {
        return mHttpHelper.updateFeatures(faceFeatures);
    }

    @Override
    public Observable<BaseResponse<OrderResponse>> newOrder(String devId, NewOrderBody newOrderBody, int userId) {
        return mHttpHelper.newOrder(devId, newOrderBody, userId);
    }

    @Override
    public Observable<BaseResponse<TerminalInfo>> terminalLogin(TerminalLoginPara terminalLoginPara) {
        return mHttpHelper.terminalLogin(terminalLoginPara);
    }

    @Override
    public Observable<BaseResponse<AssetsListPage>> fetchPageAssetsList(Integer size, Integer page, String patternName, String userRealName, String conditions) {
        return mHttpHelper.fetchPageAssetsList(size, page, patternName, userRealName, conditions);
    }

    @Override
    public Observable<BaseResponse> borrowTools(NewBorrowBackPara borrowPara) {
        return mHttpHelper.borrowTools(borrowPara);
    }

    @Override
    public Observable<BaseResponse> backTools(NewBorrowBackPara backPara) {
        return mHttpHelper.backTools(backPara);
    }


}
