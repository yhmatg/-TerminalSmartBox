package com.android.terminalbox.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.terminalbox.core.bean.box.Oprecord;
import com.android.terminalbox.core.bean.box.Tag;

import java.util.List;

@Dao
public interface TagDao extends BaseDao<Tag> {
    @Query("SELECT * FROM Tag where op_id = :opId")
    public List<Tag> findTagByOpId(int opId);

    @Query("DELETE FROM Tag WHERE op_id = :opId")
    public void deleteByOpId(int opId);

    @Query("DELETE FROM Oprecord")
    public void deleteAllData();

}
