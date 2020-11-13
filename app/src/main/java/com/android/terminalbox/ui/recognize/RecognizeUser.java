package com.android.terminalbox.ui.recognize;


import com.android.terminalbox.core.bean.user.UserInfo;

public class RecognizeUser {
    private float similar;
    private UserInfo user;

    public RecognizeUser(float similar, UserInfo user) {
        this.similar = similar;
        this.user = user;
    }

    public float getSimilar() {
        return similar;
    }

    public void setSimilar(float similar) {
        this.similar = similar;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }
}
