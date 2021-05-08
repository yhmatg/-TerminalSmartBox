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
    private String id;
    private String user_name;
    private String user_real_name;
    private String user_password;
    private String user_mobile;
    private String faceFeature;
    private String managerClient = "13";

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

    public String getManagerClient() {
        return managerClient;
    }

    public void setManagerClient(String managerClient) {
        this.managerClient = managerClient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_real_name() {
        return user_real_name;
    }

    public void setUser_real_name(String user_real_name) {
        this.user_real_name = user_real_name;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }
}