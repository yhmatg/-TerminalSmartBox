package com.android.terminalbox.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.android.terminalbox.BuildConfig;
import com.android.terminalbox.R;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.utils.Utils;
import com.android.terminalbox.utils.logger.MyCrashListener;
import com.android.terminalbox.utils.logger.TxtFormatStrategy;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.xuexiang.xlog.XLog;
import com.xuexiang.xlog.crash.CrashHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yhm
 * @date 2017/11/27
 */
//public class BaseApplication extends Application implements HasActivityInjector {
public class BaseApplication extends Application {


    private static BaseApplication instance;

    public static synchronized BaseApplication getInstance() {
        return instance;
    }

    public static String relevanceId;
    private List<UserInfo> users = new ArrayList<>();
    private UserInfo currentUer;
    private int mixTime = 4;
    private int mixTimeUnchange = 6;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initLogger();
        //崩溃日志保存到本地
        ///storage/emulated/0/Android/data/com.common.esimrfid/cache/crash_log
        XLog.init(this);
        Utils.init(this);
        CrashHandler.getInstance().setOnCrashListener(new MyCrashListener());
        //殷浩淼
        UserInfo sh0001 = new UserInfo();
        sh0001.setUser_name("sh0001");
        sh0001.setUser_password("123456");
        sh0001.setFaceFeature("AID6RAAAoEGIa928fRqAPaZXtbxOjO29x7yiPUVP9rxmwqO8UZGCPIRtuj0tTra9btZhuz6S4T0Q" +
                "VHy98SW7vLkuqj2NCIS99lIWvpsbbrxW9n69Niv2PRxkwj0PtDE9OKwGPPdRRb1KXNQ8J/aWvFql" +
                "0LyJpL29j/pIPTO7m73tcTE9vyo4vRHnF71Yq4A8RKgWvLLYZT3C+UI9jeeGvXu8Ob3raM69l9OR" +
                "vbGBpj1Ouk69gdzVvcjBAz1u8FO980WcPTvR6LwkUGs8HS9fvdPhAT7WOti8jh+DPdCd+L3VEdm9" +
                "0xCMvY/f4z06UOE8bKgDvS/Flb3mzOK8YJh0PeTRiTkK8pW9Dg9yPTQsmTyRqse9XkojvRKpAD2N" +
                "MYK90kTxPQenDbsR6VM7BizHPVaHWb2qdZk7Pa21vdptWr1p1a29DHnIvWFCrLwaf7W8xNGovaMz" +
                "aj1o7a+9wCcuPr6UVT3hrus8HXZ0vSwxAL1Gr0+9iNENPd4vMD6evcI92/wLPZBRGj4K4RW9Yw5p" +
                "u90vdr3R74g9u/xoOItXf719SKW83nCdPNrv6TxgFnm98+rKvOtMpT1kYy297XihO1ie3j2O/q09" +
                "oGwjPfi8Z72QkQc+qOy1vJvQLL1GP7k9vGeEvWOR+DpdOUM824CXvVqlTL3Fgd2900VIvV322L2E" +
                "W989RV8YPB9h8L3+YD688PDBPIKmdjz3XZM95mLxPGOworxOT6o9Jh1nvQB3Gj2Lugy8VFirvcsa" +
                "i7znKMo9c9WwvUaXxT1Y/hI8KNalvQeeVLxCQYs8xbJvPJRj3LrLYKa8hA5VvODuez24swU9O48w" +
                "PeQXEjx0LOU7+xW0vRGlV71TYks72I9qPa74lT1rPi6+MNnKuaS4ljzMQAS+j+gzvbeo5r3kgh69" +
                "FLn7Oyv5Nr0Rpro9e5aEPAZgHz6riSo8crlqPQRFGT3aKy897rdVuxOIQDwR6cQ8lUOhvMkTP731" +
                "i4K96Nt6Pfs5eL1sGak9YyEnvbKdv73Kso89RfkTPT2Ta72xqZi8wLnTvf0gf721KHu8UauQPDJY" +
                "L70Gs9O8HAUbvdTMhD2iZtq8t18ZPk219TyL8Y+9bRqivaeYCT2cmbK7L5NjPSvajr3f39k8conP" +
                "vDnfzT10SBy9uRFBvdlCWLwKqMK9bvcUPT6pNDzFMSM9/my0va909jy8CW89V7zYvYzDFT0JXQq9" +
                "MM+UPM8/H7wybS66cgE6vf3VsD0JixO8029DvWE8JzzawIs9SzrEO1MO0T3jXYW75TgBPaHDQj1X" +
                "SBA+zLwBPREDQb3XuWq9bOmhvNE5Dz0IhCA96z6rutkFVT0Ew808Z+IUPSKwxLyB1yg+cHGyPGvA" +
                "oDutD4m8");
        //张平
        UserInfo sh0002 = new UserInfo();
        sh0002.setUser_name("sh0002");
        sh0002.setUser_password("123456");
        sh0002.setFaceFeature("AID6RAAAoEFE7wG9f6KOPehxNb1uZyy9zb2SPXyZDr31Fgu+pGYYPFZH2Ly8jLS7ZwhsPVY3VrxZ" +
                "2JW8HDHMPNXa7LyjMbS9fCaWvfWbbLyRvM88d7GsPfDB1r2JtOK94WWqvX/RCz5UHxK7AlVvvJwV" +
                "ij2dZLi8/X0zPauB8r2im309KLOHvKWdoT1WrSS9Zb/RvfFkPDs3onA7IFQaPdlPBr2NGVe9HH4N" +
                "PEWVKb33mAi9FV4lPXxa9b3qXIA9IAvzvD3PBb2ySpk9GdoWO34anL3Y4li5swMCPfoHujwYzyA9" +
                "GvuYva092zzO6Sk9kNU6PaO4wjydjgm9xJj1PfnwEz0ul7a7U+U8vDMhGb1LIXu7su+oPaYtvD0/" +
                "eTm9n0kHvQAZGD0OdMk7nO2SvXzT/D3RlWo8kC1BPf86azwRbDg9s1XIPeEhzjyX+xI8WY1EvPsd" +
                "ZL1K2xA8k/7bPfvliLt5ye48IXG5PVyTjDtCeoU7euCnPCPBUj37WI28i5PPPFRpCz3EC9G8lcEX" +
                "u1O+BDyYcTi8eM/TPaMBkr3hYya9Cd5svaxIHrwUpvy8SNsTPdFibL0v4Ao9ioyMvaZl4b16qwa+" +
                "csnavB1dnT2Z7pE98s8JO7SiLz51hkW9QlxgvemRw7zskfi9E820PIaKozwINSW+FtSIvdSNJD3J" +
                "hWw939+YvBPUCz4wjV09pw8wPdB3Ir0IYL89XmVYva2uTbvviIW8vfqFPIO67z3rEco82UtbPTIm" +
                "1L3lVbU9elYvvGuvob33L4Q7n1QPPWKrmD2BqF08WUCBOwjjp7zinYs9vrIZPR8DDTzfpA89vcxA" +
                "POiftr1+Jpm8S484PUhv1DcrnSs95jhOPI6gcb3HetG9QB1QPQrOFb6lzI+9pQZePXdEBL2aEpW9" +
                "PzGXvXT3hr3OY6U8nqemPZlw572Zit88mJfdvYKZKD1YV7s93CkCvl9Epr0lCiU9F/BcvHa1xr27" +
                "b5O9sprdvETRprw2ObG80P2dvZxF1rxk3zS+tXaMvQfY9TvJyhK95/ZvvH6FmbxXgJy9jw60O+j7" +
                "pz06bx69zfWePL+IFT1yQh29uY3jvTUstr2GmkA8qUWsPR7oo73BQaw8RHvVvQw6OjzgkBu9O/2X" +
                "vKFWTbxlkxK99R7BPPR9SD367Te+oCxgPG49Uz0zjWY9XV8qvY5qXD0egWm9DdtMvDdFQT2shUK9" +
                "JfG1vTg3/byiRvu7YnFuPWTaUj0eQ+O98AxqPDz5RT205oO7n1oQvn7LBryQXu2835JLvUAkrb1U" +
                "w809esfuvSBVXT1Fi8c9jEEOvlA8pbsYwE29vLUvPQsyQL3Fm8K9rQbFvOMIkL1KMiA9nbdvPUVy" +
                "EjzIgIG9");
        UserInfo sh0003 = new UserInfo();
        sh0003.setUser_name("sh0003");
        sh0003.setUser_password("123456");
        sh0003.setFaceFeature("AID6RAAAoEFWdQu9aqLhPGvtLL11r029b4kzvU1svD11gRW9e/CQvVhWXzxKA7Q90UwPvNViHzyH" +
                "rgm8ugiSvUrSJj5ifK06UVcivMVYoDz1a1a9LxJrvaxgBr785XW90/uJvZJ75TzXL5s9hklcvXYa" +
                "aryv5qA8lMqXvdcI3r3q7AM839NLPfLN8TypCr88Ws47vfK+3bwA9ie9GHNDPVfUSz2tq806a0DR" +
                "vZkQlb2PA0G8kOIQvLARNDyYz3w9RQraPdCF77zmagM+w+zoPSzFqb2uQbg8tk+PPZDY67shHFe9" +
                "+h9fvTmqnbw4SL68pvz3O7NHaztckZC9U4aLPQr/2r19unw9gcg9PUpqHL0a1Xk95ndSvY/zzrsb" +
                "ZMk8JrcXvPNGlDtMeRy+a+GLPZdrFz0v33m9pDfBPBSvbT3otFs7OkbsPIyhBr16paS8IR7tvMBw" +
                "Hz1aVGQ9Daa8PUHqDT540JA7C3+9vJMsOL0S2gU+OWqOvedhAz32zQy8YpcoPfvUrDxeNHu8zqzI" +
                "vPpRNzztW8Q84dKbvbvErjvz7ew9eVg8vegx3r1aO4C8LMAevi2+hT01ekq8G3QKvQaskT2LY/K9" +
                "bLS/uwNMQL3bsNu9D6qLvAyoKL0gifS7OBSYPbmNFT3hIHa9EYLDvXFSpTwy24u9bf5uvRgtC76A" +
                "OJ08J6CcPFZY4Tsz4Ne9gDfMvdBbkT1nERq9VQdcvStMAL3MnMM9RT/MO58hAj12qQm8msagPfc6" +
                "FTyUiGS9qSIFPn0/KjztN0A9mw5RPOwtezzoZSw9d10YPaOhXb4tPIy93wtaPfhXLLyu7EG81EkP" +
                "PbjALDyQAs09X/OgvT6NQzwGP1a8SeYEPh/Iz711Sxe+pkXHPMtN1D36gqO8RzTbvONUgj3fJvC8" +
                "DFiBPfci9j1AbQw9f4scPpVwlDzRP+67kqbVPDAJKr1sYwU8X33qvUhS4bwiawI9p+cdvah+Lb0L" +
                "cVC9BixuvaJinryXSyI9ObbtvN8RnL1SQIS8zh+0vYPggjyRn0Q77lusvYCXYr06wPK7G7NBPRL6" +
                "iD2Jdoa9kJe+PRNQczysltK9x45nO2Czsj3dshy9J5HZvFJLjT2Mtt68/0N4vWMoO72FjTq9USrg" +
                "OsLozT0AxIu9y+IsPSm8tz2CIP+9H85DvG2yKb1f5pu8JAFXvTTe0z373XC9Mt2mO0DJFj62DQS+" +
                "7RSUPQWxpz0Q8NS97XYLvjt+ib2JGWm87KkVvPbo0LziUI+8vWxUPRV4bD3MU2Y9YV8tPZvxnbzX" +
                "lxk9865hvYGqbj0faoE6txiquxDOh73dRTg810XyvG+bar2Q0Ow8OSugvHrYML3sTQQ7l08wvdy+" +
                "7rxo5AK9");
        users.add(sh0001);
        users.add(sh0002);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }


    private void initLogger() {
        //DEBUG版本才打控制台log
        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(new AndroidLogAdapter(PrettyFormatStrategy.newBuilder().
                    tag(getString(R.string.app_name)).build()));
        }
        //把log存到本地
        Logger.addLogAdapter(new DiskLogAdapter(TxtFormatStrategy.newBuilder().
                tag(getString(R.string.app_name)).build(getPackageName(), getString(R.string.app_name))));
    }

    public static String getRelevanceId() {
        return relevanceId;
    }

    public static void setRelevanceId(String relevanceId) {
        BaseApplication.relevanceId = relevanceId;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public UserInfo getCurrentUer() {
        return currentUer;
    }

    public void setCurrentUer(UserInfo currentUer) {
        this.currentUer = currentUer;
    }

    public int getMixTime() {
        return mixTime;
    }

    public void setMixTime(int mixTime) {
        this.mixTime = mixTime;
    }

    public int getMixTimeUnchange() {
        return mixTimeUnchange;
    }

    public void setMixTimeUnchange(int mixTimeUnchange) {
        this.mixTimeUnchange = mixTimeUnchange;
    }
}
