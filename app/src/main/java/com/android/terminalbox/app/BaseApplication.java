package com.android.terminalbox.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.android.terminalbox.BuildConfig;
import com.android.terminalbox.R;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.rs485.RS485Manager;
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
    private RS485Manager rs485Manager;

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

    public RS485Manager getRs485Manager() {
        return rs485Manager;
    }

    public void setRs485Manager(RS485Manager rs485Manager) {
        this.rs485Manager = rs485Manager;
    }
}
