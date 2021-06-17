package com.android.terminalbox.app;


import android.graphics.Color;

import java.io.File;


/**
 * @author yhm
 * @date 2017/11/27
 */

public class Constants {
    public static final String MY_SHARED_PREFERENCE = "my_shared_preference";

    /**
     * url
     */
    public static final String COOKIE = "Cookie";

    /**
     * Path
     */
    public static final String PATH_DATA = BaseApplication.getInstance().getCacheDir().getAbsolutePath() + File.separator + "data";

    public static final String PATH_CACHE = PATH_DATA + "/NetCache";

    public static final int[] TAB_COLORS = new int[]{
            Color.parseColor("#90C5F0"),
            Color.parseColor("#91CED5"),
            Color.parseColor("#F88F55"),
            Color.parseColor("#C0AFD0"),
            Color.parseColor("#E78F8F"),
            Color.parseColor("#67CCB7"),
            Color.parseColor("#F6BC7E")
    };

    public static final String HOSTURL = "hostUrl";

    public static final String TOKEN = "token";
    public static final String MIX_TIME = "mix_time";
    public static final String MIX_TIME_UNCHANGE = "mix_time_unchange";
    public static final String IP_ONE = "ip_one";
    public static final String IP_TWO = "ip_two";
    public static final String IP_THREE = "ip_three";


}
