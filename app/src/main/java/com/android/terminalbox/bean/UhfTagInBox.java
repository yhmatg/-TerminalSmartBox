package com.android.terminalbox.bean;


import java.io.Serializable;

public class UhfTagInBox implements Serializable {
    private String epc;
    private String name;

    public UhfTagInBox(String epc, String name) {
        this.epc = epc;
        this.name = name;
    }

    public UhfTagInBox() {
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
