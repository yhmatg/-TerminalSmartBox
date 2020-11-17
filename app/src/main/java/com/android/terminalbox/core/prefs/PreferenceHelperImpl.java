package com.android.terminalbox.core.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.terminalbox.app.BaseApplication;
import com.android.terminalbox.app.Constants;
/**
 * @author yhm
 * @date 2017/11/27
 */

public class PreferenceHelperImpl implements PreferenceHelper {

    private final SharedPreferences mPreferences;
    private volatile static PreferenceHelperImpl INSTANCE = null;

    private PreferenceHelperImpl() {
        mPreferences = BaseApplication.getInstance().getSharedPreferences(Constants.MY_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static PreferenceHelperImpl getInstance(){
        if (INSTANCE == null) {
            synchronized (PreferenceHelperImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PreferenceHelperImpl();
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
        mPreferences.edit().putString(Constants.HOSTURL, hostUrl).apply();
    }

    @Override
    public String getHostUrl() {
        return mPreferences.getString(Constants.HOSTURL, "http://117.34.118.157:20000/");
    }

    @Override
    public void setToken(String token) {
        mPreferences.edit().putString(Constants.TOKEN, token).apply();
    }

    @Override
    public String getToken() {
        return mPreferences.getString(Constants.TOKEN, "8c72bc61-65ea-4814-aef9-d947ae3f9c5c");
    }


}
