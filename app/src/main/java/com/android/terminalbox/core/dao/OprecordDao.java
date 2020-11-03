package com.android.terminalbox.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import com.android.terminalbox.core.bean.box.Oprecord;

import java.util.List;

@Dao
public interface OprecordDao extends BaseDao<Oprecord> {
    @Query("SELECT * FROM Oprecord order by id desc Limit 1")
    public List<Oprecord> findOprecords();

    @Query("DELETE FROM Oprecord")
    public void deleteAllData();

}
