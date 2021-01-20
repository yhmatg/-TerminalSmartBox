package com.android.terminalbox.core.prefs;

/**
 * @author yhm
 * @date 2017/11/27
 */

public interface PreferenceHelper {
    void setToken(String token);

    String getToken();

    void saveHostUrl(String hostUrl);

    String getHostUrl();

    void saveMixTime(int mixTime);

    int getMixTime();

    void saveMixTimeUnchange(int mixTimeUnchange);

    int getMixTimeUnchange();

    void saveIpOne(String ipOne);

    String getIpOne();

    void saveIpTwo(String ipTwo);

    String getIpTwo();

}

