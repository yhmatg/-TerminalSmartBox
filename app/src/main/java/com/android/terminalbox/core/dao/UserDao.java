package com.android.terminalbox.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.terminalbox.core.bean.user.UserInfo;

import java.util.List;

@Dao
public interface UserDao extends BaseDao<UserInfo> {

}
