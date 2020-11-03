package com.android.terminalbox.core.bean.box;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index("name"), @Index(value = {"user_name"})})
public class UserInfo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "real_name")
    private String realName;

    @ColumnInfo(name = "face_url")
    private String faceUrl;
    @ColumnInfo(name = "feat_points")
    private String featPoints;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getFeatPoints() {
        return featPoints;
    }

    public void setFeatPoints(String featPoints) {
        this.featPoints = featPoints;
    }
}
