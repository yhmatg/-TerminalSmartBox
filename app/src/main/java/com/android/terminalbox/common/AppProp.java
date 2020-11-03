package com.android.terminalbox.common;

public class AppProp {
    private static AppProp appProp = null;
    private String uhf_host;
    private String ekey_comPort;
    private int ekey_check_period;
    private int ekey_baudrate;

    public static AppProp getInstance() {
        if (appProp == null) {
            synchronized (AppProp.class) {
                if (appProp == null) {
                    appProp = new AppProp();
                    appProp.init();
                }
            }
        }
        return appProp;
    }
    private void init(){
        uhf_host="172.16.63.220";
        ekey_check_period=1000;
//        ekey_comPort="/dev/ttyMT1";
        ekey_comPort="/dev/ttyS4";
        ekey_baudrate=9600;
    }

    public String getUhf_host() {
        return uhf_host;
    }

    public void setUhf_host(String uhf_host) {
        this.uhf_host = uhf_host;
    }

    public String getEkey_comPort() {
        return ekey_comPort;
    }

    public void setEkey_comPort(String ekey_comPort) {
        this.ekey_comPort = ekey_comPort;
    }

    public int getEkey_check_period() {
        return ekey_check_period;
    }

    public void setEkey_check_period(int ekey_check_period) {
        this.ekey_check_period = ekey_check_period;
    }

    public int getEkey_baudrate() {
        return ekey_baudrate;
    }

    public void setEkey_baudrate(int ekey_baudrate) {
        this.ekey_baudrate = ekey_baudrate;
    }
}
