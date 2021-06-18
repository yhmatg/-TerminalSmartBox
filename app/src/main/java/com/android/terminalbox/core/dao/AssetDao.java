package com.android.terminalbox.core.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.terminalbox.core.bean.cmb.AssetsListItemInfo;

import java.util.List;

@Dao
public interface AssetDao extends BaseDao<AssetsListItemInfo> {
    @Query("SELECT * FROM AssetsListItemInfo ")
    public List<AssetsListItemInfo> findAllAssets();

    @Query("SELECT * FROM AssetsListItemInfo where ast_used_status = :useStatus")
    public List<AssetsListItemInfo> findAssetsByStatus(int useStatus);

    @Query("DELETE FROM AssetsListItemInfo")
    public void deleteAllData();

}
