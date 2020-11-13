package com.android.terminalbox.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.terminalbox.core.bean.box.Tag;
import com.android.terminalbox.core.bean.user.EpcFile;

import java.util.List;

@Dao
public interface EpcFileDao extends BaseDao<EpcFile> {
    @Query("SELECT * FROM EpcFile ")
    public List<EpcFile> findAllEpcFile();

    @Query("DELETE FROM EpcFile")
    public void deleteAllData();

}
