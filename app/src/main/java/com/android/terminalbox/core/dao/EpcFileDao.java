package com.android.terminalbox.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.terminalbox.core.bean.user.EpcFile;

import java.util.List;

@Dao
public interface EpcFileDao extends BaseDao<EpcFile> {
    @Query("SELECT * FROM EpcFile ")
    public List<EpcFile> findAllEpcFile();

    @Query("SELECT * FROM EpcFile where boxCode = :boxName")
    public List<EpcFile> findEpcFileByBox(String boxName);

    @Query("SELECT * FROM EpcFile where epcCode in (:epcCodes)")
    public List<EpcFile> findEpcFileByEpcs(List<String> epcCodes);

    @Query("DELETE FROM EpcFile")
    public void deleteAllData();

}
