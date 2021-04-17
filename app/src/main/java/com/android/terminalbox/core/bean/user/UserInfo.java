package com.android.terminalbox.core.bean.user;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Auto-generated: 2019-03-05 16:34:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class UserInfo {

    /**
     * faceFeature : string1
     * faceImg : string
     * faceStatus : 2
     * flag : 1
     * gmtModified : 1605167665000
     * id : 1
     * user_password : e10adc3949ba59abbe56e057f20f883e
     * token : code-generator_token_ae2b22d2-2cdb-4140-afed-2f61fc210046
     * username : manager
     */
    private String user_name;
    private String user_password;
    private String faceFeature;
    private int managerClient = 1;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(String faceFeature) {
        this.faceFeature = faceFeature;
    }

    public int getManagerClient() {
        return managerClient;
    }

    public void setManagerClient(int managerClient) {
        this.managerClient = managerClient;
    }
}