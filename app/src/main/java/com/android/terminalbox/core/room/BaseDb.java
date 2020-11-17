package com.android.terminalbox.core.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.android.terminalbox.app.BaseApplication;
import com.android.terminalbox.core.bean.user.EpcFile;
import com.android.terminalbox.core.bean.user.UserInfo;
import com.android.terminalbox.core.dao.EpcFileDao;
import com.android.terminalbox.core.dao.UserDao;

@Database(entities = {
        UserInfo.class,
        EpcFile.class
}
        , version = 1)
@TypeConverters(DateConverter.class)
public abstract class BaseDb extends RoomDatabase {
    public static final String DB_NAME = "smartbox.db";
    private static volatile BaseDb instance;

    public static synchronized BaseDb getInstance() {
        if (instance == null) {
            instance = createDb();
        }
        return instance;
    }

    private static BaseDb createDb() {
        BaseDb build = Room.databaseBuilder(
                BaseApplication.getInstance(),
                BaseDb.class,
                DB_NAME).addCallback(new Callback() {
            //第一次创建数据库时调用，但是在创建所有表之后调用的
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }

            //当数据库被打开时调用
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }
        }).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        return build;
    }

    public abstract UserDao getUserDao();

    public abstract EpcFileDao getEpcFileDao();
}