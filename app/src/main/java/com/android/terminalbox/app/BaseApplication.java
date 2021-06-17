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
import com.xuexiang.xlog.annotation.LogLevel;
import com.xuexiang.xlog.crash.CrashHandler;
import com.xuexiang.xlog.logger.LoggerFactory;
import com.xuexiang.xlog.strategy.log.DiskLogStrategy;

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
        DiskLogStrategy diskLogStrategy = LoggerFactory.getDiskLogStrategy(
                "xlogTool", "xlog", LogLevel.ERROR, LogLevel.DEBUG
        );
        LoggerFactory.getSimpleDiskLogger("DiskLogger", diskLogStrategy, 0);
        //殷浩淼
        UserInfo sh0001 = new UserInfo();
        sh0001.setUser_name("yhm0001");
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
        sh0002.setUser_name("zp0001");
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
        //周克
        UserInfo sh0003 = new UserInfo();
        sh0003.setUser_name("zk0001");
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
        //曹水波
        UserInfo sh0004 = new UserInfo();
        sh0004.setUser_name("csb0001");
        sh0004.setUser_password("123456");
        sh0004.setFaceFeature("AID6RAAAoEFyERo9lPrdvUUmpjxWfOO72O/LvIwDXz2swAA+7I2IvcJxFj2/NuA9zejcvdcdprxL" +
                "Jyy9J2vmva4kd73WS2G9tp5YPfTLfb0oPb08/CxHPc/5AbxqUIa9k3zKvFTkyb1pUF29lWV1PUEX" +
                "zLyEuzE9shqWPaxTcTwxlIC9uKrGPC4Cw72TA0i+bDA3vY2Ar72c/Qe90MuRvEE3lL3I95a9dZqv" +
                "PWy71r2WbTi6mY/2PLn+zbnqVxy9i0+pvYB7+jt8WFu9ETfKPWb7RD2MN/G8x9NqPKLph72wpTo9" +
                "lKiOPTutY73SbHa7jJERPYX07DtA4OM8J9wBPmY8gD20ORa9cjOovSCt1z3rt/o7KIeFvERTx70L" +
                "+5E98GttvO3kjjyEPUW8iVfpvJCxub0cOM49udQGPNXG5TyeR709vIxVPHLNtLyjRte76DuhvfKg" +
                "hr3lpHu9QUfPPJbCnb0VKMg9FEcavbrCzjwZDqi8a9LjvRm6bb38HVc7duHFPf541jyFsQI8vMAm" +
                "PLm58b2+NmG9unCevXseIDz8tjy9t6tyPdUwZ73SCwg9cq9hPMyHzTxjhmM9L8uJPR8fsjwwbOw9" +
                "BnYjvaf+3bwUvEW7BuVgPfeSkb0pGNO8CD4RvDTgYjyU/hK8g2y3PXKjUDv/ly485rnZPQKiFjs0" +
                "OaU8LHlYPGxL1Tu/Pro9NlgZPfUTqz2gcL29IHTgPST34L3IYFe95ATOPPuYcz089CC9kROePbwF" +
                "lLxuIvW80/03vFy9xLyIA4696b/8vSTUsrz6fsm8EgTqPFxKRr0WHQA9dzKkPEVLqb2m1zM80bAQ" +
                "vCny9zyg7Jg8Y1UgPrZnE717e3888jWru6uKKb6Cs5a9fbtdPTwVKT01mQ09peicPf8JHj3cEV08" +
                "OW3lPFTNmr3HwH88mgDdPVyj/rtSV029j60dPVCiO7wmrMa9vd2rvRz+AjxCzLg9t+d5uwBQu71H" +
                "ds69DpubPKwwgD080YQ9KsFIPcCqZL0HdWy9fTnDPeOxYL1emqc9+xUrvcMMiz1BdSE+1kSkvcYO" +
                "mT2wPY+8LUuLvENjKr2LQca9OlBdvUUd9r3IJsc8zNjmPZLU7zz4xj88y9y8vE+SUj1Yx5s8xV7P" +
                "PUA04zmlQ8I9IbubO+J58DuEvZK98fvPPKMWzbv5hpM5fJ90PRdT/btXe1w8a02KPR2XI72/iLs8" +
                "W5PkvLezvb3Ee/W9qr0MvZHjgr0bPnA9d9RkPQK4eL0U7WQ9rY2CPIcMCT4jzDk96OcnPXcy9Lvj" +
                "iA0+2w7vPfsHDz01LyQ8xY4QPnTAZLuLf3g9xGmXvJ5pvTwVqPy8dVmHvaR/DD0waog9xGSFPFFc" +
                "Fb0ELJG8");
        //丁捷安
        UserInfo sh0005 = new UserInfo();
        sh0005.setUser_name("dja0001");
        sh0005.setUser_password("123456");
        sh0005.setFaceFeature("AID6RAAAoEG+QBM+yMEBvgU3pD12cZw8ZeCqvcY6Kz3XreQ8gxQzPeWL4T0nLAg+h2ImPSfkgDwx" +
                "Ho890Hq/vbT+Qb2u0DO9XQqrPeyznLz7byc9HjUkPE5AQD241GG90FfDvaSbAbyu+TS9Y+q3vA+l" +
                "Lr5RIZo8UyvNPYJGOD2F3M69byzDPSqID71yNCu8N2MMPPph4DycQ6A9oSTSPNFjLz3aP+g8l+m+" +
                "vOlSe70BQhm9vEQePcZfADyIy4o8gOA1vQXIZj2eaa88XcSTvLuqSbwK4sO8v6GaPfI31Dy/4IA9" +
                "L1cHvuxOhbw2eu68W/aEvMoUgLxV6BK+cNweutQgaT3/KIY9ngyDPCH/Cr3H2Ay92XfEPUdKvz1l" +
                "88Y9b73rPQWFKz166qo9Y87GPPUK7b3wuTQ9rNYevfKiYb0QNPc8vridPHP7mbxrJTe8OsI9veEK" +
                "Hz0+fyK9xViLvUfyor28lmq8K3j9PJ+ooz0sgfq85tClvagUHz3WXjy9SdPjvXJC/L0iJxG++6uE" +
                "vZSNgbxZQZE9Oyebu7Y8zDrzbHK9I+xOPL1n3L1g1Ck9nvf4vSUcDTvIsBo9Ykh2vfy/dLxmnEQ8" +
                "E85DPQo/rzyS3Yc9Su7FvPrbOTsC/sm9m8YNPmqODb3+cQm6VIZMPeBosbrtupa9Ze4FPud1lT0A" +
                "7ZQ7w7KlvQ60nz3RTys8HkAPvtIsqb1SpAQ95XZWPfNOVD0s5iW8+2FHvSLRVr1I8X69fvQRO7Av" +
                "1L2O+vs8/6ZSvRInqrzxf7C9ziUKPeyWKb0JdvG88YkIvWPCbb1DKSo7qKYwvHKMBb2lEZq8Ks9M" +
                "veUDRz2m3YE9+FupPclpCD0us4i9FdCWvU0b1L1A0ag94qirPRhYQzxTWVo9A1+ivXDPwDvFYqO9" +
                "X9YIPZsv+Lzrhus8VAiJvTSYgL1J11I9afEpvoBiET69HhA9xTCCvcbAHT19DSk9TQMoPT40jroX" +
                "toA931qjvOsq9D1EJZM80U8APuIo9LsT6xu9GTUFvs0JvTxf2AI8FJahPEp+Gr1foDi8OUAJvdqr" +
                "Uj0GpwY+8scbPlHTeb0eYF87fMYtOn7HIzxmMly9DRpKO4i+Wr0zSVE9QMLxvQDAMj0Gjoo8sumJ" +
                "vT0VFjt8Gz693ktOvEYJAT6bL3s9Qk4dPXsHnL2jk7m9SgR4u/G2Oj1zYNm9qvLYvWwgN7xF5sY9" +
                "qwCnPdNnVb1Dj8y8QM8IuxTskTwBukQ9Cfl9PSBsUr3kcjM92CHRPVERVDzfeya97WkuvWREfTxG" +
                "S2K9lsLUPCR3tL09R2w9pdxGvhdmfz0rHKK8XdAIPfGOGL1beKs9zqUlvIlyt71wLQg7ShkNvmZT" +
                "sL07WK29");
        //卢海朋
        UserInfo sh0006 = new UserInfo();
        sh0006.setUser_name("lhp0001");
        sh0006.setUser_password("123456");
        sh0006.setFaceFeature("AID6RAAAoEE1rxY81MTHPFQrwb3y5KO9Y+acPXl5Xr1MdRG+qYEtvB6l+LyIvRg96TKnvedtXz3P" +
                "UuS8/nWRu70injzbfTO8S1zyPT4Df73A8i2+TprVvYMirb24yx+8vIsxPhIfwL1mWgy9i+xHPPMO" +
                "9zzMy1S87O2xPK6z0rtdi1u9JGVNPUDc9byWcAW9JgqfvDx29zz4BTc9RfgyvU9csDyhvpE9FAWG" +
                "PY922DoGFxw9UUO9vDganDyhcaq8KPbiPbv4qz1e8yI9MCKGPcaQ8rzIDPS8WE/HvBT0P72gHHg6" +
                "adYIOe/4VLzABZc9hqMKvTrdLD1k/PC7WSg4vdIKLr2Yc1q9IW7UPMNIPb3mMfK8iClhPTeigz0Z" +
                "Zwi+KmUKPY92v71nliK+6ne9PVirsLx+n3o8mEUmvHn5Fjy3lI88NqG/vRMW/rxqn6A8p1VjvGP8" +
                "k7tRY/89tyjyPOSJwj3GnzE9R9wgvaoB4z2tbpq9t+xgvAO5Sj14wJq9FI1CPcrBa72e9i69T7lj" +
                "vdcj+7zhWga+hdAAPU6ZcL1y2SM96tCuPYiCoD0+TBg9Ci0NvdW/Br065be9EhO8u/w+2L3VLva9" +
                "HkWyvLbbCzyHkcI9eUVOvUuaRLzdYRQ9fHPGvBUWorwFX6m757GCvReNTTxbST+9shllPNlsU71+" +
                "/qu8ZFNQvdYAkb0gMxy+t8h0PJcIaj0IBow9BB2bPYY8oDwZLCg9wuGNPd4fqj35Khg9UsHcvRWR" +
                "qz2rYde7os8Nvdd0vTsj2ai9Nxx+vJnfmz3LM6Y8uD4RvIhWkzw41Tm9j+1svK0nGj3VUrO9tQVS" +
                "vb6OXjvMFAa+HEUEvj7NEzwGJIa9xDsnPkDyoTwgL929wHkrPdOpoTsPvCg8x9dqvTgSjbwdYJ26" +
                "mKAePdV23T3cn8A9UXWBPQpN0jy/QyW9l0YRPTgx870vf2w9BzDYPRjW17rbWwa9Q0mmOyhODzwq" +
                "IdQ9/KFuvXdxM72qtgg9wOAKvqyIMb14ICg+4JClPKhewbzeMHg9Wsp8PUTKobsNn8C9pS0vvmhh" +
                "Fb0C1Yo9zP1ZPHUmh71RPSU93O/1vLnRP7wYDIi8pRe6vCgvwz3xcO87mherPG9MkTukqfY8pnjy" +
                "PGDQej2T6Ey9g0QhPbDZhr1sWLK9tTu8vQWyVL0Y/YK8caD+vDZakj1Y2468fFoSPXg3Fz2nFz+9" +
                "KBGPPf7bdr1R8J87eG+4PI0qjr1W3+492SALvrvVIDxVNdo8pcOOPZ4ehr2obwm9yekNPVjqPD2E" +
                "QgE9uMVTPMIixT1JC/U8PR9avJjpIj2efOy9TpTGO5ljvLz+1ly9L0d3PYh4Mjy42yC8Qn9RPAjn" +
                "Lb01rgG8");
        //王利
        UserInfo sh0007 = new UserInfo();
        sh0007.setUser_name("wl0001");
        sh0007.setUser_password("123456");
        sh0007.setFaceFeature("AID6RAAAoEHYiYu9DB8zPRgFDbxADs+9GH8mvbYO5j1M/Km9kLsSOw6Fnb1m9508wXhVPbIWczs7" +
                "hvo9f/uAvGG87TzBQse8ZEjpPMfeNT2p2+i8miA8vXC/V73HyfY9K2g8PSkJrL18IlU9FWOQvEDk" +
                "Fj7IqBE8yOwmPNziqL2oUxE+kUCFO8iUDj0W8mC9JsRFPfaFlr1R8yu94bNjPRo6KD52OoC8zK+A" +
                "vc0dqT0Tp6k9k88vvVaVKb1qiYK9fzmivZjV3bt68bg9EWMEvn7HN71/63K9wq6LPWBTUb1vssO7" +
                "KX5uvWwmFz057oS8TjFCPVWwAL1eJ4c9bJArPcHASr3Jfmk9O/7OOpYoAL3CXD6+7IAyvOgxFLxj" +
                "cV89zlVsvQB2hD39YRA9TtflvYzRRzynylq9t63OuzrPfT0Qekq7w9ehPRHc0DtfYMc9ngL1O+Fg" +
                "/jwzOLM9sWBKPcJGYz2Uyd+94RinPRo9jDoxpNk9qIwGvvwHVj34wp+9PsG0PSsYBj7UvpQ9PIFN" +
                "PcrJUj2bn2W9Y2CRu7V7pDymcAm76IlYPMgOrL02sIY8XD7qO+/Uuj2ZADi8rdTpvRZv+7xuf4C9" +
                "wfJHO1h1CT0FAYG9SztmvPg2HTx76re9JL7fvR9A5jyH7dA8Je5fPRErFr0SqzY9xG/nOu1ATj3s" +
                "Nte7VZsZPdciR73AFlS62pVZvfsfeL2gg5M8XlKrvP3Mwjzt+Sm9ByesvelefL3LT+C8iSupve3S" +
                "oL3vJbm6UwIuvZjPErsjZaO95SkJPtApCz1G+wM9097NPfLqjLqY9Dw9fnfZPfSIpL0B6Vs85/iG" +
                "vBkuy7tx+u881ClMPWZQAjyDVpa8opuvvTuyzLzdT8m9jGmGPSDe1r3/t3g9bS9aPGcAQD28nwY8" +
                "AltqvYr6Zj0arjq9EkLMOx6rZTw1kXw9JGH1PPCp/T2bVFe9YOEHPeOxgLyQNcE800GnPFfsnrs2" +
                "cJu9VjB3vW6MPr2ib9y9+Rk2vjvVDbwuZHo9jEjIPCm3fD208oc9TpHNvAo/ubxq5xq8T8nQvL0T" +
                "Qjzzp247mirWPTRh3j2oRDA9tkJFvSGqfDsaaRS92dMTvamMnD0WfAg9mYqHveM/GL3J20u9J9vE" +
                "vWLT3b2F5Y46Y3OVvZBsvbwsKoq9KsoVPbLPRD0/VBS+0OOivEfcbr2EfAu+EDOlPR+/zLy6Br68" +
                "012nvdwkorwBg8099kAQvtNVA72begG+YW/HPb02ljysN6A9YfuWPXnG7juDKRe+Tiq4PYc7iL11" +
                "HKS7w9DwvbYSkLxNTVw9C8BJvXNqlr3rQu66CxfjO/L2sLztmoG9mil9POw2oD0OWB29S25gPSwk" +
                "ojwTOEK9");
        //严杰
        UserInfo sh0008 = new UserInfo();
        sh0008.setUser_name("yj0001");
        sh0008.setUser_password("123456");
        sh0008.setFaceFeature("AID6RAAAoEEj9cA9s45QPSJ2jj1jMpi9wNHlvPWYszxpLa+93bHPPEQYQL1hwdK9wu/KvGL/Xb28" +
                "vh87lNOOPTLvSD3cDYM9pKmQvW9qirzwkfg9seM6vUXD/7zuiww8nXMSvZh4ED0pdpG9JVATPbec" +
                "rb3II5+97C4BPl9rsb0RIa+8OUFxvVDLnD3AMZs83GsevIRMIL2Iu4Q9LGf5vUR3qr3p0uO7+7Ee" +
                "vf+wxj0coYu8GidWPLsHJLxthZQ95PY3vUn0B74nLcW98KQVvqRNqzw2jgS++neCPQAh3ruziiC9" +
                "CRsAvbBJcL2Bx4O8YSi5PUPfEr09nMs9DldlvfI3A71rO0O8eVDJvVvUaL3FaBa8bf6hPcbkuLsB" +
                "Ndm8w/KkPM1LN72fjrQ9XHNMvR4UKT05dAG64HxTvdcQV71FECI+9BazvSZKxLyDhiy9rw6lvfja" +
                "2rwH7i+9seVLvVkb8T236Vk54G3tPahmT709pYQ95+1ZPMeye71FQ+894xKuvNhrRD2acKu9KcNM" +
                "vhIhTL2wMZy9YnlOPd9O2TvXzGY93k94vVWAILzDaxS9ddEwvE5W4T1zudG8a2x2vCvf873MV509" +
                "c7syPQfzqT1Tj648LvhxPTHOv7tppv674TQdPCHonD1DIbA9mr2pvC7Sw7xME6A9dY2BvWWYvbzv" +
                "zL28BltWPdAlaLyvesq9G/CkuwNWJD3ErhE9NG7GvEytkD2kQ8e9FQN7PaQ9Xr0wsha9EmKxvQFL" +
                "PTyCWLY8wV+svePOSb2ubWq9erC0PDv3Bb5q3Jm9vyYIPodEB7787oM9KYeCPPCY4jri0fs8Wv7D" +
                "vCyYVj21ORY8AqvpvbH0VL3+aL68H+KGu9z5CL57Mki9EpsBvbLHpbug09y8b6saPgXpnT2H8hQ8" +
                "at0mPQaKFj1h66+8ZYeBPR29Wz1hrjy8QpT1O4NFBz2x2F+6wkMYPd08qj3Lx5Q87XhQvOgxRT3+" +
                "gQQ83CePu0QkvbukVHK84I6rvC/2PL0DRBw828KevLFpLr3Kow49OePtvaeakj0JWxy+pMY7vOuk" +
                "7LxnsiK9WT1pPaLxVzscmEo9vwVeO7unez2i8sg8FN86PM2sNDwfy8M9myi5vHBUtL2hCN49/CoH" +
                "vVv+fztc3Ba9E+y8PZFOnj0+nGA9yA3LPbNNLr0Ozb28rG18uz4qaD0NrKm8CSxDvXjXBL4ht9i7" +
                "jYw/vYLQAbo3Wfc8YrrZPEVycj2mNxE9ZfxmvBAvcr3OFD+9eRPwu8CjBT3soic7Dd+hPe6zZb1V" +
                "owY99FT7vHU+fDwahYI9Aj2LvYIco7y8MQa+NE6wvapkFz0cUa27PZisvUcKHr3XDuW75I5aPbcX" +
                "5z30Wus8");
        //张强胜
        UserInfo sh0009 = new UserInfo();
        sh0009.setUser_name("zqs0001");
        sh0009.setUser_password("123456");
        sh0009.setFaceFeature("AID6RAAAoEE1bHk8UCI1PC9oE75WCyq9iUc4vLW6/LwPPwg8AmxRuxuFGj4Kym0959OEvQfxWjy7" +
                "V7w8KTn2PKU5wDyBYVq9CVmcPK50gLxkxg6970FlPfUIUL3C4qm89BIkvQhvQj24bC88nuUPvME2" +
                "Jj2A0ys9IKCGuuOqmD0gazg91r/JPQtKXb3Ius+8TeuGO98M1b3ebSG8ecw6PP8gI70N6gm+dMJE" +
                "vEuIIz607ac8MxmKvA3q0z1A2iY9Dli5PGfdAz0yZwY9LssHPjfqA77Idxg9+hjkvMCbzr1dSLQ8" +
                "wz6XvOXXnrxksvs77qbgPAjCMzzjpgC+Kap3ve+kTL6tF9W9gjEBvRTOEr0zNCI94aObOwCM5Ti8" +
                "aF+8XPSJvRzGSzycUc+9JrmRvbkF5D3QpxK96YKTvVn/vz2kQNs8reM6vK/HCL6uObi9nMfVu000" +
                "gbxVv7a921tAvUGmRT3gsUS9o+ODO1ovyr2eayy97gQPvS8JLbvbkJ+8qM1YvVpz8bz3hfu82ezX" +
                "PE/t27wbaFw9cA3KvRjsbr0dQTS95F1+vSajwz0oRhq+QBabPdNss7y+7y89EPoPvVO+qbzpTrG9" +
                "M6DQvK8aQL28ZY08HA0gPjmnoDvr5nY8xME1PMTPdTxqvDk9rdEwvbcw9Lux2nG5nQ0OO8hJjz2b" +
                "gZc9Y3saPVGI/LwAkhs776C+PYdYQD24wco8mbxLPNf3dz23Xry80CU8PZi/jj0FQxE9Hk8MPp9e" +
                "pj3TG0U+d7qqvPgFMzvKGCI9pAwMPRMQIznxkw29EYGjvQhSeD31f389CYfHvPjEWzsAXaC97jij" +
                "PMUFbj2osri8JaelvDr1FT7W5Yg9Zhl+PcFFjT2r1Hm967AhvJMLqbxd3yK9KV2DPSk1rb22WBG9" +
                "v2afvCfbVj07KeM84FIvvZjl/DyuLs68YW6HPRi3ljoiWRc+Iy6wPOstTz1YR3c70ZiAPJ59AT7G" +
                "Axs9SvIjPPcUlr3p5O89DFKqPfYRTz3CvyA9cRZmPHkGGD2ADsa7yalzvLRXnb31SPU8NGW5vQgL" +
                "IT2ZnOA8qJ8zvRXayzz4tkO8BryZuqPxLT3Gz0K9lj0UvQ6NCrxD8uK8a67KPVWnej1omYw9yOYW" +
                "PTRkijzIcqU544dNvZFguT2Rx7K9/susvVp8cL2K3Fg938CqPPVC2zyr+0i9nZW2vcyQhT3sFhc+" +
                "3DT0PCDZ0z2MNGO8NsHiPd6t1r2oAbs7kMEaPWrgIDzeX8g9ALojvUdjMb3jwuy7WASTPTWgED2v" +
                "EJe9wAdCvLiwNbxM0Gu8fWSgPX/6Er30PIo94lIyvsjfbL1C9QE+k7cSveKSVzy3gac9G0DEPaFe" +
                "tL3mPzO9");
        users.add(sh0001);
        users.add(sh0002);
        users.add(sh0003);
        users.add(sh0004);
        users.add(sh0005);
        users.add(sh0006);
        users.add(sh0007);
        users.add(sh0008);
        users.add(sh0009);
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
