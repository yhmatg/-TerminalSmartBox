package com.android.terminalbox.core.bean.user;

import java.io.Serializable;

/**
 * Auto-generated: 2019-03-05 16:34:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class UserInfo implements Serializable {

    /**
     * faceImg : http://172.16.68.110:9000/group1/M00/00/00/rBBEbl-aYIaAVJHfAAB9BjiV874339.png
     * faceStatus : 0
     * flag : 1
     * id : 1
     * password : e10adc3949ba59abbe56e057f20f883e
     * token : code-generator_token_ae2b22d2-2cdb-4140-afed-2f61fc210046
     * username : manager
     */

    private String faceImg;
    private String faceStatus;
    private String flag;
    private int id;
    private String password;
    private String token;
    private String username;

    public String getFaceImg() {
        return faceImg;
    }

    public void setFaceImg(String faceImg) {
        this.faceImg = faceImg;
    }

    public String getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(String faceStatus) {
        this.faceStatus = faceStatus;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}